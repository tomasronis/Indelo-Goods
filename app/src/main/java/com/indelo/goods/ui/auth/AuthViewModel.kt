package com.indelo.goods.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.UserType
import com.indelo.goods.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthStep {
    PHONE_ENTRY,
    OTP_VERIFICATION,
    USER_TYPE_SELECTION
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val step: AuthStep = AuthStep.PHONE_ENTRY,
    val phone: String = "",
    val otpSent: Boolean = false,
    val selectedUserType: UserType? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        // Load user type when authenticated
        viewModelScope.launch {
            isAuthenticated.collect { authenticated ->
                android.util.Log.d("AuthViewModel", "Auth state changed in init: $authenticated")
                if (authenticated) {
                    loadUserType()
                }
            }
        }
    }

    private fun loadUserType() {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Loading user type...")
            val result = authRepository.getUserType()
            android.util.Log.d("AuthViewModel", "User type result: isSuccess=${result.isSuccess}, userType=${result.getOrNull()}")
            if (result.isSuccess) {
                val userType = result.getOrNull()
                _uiState.update { it.copy(selectedUserType = userType) }
                android.util.Log.d("AuthViewModel", "User type updated to: $userType")
            } else {
                android.util.Log.e("AuthViewModel", "Failed to load user type: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, phone = phone) }
            android.util.Log.d("AuthViewModel", "Calling sendOtp for: $phone")
            val result = authRepository.sendOtp(phone)
            android.util.Log.d("AuthViewModel", "Result: isSuccess=${result.isSuccess}, error=${result.exceptionOrNull()?.message}")
            _uiState.update {
                if (result.isSuccess) {
                    android.util.Log.d("AuthViewModel", "OTP sent successfully, moving to verification")
                    it.copy(
                        isLoading = false,
                        otpSent = true,
                        step = AuthStep.OTP_VERIFICATION,
                        error = null
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Failed to send code"
                    android.util.Log.e("AuthViewModel", "OTP failed with error: $errorMsg")
                    it.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun verifyOtp(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            android.util.Log.d("AuthViewModel", "Verifying OTP token: $token for phone: ${_uiState.value.phone}")
            val result = authRepository.verifyOtp(_uiState.value.phone, token)
            android.util.Log.d("AuthViewModel", "Verify result: isSuccess=${result.isSuccess}, error=${result.exceptionOrNull()?.message}")
            _uiState.update {
                if (result.isSuccess) {
                    android.util.Log.d("AuthViewModel", "OTP verified successfully, moving to user type selection")
                    it.copy(
                        isLoading = false,
                        step = AuthStep.USER_TYPE_SELECTION,
                        error = null
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Invalid code"
                    android.util.Log.e("AuthViewModel", "OTP verification failed: $errorMsg")
                    it.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun selectUserType(userType: UserType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedUserType = userType) }
            val result = authRepository.saveUserType(userType)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun goBack() {
        _uiState.update {
            when (it.step) {
                AuthStep.OTP_VERIFICATION -> it.copy(step = AuthStep.PHONE_ENTRY, otpSent = false)
                AuthStep.USER_TYPE_SELECTION -> it.copy(step = AuthStep.OTP_VERIFICATION)
                else -> it
            }
        }
    }

    // Legacy email/password methods (kept for fallback)
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.signUp(email, password)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.signIn(email, password)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.signOut()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    step = AuthStep.PHONE_ENTRY,
                    phone = "",
                    otpSent = false,
                    selectedUserType = null,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
