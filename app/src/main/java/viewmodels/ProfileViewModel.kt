package com.example.lupay.ui.viewmodels

import GeneralUiState
import SessionManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import network.Repository.UserRepository
import network.Repository.WalletRepository

data class ProfileUiState(
    val userName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String? = null,
    val email: String = "",
    val cbu: String = "",
    val alias: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var generalUiState by mutableStateOf(GeneralUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
        private set

    fun fetchUserData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                val walletInfo = walletRepository.getWalletDetails()
                _uiState.value = _uiState.value.copy(
                    userName = "${user.firstName} ${user.lastName}",
                    firstName = user.firstName,
                    lastName = user.lastName,
                    birthDate = user.birthDate,
                    email = user.email,
                    cbu = walletInfo.cbu ?: "No especificado",
                    alias = walletInfo.alias ?: "No especificado",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    fun updateAlias(newAlias: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val walletInfo = userRepository.updateAlias(newAlias)
                _uiState.value = _uiState.value.copy(
                    alias = walletInfo.alias ?: "No especificado",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update alias"
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun resetPassword(email: String, newPassword: String, resetCode: String) {
        if (newPassword.length <= 6) {
            _uiState.value = _uiState.value.copy(
                error = "Password must be longer than 6 characters"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                userRepository.resetPassword(email, newPassword, resetCode)
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to reset password"
                )
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                userRepository.logoutUser()
                sessionManager.removeAuthToken()
                generalUiState = generalUiState.copy(isAuthenticated = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to logout"
                )
            }
        }
    }

    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        application.sessionManager,
                        application.walletRepository,
                        application.userRepository
                    ) as T
                }
            }
    }
}

