package com.indelo.goods.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.ProducerProfile
import com.indelo.goods.data.repository.AuthRepository
import com.indelo.goods.data.repository.ProducerProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProducerProfileState(
    val profile: ProducerProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ProducerProfileViewModel(
    private val profileRepository: ProducerProfileRepository = ProducerProfileRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ProducerProfileState())
    val state: StateFlow<ProducerProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update {
                    it.copy(isLoading = false, error = "Not authenticated")
                }
                return@launch
            }

            val result = profileRepository.getProducerProfile(userId)
            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        profile = result.getOrNull(),
                        isLoading = false
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    fun saveProfile(
        companyName: String?,
        brandName: String?,
        bio: String?,
        background: String?,
        inspiration: String?,
        goals: String?,
        websiteUrl: String?,
        location: String?,
        foundedYear: Int?,
        specialty: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, isSuccess = false) }

            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update {
                    it.copy(isSaving = false, error = "Not authenticated")
                }
                return@launch
            }

            val profile = ProducerProfile(
                id = userId,
                companyName = companyName?.takeIf { it.isNotBlank() },
                brandName = brandName?.takeIf { it.isNotBlank() },
                bio = bio?.takeIf { it.isNotBlank() },
                background = background?.takeIf { it.isNotBlank() },
                inspiration = inspiration?.takeIf { it.isNotBlank() },
                goals = goals?.takeIf { it.isNotBlank() },
                websiteUrl = websiteUrl?.takeIf { it.isNotBlank() },
                location = location?.takeIf { it.isNotBlank() },
                foundedYear = foundedYear,
                specialty = specialty?.takeIf { it.isNotBlank() }
            )

            val result = if (_state.value.profile == null) {
                profileRepository.createProducerProfile(profile)
            } else {
                profileRepository.updateProducerProfile(profile)
            }

            _state.update {
                if (result.isSuccess) {
                    it.copy(
                        profile = result.getOrNull(),
                        isSaving = false,
                        isSuccess = true
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to save profile"
                    )
                }
            }
        }
    }

    fun clearSuccess() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
