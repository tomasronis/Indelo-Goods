package com.indelo.goods.data.repository

import com.indelo.goods.data.model.Shop
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository {

    private val postgrest = SupabaseClientProvider.client.postgrest

    suspend fun getShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val shops = postgrest
                .from("shops")
                .select()
                .decodeList<Shop>()
            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopsByOwner(ownerId: String): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val shops = postgrest
                .from("shops")
                .select {
                    filter {
                        eq("owner_id", ownerId)
                    }
                }
                .decodeList<Shop>()
            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopById(id: String): Result<Shop?> = withContext(Dispatchers.IO) {
        try {
            val shop = postgrest
                .from("shops")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Shop>()
            Result.success(shop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createShop(shop: Shop): Result<Shop> = withContext(Dispatchers.IO) {
        try {
            val createdShop = postgrest
                .from("shops")
                .insert(shop) {
                    select()
                }
                .decodeSingle<Shop>()
            Result.success(createdShop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateShop(shop: Shop): Result<Shop> = withContext(Dispatchers.IO) {
        try {
            val updatedShop = postgrest
                .from("shops")
                .update(shop) {
                    filter {
                        eq("id", shop.id!!)
                    }
                    select()
                }
                .decodeSingle<Shop>()
            Result.success(updatedShop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteShop(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest
                .from("shops")
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
