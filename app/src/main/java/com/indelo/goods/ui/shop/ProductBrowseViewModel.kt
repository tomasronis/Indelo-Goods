package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.ai.AISearchService
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
    val allProducts: List<Product> = emptyList(), // Store all products for search
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class ProductBrowseViewModel(
    private val shopRepository: ShopRepository = ShopRepository(),
    private val productRepository: ProductRepository = ProductRepository(),
    private val aiSearchService: AISearchService = AISearchService()
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
                    val products = productsResult.getOrDefault(emptyList())
                    it.copy(
                        shop = shop,
                        products = products,
                        allProducts = products, // Store all products for search
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

    fun searchWithAI(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, searchQuery = query) }

            val allProducts = _state.value.allProducts
            val result = aiSearchService.searchProducts(query, allProducts)

            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        products = result.getOrDefault(allProducts),
                        isSearching = false
                    )
                } else {
                    it.copy(
                        products = allProducts, // Show all on error
                        isSearching = false,
                        error = "Search failed, showing all products"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                products = it.allProducts,
                searchQuery = ""
            )
        }
    }
}
