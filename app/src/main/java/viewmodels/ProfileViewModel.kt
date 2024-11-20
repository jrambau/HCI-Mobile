package com.example.lupay.ui.viewmodels

import GeneralUiState
import SessionManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lupay.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import network.Repository.UserRepository
import network.Repository.WalletRepository

data class ProfileUiState(
    val userName: String = "John Doe",
    val firstName: String = "John",
    val lastName: String = "Doe",
    val birthDate: String? = "08/01/1999",
    val fiscalActivity: String? = null,
    val email: String = "johndoe@gmail.com",
    val phone: String? = null,
    val cvu: String = "000957057772782438000",
    val alias: String = "doe.john",
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
    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }
    companion object {
        fun provideFactory(application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
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