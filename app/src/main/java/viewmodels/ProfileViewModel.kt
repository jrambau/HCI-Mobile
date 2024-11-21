package com.example.lupay.ui.viewmodels

import GeneralUiState
import SessionManager
import android.util.Log
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
import kotlin.math.log

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

    /**
     * Fetches user and wallet data and updates the UI state.
     */
    fun fetchUserData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                // Fetch user data
                val user = userRepository.getUser()
                // Fetch wallet details
                val walletInfo = walletRepository.getWalletDetails()
                val aux1 = walletInfo.cbu
                val aux2 = walletInfo.alias
                Log.d("ProfileViewModel", "Fetched wallet info: $aux1 $aux2")
                _uiState.value = _uiState.value.copy(
                    userName = "${user.firstName} ${user.lastName}",
                    firstName = user.firstName,
                    lastName = user.lastName,
                    birthDate = user.birthDate,
                    email = user.email,
                    cbu = aux1 ?: "No especificado",
                    alias = aux2 ?: "No especificado",
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

    /**
     * Updates the user's alias and refreshes the UI state.
     */
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
