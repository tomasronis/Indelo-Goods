package com.indelo.goods.data.repository

import com.indelo.goods.data.model.Order
import com.indelo.goods.data.model.OrderItem
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository {

    private val postgrest = SupabaseClientProvider.client.postgrest

    // Orders
    suspend fun getOrdersByShop(shopId: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val orders = postgrest
                .from("orders")
                .select {
                    filter {
                        eq("shop_id", shopId)
                    }
                }
                .decodeList<Order>()
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersByProducer(producerId: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val orders = postgrest
                .from("orders")
                .select {
                    filter {
                        eq("producer_id", producerId)
                    }
                }
                .decodeList<Order>()
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(id: String): Result<Order?> = withContext(Dispatchers.IO) {
        try {
            val order = postgrest
                .from("orders")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Order>()
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(order: Order): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val createdOrder = postgrest
                .from("orders")
                .insert(order) {
                    select()
                }
                .decodeSingle<Order>()
            Result.success(createdOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val updatedOrder = postgrest
                .from("orders")
                .update(mapOf("status" to status)) {
                    filter {
                        eq("id", orderId)
                    }
                    select()
                }
                .decodeSingle<Order>()
            Result.success(updatedOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Order Items
    suspend fun getOrderItems(orderId: String): Result<List<OrderItem>> = withContext(Dispatchers.IO) {
        try {
            val items = postgrest
                .from("order_items")
                .select {
                    filter {
                        eq("order_id", orderId)
                    }
                }
                .decodeList<OrderItem>()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrderItem(item: OrderItem): Result<OrderItem> = withContext(Dispatchers.IO) {
        try {
            val createdItem = postgrest
                .from("order_items")
                .insert(item) {
                    select()
                }
                .decodeSingle<OrderItem>()
            Result.success(createdItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrderWithItems(order: Order, items: List<OrderItem>): Result<Order> = withContext(Dispatchers.IO) {
        try {
            // Create order first
            val createdOrder = postgrest
                .from("orders")
                .insert(order) {
                    select()
                }
                .decodeSingle<Order>()

            // Then create order items
            val itemsWithOrderId = items.map { it.copy(orderId = createdOrder.id!!) }
            for (item in itemsWithOrderId) {
                postgrest
                    .from("order_items")
                    .insert(item)
            }

            Result.success(createdOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
