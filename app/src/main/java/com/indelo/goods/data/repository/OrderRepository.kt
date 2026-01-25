package com.indelo.goods.data.repository

import com.google.gson.Gson
import com.indelo.goods.data.model.Order
import com.indelo.goods.data.model.OrderItem
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository {

    private val api = SupabaseClientProvider.api
    private val gson = Gson()

    // Orders
    suspend fun getOrdersByShop(shopId: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("shop_id" to "eq.$shopId")
            val response = api.select(table = "orders", filters = filters)
            if (response.isSuccessful) {
                val orders = response.body()?.map { gson.fromJson(it, Order::class.java) } ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception("Failed to get orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersByProducer(producerId: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("producer_id" to "eq.$producerId")
            val response = api.select(table = "orders", filters = filters)
            if (response.isSuccessful) {
                val orders = response.body()?.map { gson.fromJson(it, Order::class.java) } ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception("Failed to get orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(id: String): Result<Order?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$id")
            val response = api.select(table = "orders", filters = filters)
            if (response.isSuccessful) {
                val order = response.body()?.firstOrNull()?.let { gson.fromJson(it, Order::class.java) }
                Result.success(order)
            } else {
                Result.failure(Exception("Failed to get order: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(order: Order): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "orders", body = order)
            if (response.isSuccessful) {
                val createdOrder = response.body()?.firstOrNull()?.let { gson.fromJson(it, Order::class.java) }
                if (createdOrder != null) {
                    Result.success(createdOrder)
                } else {
                    Result.failure(Exception("Failed to parse created order"))
                }
            } else {
                Result.failure(Exception("Failed to create order: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$orderId")
            val updates = mapOf("status" to status)
            val response = api.update(table = "orders", body = updates, filters = filters)
            if (response.isSuccessful) {
                val updatedOrder = response.body()?.firstOrNull()?.let { gson.fromJson(it, Order::class.java) }
                if (updatedOrder != null) {
                    Result.success(updatedOrder)
                } else {
                    Result.failure(Exception("Failed to parse updated order"))
                }
            } else {
                Result.failure(Exception("Failed to update order: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Order Items
    suspend fun getOrderItems(orderId: String): Result<List<OrderItem>> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("order_id" to "eq.$orderId")
            val response = api.select(table = "order_items", filters = filters)
            if (response.isSuccessful) {
                val items = response.body()?.map { gson.fromJson(it, OrderItem::class.java) } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to get order items: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrderItem(item: OrderItem): Result<OrderItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "order_items", body = item)
            if (response.isSuccessful) {
                val createdItem = response.body()?.firstOrNull()?.let { gson.fromJson(it, OrderItem::class.java) }
                if (createdItem != null) {
                    Result.success(createdItem)
                } else {
                    Result.failure(Exception("Failed to parse created order item"))
                }
            } else {
                Result.failure(Exception("Failed to create order item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrderWithItems(order: Order, items: List<OrderItem>): Result<Order> = withContext(Dispatchers.IO) {
        try {
            // Create order first
            val orderResponse = api.insert(table = "orders", body = order)
            if (!orderResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to create order: ${orderResponse.message()}"))
            }

            val createdOrder = orderResponse.body()?.firstOrNull()?.let { gson.fromJson(it, Order::class.java) }
                ?: return@withContext Result.failure(Exception("Failed to parse created order"))

            // Then create order items
            val itemsWithOrderId = items.map { it.copy(orderId = createdOrder.id!!) }
            for (item in itemsWithOrderId) {
                api.insert(table = "order_items", body = item)
            }

            Result.success(createdOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
