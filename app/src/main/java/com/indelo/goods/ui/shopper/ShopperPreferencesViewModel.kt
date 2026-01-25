package com.indelo.goods.ui.shopper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.ShopperPreferences
import com.indelo.goods.data.repository.AuthRepository
import com.indelo.goods.data.repository.ShopperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopperPreferencesState(
    val preferences: ShopperPreferences? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class ShopperPreferencesViewModel(
    private val shopperRepository: ShopperRepository = ShopperRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ShopperPreferencesState())
    val state: StateFlow<ShopperPreferencesState> = _state.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch

            _state.update { it.copy(isLoading = true) }

            val result = shopperRepository.getPreferences(userId)
            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        preferences = result.getOrNull(),
                        isLoading = false
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun savePreferences(categories: List<String>, notificationsEnabled: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update { it.copy(error = "Not authenticated") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            val preferences = ShopperPreferences(
                userId = userId,
                favoriteCategories = categories,
                notificationsEnabled = notificationsEnabled
            )

            val result = shopperRepository.savePreferences(preferences)

            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        preferences = result.getOrNull(),
                        isLoading = false,
                        isSaved = true
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to save preferences"
                    )
                }
            }
        }
    }
}
