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
import network.Repository.PaymentRepository
import network.Repository.UserRepository
import network.Repository.WalletRepository
import network.model.NetworkCard

data class UiState(
    val isHidden: Boolean = true,
    val isLoading: Boolean = false
)

class CreditCardViewModel(
    private val sessionManager: SessionManager,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    var generalUiState by mutableStateOf(GeneralUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
        private set
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _cards = MutableStateFlow<List<NetworkCard>>(emptyList())
    val cards: StateFlow<List<NetworkCard>> = _cards.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchCards()
    }

    fun fetchCards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val fetchedCards = walletRepository.getCards().toList()
                _cards.value = fetchedCards
            } catch (e: Exception) {
                _error.value = "Failed to fetch cards: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun addNewCard(newCardData: NetworkCard) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val addedCard = walletRepository.addCard(newCardData)
                _cards.value = _cards.value + addedCard
            } catch (e: Exception) {
                _error.value = "Failed to add new card: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            try {
                walletRepository.deleteCard(cardId)
                _cards.value = _cards.value.filter { it.id != cardId }
            } catch (e: Exception) {
                _error.value = "Failed to delete card: ${e.message}"
            }
        }
    }

    fun toggleHidden() {
        _uiState.value = _uiState.value.copy(isHidden = !_uiState.value.isHidden)
    }

    fun updateError(errorMessage: String?) {
        _error.value = errorMessage
    }

    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreditCardViewModel(
                    application.sessionManager,
                    application.walletRepository,
                    application.userRepository,
                    application.paymentRepository
                ) as T
            }
        }
    }
}

