package com.indelo.goods.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.Shop
import com.indelo.goods.data.repository.AuthRepository
import com.indelo.goods.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopListState(
    val shops: List<Shop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ShopFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ShopViewModel(
    private val shopRepository: ShopRepository = ShopRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _listState = MutableStateFlow(ShopListState())
    val listState: StateFlow<ShopListState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(ShopFormState())
    val formState: StateFlow<ShopFormState> = _formState.asStateFlow()

    init {
        loadShops()
    }

    fun loadShops() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }

            val ownerId = authRepository.currentUserId
            if (ownerId == null) {
                _listState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val result = shopRepository.getShopsByOwner(ownerId)
            _listState.update {
                if (result.isSuccess) {
                    it.copy(
                        shops = result.getOrDefault(emptyList()),
                        isLoading = false
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load shops"
                    )
                }
            }
        }
    }

    fun createShop(shop: Shop) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val result = shopRepository.createShop(shop)

            _formState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isSuccess = true)
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to create shop"
                    )
                }
            }

            if (result.isSuccess) {
                loadShops()
            }
        }
    }

    fun createShopFromForm(formData: ShopFormData) {
        viewModelScope.launch {
            val ownerId = authRepository.currentUserId
            if (ownerId == null) {
                _formState.update { it.copy(error = "Not authenticated") }
                return@launch
            }

            val shop = Shop(
                name = formData.name,
                businessType = formData.businessType.ifBlank { null },
                description = formData.description.ifBlank { null },
                ownerId = ownerId,
                address = formData.address.ifBlank { null },
                city = formData.city.ifBlank { null },
                state = formData.state.ifBlank { null },
                zipCode = formData.zipCode.ifBlank { null },
                country = formData.country.ifBlank { null },
                phone = formData.phone.ifBlank { null },
                email = formData.email.ifBlank { null },
                taxId = formData.taxId.ifBlank { null }
            )

            createShop(shop)
        }
    }

    fun deleteShop(shopId: String) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }

            val result = shopRepository.deleteShop(shopId)

            if (result.isSuccess) {
                loadShops()
            } else {
                _listState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to delete shop"
                    )
                }
            }
        }
    }

    fun clearFormState() {
        _formState.update { ShopFormState() }
    }

    fun clearError() {
        _listState.update { it.copy(error = null) }
        _formState.update { it.copy(error = null) }
    }
}
