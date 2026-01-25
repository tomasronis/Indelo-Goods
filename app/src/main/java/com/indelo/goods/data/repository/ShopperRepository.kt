package com.indelo.goods.data.repository

import com.indelo.goods.data.model.MonthlyProductSelection
import com.indelo.goods.data.model.ShopperPreferences
import com.indelo.goods.data.model.ShopperSubscription
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopperRepository {

    private val postgrest = SupabaseClientProvider.client.postgrest

    // Preferences
    suspend fun getPreferences(userId: String): Result<ShopperPreferences?> = withContext(Dispatchers.IO) {
        try {
            val preferences = postgrest
                .from("shopper_preferences")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<ShopperPreferences>()
            Result.success(preferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePreferences(preferences: ShopperPreferences): Result<ShopperPreferences> = withContext(Dispatchers.IO) {
        try {
            val savedPreferences = postgrest
                .from("shopper_preferences")
                .upsert(preferences) {
                    select()
                }
                .decodeSingle<ShopperPreferences>()
            Result.success(savedPreferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Subscriptions
    suspend fun getSubscription(userId: String): Result<ShopperSubscription?> = withContext(Dispatchers.IO) {
        try {
            val subscription = postgrest
                .from("shopper_subscriptions")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<ShopperSubscription>()
            Result.success(subscription)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSubscription(subscription: ShopperSubscription): Result<ShopperSubscription> = withContext(Dispatchers.IO) {
        try {
            val createdSubscription = postgrest
                .from("shopper_subscriptions")
                .insert(subscription) {
                    select()
                }
                .decodeSingle<ShopperSubscription>()
            Result.success(createdSubscription)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubscription(subscription: ShopperSubscription): Result<ShopperSubscription> = withContext(Dispatchers.IO) {
        try {
            val updatedSubscription = postgrest
                .from("shopper_subscriptions")
                .update(subscription) {
                    filter {
                        eq("id", subscription.id!!)
                    }
                    select()
                }
                .decodeSingle<ShopperSubscription>()
            Result.success(updatedSubscription)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Monthly Product Selections
    suspend fun getMonthlySelections(subscriptionId: String, month: String): Result<List<MonthlyProductSelection>> = withContext(Dispatchers.IO) {
        try {
            val selections = postgrest
                .from("monthly_product_selections")
                .select {
                    filter {
                        eq("subscription_id", subscriptionId)
                        eq("month", month)
                    }
                }
                .decodeList<MonthlyProductSelection>()
            Result.success(selections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMonthlySelection(selection: MonthlyProductSelection): Result<MonthlyProductSelection> = withContext(Dispatchers.IO) {
        try {
            val createdSelection = postgrest
                .from("monthly_product_selections")
                .insert(selection) {
                    select()
                }
                .decodeSingle<MonthlyProductSelection>()
            Result.success(createdSelection)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun redeemSelection(selectionId: String): Result<MonthlyProductSelection> = withContext(Dispatchers.IO) {
        try {
            val updatedSelection = postgrest
                .from("monthly_product_selections")
                .update(mapOf("redeemed" to true, "redeemed_at" to System.currentTimeMillis().toString())) {
                    filter {
                        eq("id", selectionId)
                    }
                    select()
                }
                .decodeSingle<MonthlyProductSelection>()
            Result.success(updatedSelection)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
