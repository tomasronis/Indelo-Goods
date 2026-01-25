package com.indelo.goods.data.repository

import com.google.gson.Gson
import com.indelo.goods.data.model.MonthlyProductSelection
import com.indelo.goods.data.model.ShopperPreferences
import com.indelo.goods.data.model.ShopperSubscription
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopperRepository {

    private val api = SupabaseClientProvider.api
    private val gson = Gson()

    // Preferences
    suspend fun getPreferences(userId: String): Result<ShopperPreferences?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("user_id" to "eq.$userId")
            val response = api.select(table = "shopper_preferences", filters = filters)
            if (response.isSuccessful) {
                val preferences = response.body()?.firstOrNull()?.let { gson.fromJson(it, ShopperPreferences::class.java) }
                Result.success(preferences)
            } else {
                Result.failure(Exception("Failed to get preferences: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePreferences(preferences: ShopperPreferences): Result<ShopperPreferences> = withContext(Dispatchers.IO) {
        try {
            // Try to get existing preferences first
            val existing = getPreferences(preferences.userId).getOrNull()

            val response = if (existing != null) {
                // Update existing
                val filters = mapOf("user_id" to "eq.${preferences.userId}")
                api.update(table = "shopper_preferences", body = preferences, filters = filters)
            } else {
                // Insert new
                api.insert(table = "shopper_preferences", body = preferences)
            }

            if (response.isSuccessful) {
                val savedPreferences = response.body()?.firstOrNull()?.let { gson.fromJson(it, ShopperPreferences::class.java) }
                if (savedPreferences != null) {
                    Result.success(savedPreferences)
                } else {
                    Result.failure(Exception("Failed to parse saved preferences"))
                }
            } else {
                Result.failure(Exception("Failed to save preferences: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Subscriptions
    suspend fun getSubscription(userId: String): Result<ShopperSubscription?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("user_id" to "eq.$userId")
            val response = api.select(table = "shopper_subscriptions", filters = filters)
            if (response.isSuccessful) {
                val subscription = response.body()?.firstOrNull()?.let { gson.fromJson(it, ShopperSubscription::class.java) }
                Result.success(subscription)
            } else {
                Result.failure(Exception("Failed to get subscription: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSubscription(subscription: ShopperSubscription): Result<ShopperSubscription> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "shopper_subscriptions", body = subscription)
            if (response.isSuccessful) {
                val createdSubscription = response.body()?.firstOrNull()?.let { gson.fromJson(it, ShopperSubscription::class.java) }
                if (createdSubscription != null) {
                    Result.success(createdSubscription)
                } else {
                    Result.failure(Exception("Failed to parse created subscription"))
                }
            } else {
                Result.failure(Exception("Failed to create subscription: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubscription(subscription: ShopperSubscription): Result<ShopperSubscription> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.${subscription.id!!}")
            val response = api.update(table = "shopper_subscriptions", body = subscription, filters = filters)
            if (response.isSuccessful) {
                val updatedSubscription = response.body()?.firstOrNull()?.let { gson.fromJson(it, ShopperSubscription::class.java) }
                if (updatedSubscription != null) {
                    Result.success(updatedSubscription)
                } else {
                    Result.failure(Exception("Failed to parse updated subscription"))
                }
            } else {
                Result.failure(Exception("Failed to update subscription: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Monthly Product Selections
    suspend fun getMonthlySelections(subscriptionId: String, month: String): Result<List<MonthlyProductSelection>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf(
                "subscription_id" to "eq.$subscriptionId",
                "month" to "eq.$month"
            )
            val response = api.select(table = "monthly_product_selections", filters = filters)
            if (response.isSuccessful) {
                val selections = response.body()?.map { gson.fromJson(it, MonthlyProductSelection::class.java) } ?: emptyList()
                Result.success(selections)
            } else {
                Result.failure(Exception("Failed to get monthly selections: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMonthlySelection(selection: MonthlyProductSelection): Result<MonthlyProductSelection> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "monthly_product_selections", body = selection)
            if (response.isSuccessful) {
                val createdSelection = response.body()?.firstOrNull()?.let { gson.fromJson(it, MonthlyProductSelection::class.java) }
                if (createdSelection != null) {
                    Result.success(createdSelection)
                } else {
                    Result.failure(Exception("Failed to parse created selection"))
                }
            } else {
                Result.failure(Exception("Failed to add monthly selection: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun redeemSelection(selectionId: String): Result<MonthlyProductSelection> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$selectionId")
            val updates = mapOf(
                "redeemed" to true,
                "redeemed_at" to System.currentTimeMillis().toString()
            )
            val response = api.update(table = "monthly_product_selections", body = updates, filters = filters)
            if (response.isSuccessful) {
                val updatedSelection = response.body()?.firstOrNull()?.let { gson.fromJson(it, MonthlyProductSelection::class.java) }
                if (updatedSelection != null) {
                    Result.success(updatedSelection)
                } else {
                    Result.failure(Exception("Failed to parse redeemed selection"))
                }
            } else {
                Result.failure(Exception("Failed to redeem selection: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
