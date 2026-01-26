package com.indelo.goods.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

/**
 * Shopper preferences for category interests and notifications
 */
@Serializable
data class ShopperPreferences(
    @SerialName("id")
    @SerializedName("id")
    val id: String? = null,

    @SerialName("user_id")
    @SerializedName("user_id")
    val userId: String,

    @SerialName("favorite_categories")
    @SerializedName("favorite_categories")
    val favoriteCategories: List<String> = emptyList(),

    @SerialName("notifications_enabled")
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = false,

    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Shopper subscription for $49/month plan
 * Includes 3 product trials per month
 */
@Serializable
data class ShopperSubscription(
    @SerialName("id")
    @SerializedName("id")
    val id: String? = null,

    @SerialName("user_id")
    @SerializedName("user_id")
    val userId: String,

    @SerialName("status")
    @SerializedName("status")
    val status: String, // active, cancelled, past_due, trial

    @SerialName("stripe_subscription_id")
    @SerializedName("stripe_subscription_id")
    val stripeSubscriptionId: String? = null,

    @SerialName("current_period_start")
    @SerializedName("current_period_start")
    val currentPeriodStart: String? = null,

    @SerialName("current_period_end")
    @SerializedName("current_period_end")
    val currentPeriodEnd: String? = null,

    @SerialName("products_used_this_month")
    @SerializedName("products_used_this_month")
    val productsUsedThisMonth: Int = 0, // Max 3 per month

    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Monthly product selection (3 products shopper wants to try)
 */
@Serializable
data class MonthlyProductSelection(
    @SerialName("id")
    @SerializedName("id")
    val id: String? = null,

    @SerialName("subscription_id")
    @SerializedName("subscription_id")
    val subscriptionId: String,

    @SerialName("product_id")
    @SerializedName("product_id")
    val productId: String,

    @SerialName("shop_id")
    @SerializedName("shop_id")
    val shopId: String?, // Where they want to pick it up

    @SerialName("month")
    @SerializedName("month")
    val month: String, // Format: YYYY-MM

    @SerialName("redeemed")
    @SerializedName("redeemed")
    val redeemed: Boolean = false,

    @SerialName("redeemed_at")
    @SerializedName("redeemed_at")
    val redeemedAt: String? = null,

    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null
)
