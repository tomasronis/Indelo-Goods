package com.indelo.goods.data.repository

import android.content.Context
import android.net.Uri
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ProductRepository(private val context: Context? = null) {

    private val postgrest = SupabaseClientProvider.client.postgrest
    private val storage = SupabaseClientProvider.client.storage

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

            // Upload to Supabase Storage
            storage.from("products").upload(filePath, bytes, upsert = false)

            // Get public URL
            val publicUrl = storage.from("products").publicUrl(filePath)

            Result.success(publicUrl)
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

            storage.from("products").delete(filePath)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
