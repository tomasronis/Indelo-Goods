package com.indelo.goods.data.repository

import com.indelo.goods.data.model.Product
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {

    private val postgrest = SupabaseClientProvider.client.postgrest

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = postgrest
                .from("products")
                .select()
                .decodeList<Product>()
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Result<Product?> = withContext(Dispatchers.IO) {
        try {
            val product = postgrest
                .from("products")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Product>()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = postgrest
                .from("products")
                .select {
                    filter {
                        eq("category_id", categoryId)
                    }
                }
                .decodeList<Product>()
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByProducer(producerId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = postgrest
                .from("products")
                .select {
                    filter {
                        eq("producer_id", producerId)
                    }
                }
                .decodeList<Product>()
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = postgrest
                .from("products")
                .select {
                    filter {
                        ilike("name", "%$query%")
                    }
                }
                .decodeList<Product>()
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val createdProduct = postgrest
                .from("products")
                .insert(product) {
                    select()
                }
                .decodeSingle<Product>()
            Result.success(createdProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val updatedProduct = postgrest
                .from("products")
                .update(product) {
                    filter {
                        eq("id", product.id!!)
                    }
                    select()
                }
                .decodeSingle<Product>()
            Result.success(updatedProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest
                .from("products")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
