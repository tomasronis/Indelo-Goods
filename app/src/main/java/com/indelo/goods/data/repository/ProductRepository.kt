package com.indelo.goods.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class ProductRepository(private val context: Context? = null) {

    private val api = SupabaseClientProvider.api
    private val gson = Gson()

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = api.select(table = "products")
            if (response.isSuccessful) {
                val products = response.body()?.map { gson.fromJson(it, Product::class.java) } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to get products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Result<Product?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$id")
            val response = api.select(table = "products", filters = filters)
            if (response.isSuccessful) {
                val product = response.body()?.firstOrNull()?.let { gson.fromJson(it, Product::class.java) }
                Result.success(product)
            } else {
                Result.failure(Exception("Failed to get product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("category_id" to "eq.$categoryId")
            val response = api.select(table = "products", filters = filters)
            if (response.isSuccessful) {
                val products = response.body()?.map { gson.fromJson(it, Product::class.java) } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to get products by category: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByProducer(producerId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("producer_id" to "eq.$producerId")
            val response = api.select(table = "products", filters = filters)
            if (response.isSuccessful) {
                val products = response.body()?.map { gson.fromJson(it, Product::class.java) } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to get products by producer: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("name" to "ilike.*$query*")
            val response = api.select(table = "products", filters = filters)
            if (response.isSuccessful) {
                val products = response.body()?.map { gson.fromJson(it, Product::class.java) } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to search products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get products available for a specific shop based on city and shop ID
     * A product is available if:
     * - availableCities contains the shop's city
     * - availableShopIds contains the shop's ID
     * - Both lists are null (available to all)
     */
    suspend fun getProductsForShop(shopId: String, city: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Fetch all products (PostgREST doesn't support array filtering easily, so we filter in-app)
            val response = api.select(table = "products")
            if (response.isSuccessful) {
                val allProducts = response.body()?.map { gson.fromJson(it, Product::class.java) } ?: emptyList()

                // Filter products based on shop location and shop-specific availability
                val availableProducts = allProducts.filter { product ->
                    val isAvailableByCity = product.availableCities == null ||
                        product.availableCities.contains(city)

                    val isAvailableByShop = product.availableShopIds == null ||
                        product.availableShopIds.contains(shopId)

                    isAvailableByCity && isAvailableByShop
                }

                Result.success(availableProducts)
            } else {
                Result.failure(Exception("Failed to get products for shop: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "products", body = product)
            if (response.isSuccessful) {
                val createdProduct = response.body()?.firstOrNull()?.let { gson.fromJson(it, Product::class.java) }
                    ?: return@withContext Result.failure(Exception("No product returned"))
                Result.success(createdProduct)
            } else {
                Result.failure(Exception("Failed to create product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.${product.id}")
            val response = api.update(table = "products", body = product, filters = filters)
            if (response.isSuccessful) {
                val updatedProduct = response.body()?.firstOrNull()?.let { gson.fromJson(it, Product::class.java) }
                    ?: return@withContext Result.failure(Exception("No product returned"))
                Result.success(updatedProduct)
            } else {
                Result.failure(Exception("Failed to update product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$id")
            val response = api.delete(table = "products", filters = filters)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads a product image to Supabase Storage and returns the public URL
     * @param imageUri The URI of the image to upload
     * @param productId Optional product ID for naming the file (uses UUID if null)
     * @return Result containing the public URL of the uploaded image
     */
    suspend fun uploadProductImage(imageUri: Uri, productId: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (context == null) {
                return@withContext Result.failure(Exception("Context is required for image upload"))
            }

            // Read image bytes from URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(Exception("Could not read image"))

            val bytes = inputStream.readBytes()
            inputStream.close()

            // Generate unique filename
            val fileExtension = context.contentResolver.getType(imageUri)?.substringAfter("/") ?: "jpg"
            val fileName = "${productId ?: UUID.randomUUID()}_${System.currentTimeMillis()}.$fileExtension"
            val filePath = "product-images/$fileName"

            // Create request body
            val contentType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(contentType.toMediaTypeOrNull())

            // Upload to Supabase Storage
            val response = api.uploadFile(
                bucket = "products",
                path = filePath,
                file = requestBody
            )

            if (response.isSuccessful) {
                // Construct public URL
                val supabaseUrl = com.indelo.goods.data.supabase.SupabaseConfig.SUPABASE_URL
                val publicUrl = "$supabaseUrl/storage/v1/object/public/products/$filePath"
                Result.success(publicUrl)
            } else {
                Result.failure(Exception("Failed to upload image: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a product image from Supabase Storage
     * @param imageUrl The public URL of the image to delete
     */
    suspend fun deleteProductImage(imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Extract file path from public URL
            // URL format: https://<project>.supabase.co/storage/v1/object/public/products/<file-path>
            val filePath = imageUrl.substringAfter("/products/")

            val response = api.deleteFile(bucket = "products", path = filePath)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete image: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
