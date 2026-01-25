package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.model.Shop
import com.indelo.goods.data.repository.ProductRepository
import com.indelo.goods.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductBrowseState(
    val shop: Shop? = null,
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductBrowseViewModel(
    private val shopRepository: ShopRepository = ShopRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ProductBrowseState())
    val state: StateFlow<ProductBrowseState> = _state.asStateFlow()

    fun loadProductsForShop(shopId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // First, load the shop to get its city
            val shopResult = shopRepository.getShopById(shopId)
            if (shopResult.isFailure || shopResult.getOrNull() == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Shop not found"
                    )
                }
                return@launch
            }

            val shop = shopResult.getOrNull()!!
            val city = shop.city ?: ""

            // Load products available for this shop
            val productsResult = productRepository.getProductsForShop(shopId, city)

            _state.update {
                if (productsResult.isSuccess) {
                    it.copy(
                        shop = shop,
                        products = productsResult.getOrDefault(emptyList()),
                        isLoading = false
                    )
                } else {
                    it.copy(
                        shop = shop,
                        isLoading = false,
                        error = productsResult.exceptionOrNull()?.message ?: "Failed to load products"
                    )
                }
            }
        }
    }
}
