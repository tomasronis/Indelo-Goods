package com.indelo.goods.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Order
import com.indelo.goods.data.model.OrderItem
import com.indelo.goods.data.repository.AuthRepository
import com.indelo.goods.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderWithItems(
    val order: Order,
    val items: List<OrderItem>
)

data class ProducerOrdersState(
    val orders: List<OrderWithItems> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val updatingOrderId: String? = null
)

class ProducerOrdersViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ProducerOrdersState())
    val state: StateFlow<ProducerOrdersState> = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val producerId = authRepository.currentUserId
            if (producerId == null) {
                _state.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val ordersResult = orderRepository.getOrdersByProducer(producerId)

            if (ordersResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = ordersResult.exceptionOrNull()?.message ?: "Failed to load orders"
                    )
                }
                return@launch
            }

            val orders = ordersResult.getOrDefault(emptyList())

            // Load items for each order
            val ordersWithItems = mutableListOf<OrderWithItems>()
            for (order in orders) {
                val itemsResult = orderRepository.getOrderItems(order.id ?: "")
                val items = itemsResult.getOrDefault(emptyList())
                ordersWithItems.add(OrderWithItems(order, items))
            }

            _state.update {
                it.copy(
                    orders = ordersWithItems.sortedByDescending { owi -> owi.order.createdAt },
                    isLoading = false
                )
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            _state.update { it.copy(updatingOrderId = orderId) }

            val result = orderRepository.updateOrderStatus(orderId, newStatus)

            if (result.isSuccess) {
                // Refresh orders
                loadOrders()
            } else {
                _state.update {
                    it.copy(
                        updatingOrderId = null,
                        error = result.exceptionOrNull()?.message ?: "Failed to update order status"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
