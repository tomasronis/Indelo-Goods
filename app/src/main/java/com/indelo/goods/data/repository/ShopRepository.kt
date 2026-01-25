package com.indelo.goods.data.repository

import com.google.gson.Gson
import com.indelo.goods.data.model.Shop
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository {

    private val api = SupabaseClientProvider.api
    private val gson = Gson()

    suspend fun getShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val response = api.select(table = "shops")
            if (response.isSuccessful) {
                val shops = response.body()?.map { gson.fromJson(it, Shop::class.java) } ?: emptyList()
                Result.success(shops)
            } else {
                Result.failure(Exception("Failed to get shops: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopsByOwner(ownerId: String): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("owner_id" to "eq.$ownerId")
            val response = api.select(table = "shops", filters = filters)
            if (response.isSuccessful) {
                val shops = response.body()?.map { gson.fromJson(it, Shop::class.java) } ?: emptyList()
                Result.success(shops)
            } else {
                Result.failure(Exception("Failed to get shops: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopById(id: String): Result<Shop?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$id")
            val response = api.select(table = "shops", filters = filters)
            if (response.isSuccessful) {
                val shop = response.body()?.firstOrNull()?.let { gson.fromJson(it, Shop::class.java) }
                Result.success(shop)
            } else {
                Result.failure(Exception("Failed to get shop: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createShop(shop: Shop): Result<Shop> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "shops", body = shop)
            if (response.isSuccessful) {
                val createdShop = response.body()?.firstOrNull()?.let { gson.fromJson(it, Shop::class.java) }
                if (createdShop != null) {
                    Result.success(createdShop)
                } else {
                    Result.failure(Exception("Failed to parse created shop"))
                }
            } else {
                Result.failure(Exception("Failed to create shop: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateShop(shop: Shop): Result<Shop> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.${shop.id!!}")
            val response = api.update(table = "shops", body = shop, filters = filters)
            if (response.isSuccessful) {
                val updatedShop = response.body()?.firstOrNull()?.let { gson.fromJson(it, Shop::class.java) }
                if (updatedShop != null) {
                    Result.success(updatedShop)
                } else {
                    Result.failure(Exception("Failed to parse updated shop"))
                }
            } else {
                Result.failure(Exception("Failed to update shop: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteShop(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$id")
            val response = api.delete(table = "shops", filters = filters)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete shop: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
