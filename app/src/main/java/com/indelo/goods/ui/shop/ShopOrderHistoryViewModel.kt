package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Order
import com.indelo.goods.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopOrderHistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ShopOrderHistoryViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ShopOrderHistoryState())
    val state: StateFlow<ShopOrderHistoryState> = _state.asStateFlow()

    fun loadOrdersForShop(shopId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = orderRepository.getOrdersByShop(shopId)

            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        orders = result.getOrDefault(emptyList())
                            .sortedByDescending { order -> order.createdAt },
                        isLoading = false
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load orders"
                    )
                }
            }
        }
    }
}
