package com.indelo.goods.ui.producer

import android.content.Context
import android.net.Uri
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

data class ProductEditState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ProductViewModel(
    private val context: Context? = null,
    private val productRepository: ProductRepository = context?.let { ProductRepository(it) } ?: ProductRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListState())
    val listState: StateFlow<ProductListState> = _listState.asStateFlow()

    private val _createState = MutableStateFlow(ProductCreateState())
    val createState: StateFlow<ProductCreateState> = _createState.asStateFlow()

    private val _editState = MutableStateFlow(ProductEditState())
    val editState: StateFlow<ProductEditState> = _editState.asStateFlow()

    init {
        android.util.Log.d("ProductViewModel", "Initializing ProductViewModel, context=$context")
        loadProducerProducts()
    }

    fun loadProducerProducts() {
        viewModelScope.launch {
            android.util.Log.d("ProductViewModel", "Loading producer products...")
            _listState.update { it.copy(isLoading = true, error = null) }

            val producerId = authRepository.currentUserId
            android.util.Log.d("ProductViewModel", "Producer ID: $producerId")
            if (producerId == null) {
                android.util.Log.e("ProductViewModel", "No producer ID - not authenticated")
                _listState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val result = productRepository.getProductsByProducer(producerId)
            android.util.Log.d("ProductViewModel", "Load result: isSuccess=${result.isSuccess}, productsCount=${result.getOrNull()?.size}")
            _listState.update {
                if (result.isSuccess) {
                    android.util.Log.d("ProductViewModel", "Successfully loaded ${result.getOrDefault(emptyList()).size} products")
                    it.copy(
                        products = result.getOrDefault(emptyList()),
                        isLoading = false
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Failed to load products"
                    android.util.Log.e("ProductViewModel", "Failed to load products: $errorMsg", result.exceptionOrNull())
                    it.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun createProduct(formState: ProductFormState, imageUri: Uri? = null) {
        viewModelScope.launch {
            _createState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val producerId = authRepository.currentUserId
            if (producerId == null) {
                _createState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            // Upload image if provided
            var imageUrl: String? = null
            if (imageUri != null) {
                val uploadResult = productRepository.uploadProductImage(imageUri)
                if (uploadResult.isFailure) {
                    _createState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }
                imageUrl = uploadResult.getOrNull()
            }

            val product = formState.toProduct(producerId, imageUrl)
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

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _editState.update { it.copy(isLoading = true, error = null) }

            val result = productRepository.getProductById(productId)

            _editState.update {
                if (result.isSuccess) {
                    it.copy(product = result.getOrNull(), isLoading = false)
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load product"
                    )
                }
            }
        }
    }

    fun updateProduct(productId: String, formState: ProductFormState, imageUri: Uri? = null) {
        viewModelScope.launch {
            _editState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val producerId = authRepository.currentUserId
            if (producerId == null) {
                _editState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            // Get current product to preserve existing image URL if no new image
            val currentProduct = _editState.value.product
            var imageUrl: String? = currentProduct?.imageUrl

            // Upload new image if provided
            if (imageUri != null) {
                val uploadResult = productRepository.uploadProductImage(imageUri, productId)
                if (uploadResult.isFailure) {
                    _editState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }
                imageUrl = uploadResult.getOrNull()
            }

            val product = formState.toProduct(producerId, imageUrl).copy(id = productId)
            val result = productRepository.updateProduct(product)

            _editState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isSuccess = true)
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to update product"
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

    fun clearEditState() {
        _editState.update { ProductEditState() }
    }

    fun clearError() {
        _listState.update { it.copy(error = null) }
        _createState.update { it.copy(error = null) }
        _editState.update { it.copy(error = null) }
    }
}

// Extension function to convert form state to Product
private fun ProductFormState.toProduct(producerId: String, imageUrl: String? = null): Product {
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
        producerId = producerId,
        imageUrl = imageUrl
    )
}

// Extension function to convert Product to ProductFormState
fun Product.toFormState(): ProductFormState {
    return ProductFormState(
        name = name,
        brand = brand ?: "",
        shortDescription = shortDescription ?: "",
        description = description ?: "",
        category = "", // TODO: Map category_id to category name
        wholesalePrice = wholesalePrice.toString(),
        retailPrice = retailPrice?.toString() ?: "",
        unitsPerCase = unitsPerCase.toString(),
        minimumOrderQuantity = minimumOrderQuantity.toString(),
        volumeMl = volumeMl?.toString() ?: "",
        weightG = weightG?.toString() ?: "",
        servingSize = servingSize ?: "",
        servingsPerContainer = servingsPerContainer?.toString() ?: "",
        shelfLifeDays = shelfLifeDays?.toString() ?: "",
        countryOfOrigin = countryOfOrigin ?: "",
        storageInstructions = storageInstructions ?: "",
        ingredients = ingredients ?: "",
        allergens = allergens ?: "",
        isOrganic = isOrganic,
        isNonGmo = isNonGmo,
        isVegan = isVegan,
        isGlutenFree = isGlutenFree,
        isKosher = isKosher,
        sku = sku ?: "",
        upc = upc ?: "",
        leadTimeDays = leadTimeDays?.toString() ?: ""
    )
}
