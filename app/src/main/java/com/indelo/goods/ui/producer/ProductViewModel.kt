package com.indelo.goods.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Product
import com.indelo.goods.data.repository.AuthRepository
import com.indelo.goods.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ProductCreateState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ProductViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListState())
    val listState: StateFlow<ProductListState> = _listState.asStateFlow()

    private val _createState = MutableStateFlow(ProductCreateState())
    val createState: StateFlow<ProductCreateState> = _createState.asStateFlow()

    init {
        loadProducerProducts()
    }

    fun loadProducerProducts() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }

            val producerId = authRepository.currentUserId
            if (producerId == null) {
                _listState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val result = productRepository.getProductsByProducer(producerId)
            _listState.update {
                if (result.isSuccess) {
                    it.copy(
                        products = result.getOrDefault(emptyList()),
                        isLoading = false
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load products"
                    )
                }
            }
        }
    }

    fun createProduct(formState: ProductFormState) {
        viewModelScope.launch {
            _createState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val producerId = authRepository.currentUserId
            if (producerId == null) {
                _createState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val product = formState.toProduct(producerId)
            val result = productRepository.createProduct(product)

            _createState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isSuccess = true)
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to create product"
                    )
                }
            }

            // Refresh the product list
            if (result.isSuccess) {
                loadProducerProducts()
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }

            val result = productRepository.deleteProduct(productId)

            if (result.isSuccess) {
                loadProducerProducts()
            } else {
                _listState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to delete product"
                    )
                }
            }
        }
    }

    fun clearCreateState() {
        _createState.update { ProductCreateState() }
    }

    fun clearError() {
        _listState.update { it.copy(error = null) }
        _createState.update { it.copy(error = null) }
    }
}

// Extension function to convert form state to Product
private fun ProductFormState.toProduct(producerId: String): Product {
    return Product(
        name = name,
        brand = brand.ifBlank { null },
        shortDescription = shortDescription.ifBlank { null },
        description = description.ifBlank { null },
        wholesalePrice = wholesalePrice.toDoubleOrNull() ?: 0.0,
        retailPrice = retailPrice.toDoubleOrNull(),
        unitsPerCase = unitsPerCase.toIntOrNull() ?: 1,
        minimumOrderQuantity = minimumOrderQuantity.toIntOrNull() ?: 1,
        volumeMl = volumeMl.toIntOrNull(),
        weightG = weightG.toIntOrNull(),
        servingSize = servingSize.ifBlank { null },
        servingsPerContainer = servingsPerContainer.toIntOrNull(),
        shelfLifeDays = shelfLifeDays.toIntOrNull(),
        countryOfOrigin = countryOfOrigin.ifBlank { null },
        storageInstructions = storageInstructions.ifBlank { null },
        ingredients = ingredients.ifBlank { null },
        allergens = allergens.ifBlank { null },
        isOrganic = isOrganic,
        isNonGmo = isNonGmo,
        isVegan = isVegan,
        isGlutenFree = isGlutenFree,
        isKosher = isKosher,
        sku = sku.ifBlank { null },
        upc = upc.ifBlank { null },
        leadTimeDays = leadTimeDays.toIntOrNull(),
        producerId = producerId
    )
}
