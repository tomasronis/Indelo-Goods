package com.indelo.goods.data.ai

import com.indelo.goods.BuildConfig
import com.indelo.goods.data.model.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AISearchService {
    private val anthropicApi: AnthropicApi

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        anthropicApi = retrofit.create(AnthropicApi::class.java)
    }

    suspend fun searchProducts(query: String, products: List<Product>): Result<List<Product>> {
        if (query.isBlank()) {
            return Result.success(products)
        }

        return try {
            // Create a simplified product list for Claude to analyze
            val productSummaries = products.mapIndexed { index, product ->
                """
                Product ${index}:
                - Name: ${product.name}
                - Brand: ${product.brand ?: "N/A"}
                - Description: ${product.description ?: "N/A"}
                - Tags: ${product.tags?.joinToString(", ") ?: "N/A"}
                - Organic: ${product.isOrganic}
                - Vegan: ${product.isVegan}
                - Gluten-free: ${product.isGlutenFree}
                - Non-GMO: ${product.isNonGmo}
                - Kosher: ${product.isKosher}
                """.trimIndent()
            }.joinToString("\n\n")

            val prompt = """
                You are helping a shop owner find products. Given the user's search query and a list of products,
                return ONLY the numbers (0-indexed) of products that match the query, separated by commas.

                User query: "$query"

                Products:
                $productSummaries

                Return ONLY the matching product numbers as a comma-separated list (e.g., "0,2,5").
                If no products match, return "NONE".
                If the query is too vague or matches everything, return "ALL".
            """.trimIndent()

            val request = AnthropicRequest(
                model = "claude-sonnet-4-20250514",
                max_tokens = 500,
                messages = listOf(
                    AnthropicMessage(
                        role = "user",
                        content = prompt
                    )
                )
            )

            val response = anthropicApi.createMessage(
                apiKey = BuildConfig.ANTHROPIC_API_KEY,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val aiResponse = response.body()!!
                val resultText = aiResponse.content.firstOrNull()?.text?.trim() ?: ""

                val filteredProducts = when {
                    resultText == "NONE" -> emptyList()
                    resultText == "ALL" -> products
                    else -> {
                        val indices = resultText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .filter { it in products.indices }
                        indices.map { products[it] }
                    }
                }

                Result.success(filteredProducts)
            } else {
                // Fallback to simple text search if API fails
                Result.success(simpleTextSearch(query, products))
            }
        } catch (e: Exception) {
            // Fallback to simple text search on error
            Result.success(simpleTextSearch(query, products))
        }
    }

    private fun simpleTextSearch(query: String, products: List<Product>): List<Product> {
        return products.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.brand?.contains(query, ignoreCase = true) == true ||
            product.description?.contains(query, ignoreCase = true) == true ||
            product.tags?.any { it.contains(query, ignoreCase = true) } == true
        }
    }
}
