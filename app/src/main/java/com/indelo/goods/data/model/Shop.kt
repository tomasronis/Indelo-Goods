package com.indelo.goods.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

@Serializable
data class Shop(
    val id: String? = null,
    val name: String,
    val description: String? = null,

    // Owner
    @SerialName("owner_id")
    @SerializedName("owner_id")
    val ownerId: String,

    // Location
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerialName("zip_code")
    @SerializedName("zip_code")
    val zipCode: String? = null,
    val country: String? = null,
    val region: String? = null, // Geographic region for product availability

    // Contact
    val phone: String? = null,
    val email: String? = null,

    // Business Info
    @SerialName("business_type")
    @SerializedName("business_type")
    val businessType: String? = null, // e.g., "Cafe", "Restaurant", "Retail Store"
    @SerialName("tax_id")
    @SerializedName("tax_id")
    val taxId: String? = null,

    // Metadata
    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class Order(
    val id: String? = null,

    // Relationships
    @SerialName("shop_id")
    @SerializedName("shop_id")
    val shopId: String,
    @SerialName("producer_id")
    @SerializedName("producer_id")
    val producerId: String? = null,

    // Order Info
    val status: String = "pending", // pending, confirmed, shipped, delivered, cancelled
    @SerialName("total_amount")
    @SerializedName("total_amount")
    val totalAmount: Double,
    val currency: String = "USD",

    // Shipping
    @SerialName("shipping_address")
    @SerializedName("shipping_address")
    val shippingAddress: String? = null,
    @SerialName("shipping_status")
    @SerializedName("shipping_status")
    val shippingStatus: String? = null,
    @SerialName("tracking_number")
    @SerializedName("tracking_number")
    val trackingNumber: String? = null,

    // Notes
    val notes: String? = null,

    // Metadata
    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerialName("fulfilled_at")
    @SerializedName("fulfilled_at")
    val fulfilledAt: String? = null
)

@Serializable
data class OrderItem(
    val id: String? = null,

    // Relationships
    @SerialName("order_id")
    @SerializedName("order_id")
    val orderId: String,
    @SerialName("product_id")
    @SerializedName("product_id")
    val productId: String,

    // Item Details
    val quantity: Int,
    @SerialName("unit_price")
    @SerializedName("unit_price")
    val unitPrice: Double, // Price at time of order
    val subtotal: Double, // quantity * unit_price

    // Product Info (cached for historical reference)
    @SerialName("product_name")
    @SerializedName("product_name")
    val productName: String? = null,
    @SerialName("product_image_url")
    @SerializedName("product_image_url")
    val productImageUrl: String? = null,

    // Metadata
    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null
)

// Regions for product availability
object Regions {
    const val NORTHEAST = "Northeast"
    const val SOUTHEAST = "Southeast"
    const val MIDWEST = "Midwest"
    const val SOUTHWEST = "Southwest"
    const val WEST = "West"
    const val NATIONAL = "National"

    val ALL = listOf(NORTHEAST, SOUTHEAST, MIDWEST, SOUTHWEST, WEST, NATIONAL)
}
