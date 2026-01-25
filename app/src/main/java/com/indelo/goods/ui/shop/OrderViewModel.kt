package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Order
import com.indelo.goods.data.model.OrderItem
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderCartItem(
    val product: Product,
    val quantity: Int // Number of cases
) {
    val subtotal: Double
        get() = product.wholesalePrice * quantity
}

data class OrderState(
    val shopId: String? = null,
    val items: List<OrderCartItem> = emptyList(),
    val isPlacingOrder: Boolean = false,
    val orderPlaced: Boolean = false,
    val error: String? = null
) {
    val totalAmount: Double
        get() = items.sumOf { it.subtotal }

    val totalItems: Int
        get() = items.size
}

class OrderViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    fun setShopId(shopId: String) {
        _state.update { it.copy(shopId = shopId) }
    }

    fun addProduct(product: Product) {
        _state.update { currentState ->
            val existingItem = currentState.items.find { it.product.id == product.id }
            val newItems = if (existingItem != null) {
                // Increase quantity
                currentState.items.map {
                    if (it.product.id == product.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }
            } else {
                // Add new item with minimum order quantity
                currentState.items + OrderCartItem(
                    product = product,
                    quantity = product.minimumOrderQuantity
                )
            }
            currentState.copy(items = newItems)
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        _state.update { currentState ->
            val newItems = if (quantity > 0) {
                currentState.items.map {
                    if (it.product.id == productId) {
                        it.copy(quantity = quantity)
                    } else {
                        it
                    }
                }
            } else {
                // Remove item if quantity is 0
                currentState.items.filter { it.product.id != productId }
            }
            currentState.copy(items = newItems)
        }
    }

    fun removeProduct(productId: String) {
        _state.update { currentState ->
            val newItems = currentState.items.filter { it.product.id != productId }
            currentState.copy(items = newItems)
        }
    }

    fun clearCart() {
        _state.update { it.copy(items = emptyList(), orderPlaced = false, error = null) }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentState = _state.value
            val shopId = currentState.shopId

            if (shopId == null) {
                _state.update { it.copy(error = "Shop not selected") }
                return@launch
            }

            if (currentState.items.isEmpty()) {
                _state.update { it.copy(error = "Cart is empty") }
                return@launch
            }

            _state.update { it.copy(isPlacingOrder = true, error = null) }

            // Create the order
            val order = Order(
                shopId = shopId,
                totalAmount = currentState.totalAmount,
                status = "pending"
            )

            // Create order items
            val orderItems = currentState.items.map { cartItem ->
                OrderItem(
                    orderId = "", // Will be set after order is created
                    productId = cartItem.product.id ?: "",
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.product.wholesalePrice,
                    subtotal = cartItem.subtotal,
                    productName = cartItem.product.name,
                    productImageUrl = cartItem.product.imageUrl
                )
            }

            val result = orderRepository.createOrderWithItems(order, orderItems)

            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        isPlacingOrder = false,
                        orderPlaced = true,
                        items = emptyList() // Clear cart on success
                    )
                } else {
                    it.copy(
                        isPlacingOrder = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to place order"
                    )
                }
            }
        }
    }
}
