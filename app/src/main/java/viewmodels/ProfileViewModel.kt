package com.example.lupay.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val name: String = "John Doe",
    val email: String = "john.doe@example.com",
    val phoneNumber: String = "+1234567890",
    val joinDate: String = "November 2023",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phoneNumber: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            name = name ?: _uiState.value.name,
            email = email ?: _uiState.value.email,
            phoneNumber = phoneNumber ?: _uiState.value.phoneNumber,
            isLoading = false
        )
    }
}