package com.indelo.goods.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indelo.goods.data.model.UserType
import com.indelo.goods.data.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
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

    val sessionStatus: StateFlow<SessionStatus> = authRepository.sessionStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionStatus.Initializing
        )

    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        // Load user type when authenticated
        viewModelScope.launch {
            sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    loadUserType()
                }
            }
        }
    }

    private fun loadUserType() {
        viewModelScope.launch {
            val result = authRepository.getUserType()
            if (result.isSuccess) {
                val userType = result.getOrNull()
                _uiState.update { it.copy(selectedUserType = userType) }
            }
        }
    }

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, phone = phone) }
            val result = authRepository.sendOtp(phone)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        otpSent = true,
                        step = AuthStep.OTP_VERIFICATION,
                        error = null
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to send code"
                    )
                }
            }
        }
    }

    fun verifyOtp(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.verifyOtp(_uiState.value.phone, token)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        step = AuthStep.USER_TYPE_SELECTION,
                        error = null
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Invalid code"
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
