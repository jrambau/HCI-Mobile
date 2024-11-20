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

data class Card(
    val id: Int,
    val cardNumber: String,
    val cardName: String,
    val cardExpiry: String,
    val cvv: String
)

data class NewCardData(
    val cardNumber: String,
    val cardName: String,
    val cardExpiry: String,
    val cvv: String
)

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

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchCards()
    }

    // Fetching cards (simulate API call)
    fun fetchCards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Simulate fetching cards
                val fakeCards = listOf(
                    Card(1, "3455 5678 9012 3456", "John Doe", "12/23", "123"),
                    Card(2, "4593 5432 1098 7654", "Jane Smith", "11/24", "456")
                )
                _cards.value = fakeCards
            } catch (e: Exception) {
                _error.value = "Failed to fetch cards: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // Adding a new card (simulate API call)
    fun addNewCard(newCardData: NewCardData) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Simulate adding a card
                val newCard = Card(
                    id = _cards.value.size + 1,
                    cardNumber = newCardData.cardNumber,
                    cardName = newCardData.cardName,
                    cardExpiry = newCardData.cardExpiry,
                    cvv = newCardData.cvv
                )
                _cards.value = _cards.value + newCard
            } catch (e: Exception) {
                _error.value = "Failed to add new card: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // Deleting a card (simulate API call)
    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            try {
                // Simulate deleting a card
                _cards.value = _cards.value.filter { it.id != cardId }
            } catch (e: Exception) {
                _error.value = "Failed to delete card: ${e.message}"
            }
        }
    }

    // Toggle card visibility
    fun toggleHidden() {
        _uiState.value = _uiState.value.copy(isHidden = !_uiState.value.isHidden)
    }

    // Function to update the error message
    fun updateError(errorMessage: String?) {
        _error.value = errorMessage
    }
    companion object {
        fun provideFactory(application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
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
