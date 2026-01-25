package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Order
import com.indelo.goods.data.model.OrderItem
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.model.Shop
import com.indelo.goods.data.repository.OrderRepository
import com.indelo.goods.data.repository.ShopRepository
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
    val shop: Shop? = null,
    val items: List<OrderCartItem> = emptyList(),
    val deliveryAddress: String = "",
    val notes: String = "",
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
    private val orderRepository: OrderRepository = OrderRepository(),
    private val shopRepository: ShopRepository = ShopRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    fun setShopId(shopId: String) {
        _state.update { it.copy(shopId = shopId) }
        loadShop(shopId)
    }

    private fun loadShop(shopId: String) {
        viewModelScope.launch {
            val result = shopRepository.getShopById(shopId)
            if (result.isSuccess) {
                val shop = result.getOrNull()
                _state.update { currentState ->
                    // Pre-populate delivery address with shop's address
                    val defaultAddress = buildString {
                        shop?.address?.let { append(it).append("\n") }
                        shop?.city?.let { append(it) }
                        shop?.state?.let { if (shop.city != null) append(", ") else append(""); append(it) }
                        shop?.zipCode?.let { append(" ").append(it) }
                    }.trim()

                    currentState.copy(
                        shop = shop,
                        deliveryAddress = if (defaultAddress.isNotBlank()) defaultAddress else currentState.deliveryAddress
                    )
                }
            }
        }
    }

    fun updateDeliveryAddress(address: String) {
        _state.update { it.copy(deliveryAddress = address) }
    }

    fun updateNotes(notes: String) {
        _state.update { it.copy(notes = notes) }
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

            if (currentState.deliveryAddress.isBlank()) {
                _state.update { it.copy(error = "Delivery address is required") }
                return@launch
            }

            _state.update { it.copy(isPlacingOrder = true, error = null) }

            // Get producer_id from first product (assuming all products in order are from same producer)
            val producerId = currentState.items.firstOrNull()?.product?.producerId

            // Create the order
            val order = Order(
                shopId = shopId,
                producerId = producerId,
                totalAmount = currentState.totalAmount,
                status = "pending",
                shippingAddress = currentState.deliveryAddress,
                notes = currentState.notes.ifBlank { null }
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
