package com.indelo.goods.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Shopper preferences for category interests and notifications
 */
@Serializable
data class ShopperPreferences(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("favorite_categories")
    val favoriteCategories: List<String> = emptyList(),

    @SerialName("notifications_enabled")
    val notificationsEnabled: Boolean = false,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Shopper subscription for $49/month plan
 * Includes 3 product trials per month
 */
@Serializable
data class ShopperSubscription(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("status")
    val status: String, // active, cancelled, past_due, trial

    @SerialName("stripe_subscription_id")
    val stripeSubscriptionId: String? = null,

    @SerialName("current_period_start")
    val currentPeriodStart: String? = null,

    @SerialName("current_period_end")
    val currentPeriodEnd: String? = null,

    @SerialName("products_used_this_month")
    val productsUsedThisMonth: Int = 0, // Max 3 per month

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Monthly product selection (3 products shopper wants to try)
 */
@Serializable
data class MonthlyProductSelection(
    @SerialName("id")
    val id: String? = null,

    @SerialName("subscription_id")
    val subscriptionId: String,

    @SerialName("product_id")
    val productId: String,

    @SerialName("shop_id")
    val shopId: String?, // Where they want to pick it up

    @SerialName("month")
    val month: String, // Format: YYYY-MM

    @SerialName("redeemed")
    val redeemed: Boolean = false,

    @SerialName("redeemed_at")
    val redeemedAt: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)
