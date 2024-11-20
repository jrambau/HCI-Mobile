package com.example.lupay.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val userName: String = "John Doe",
    val firstName: String = "John",
    val lastName: String = "Doe",
    val birthDate: String? = "08/01/1999",
    val fiscalActivity: String? = null,
    val email: String = "a@gmail.com",
    val phone: String? = null,
    val cvu: String = "000957057772782438000",
    val alias: String = "doe.john",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }
}