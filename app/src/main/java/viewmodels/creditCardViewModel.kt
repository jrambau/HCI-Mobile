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
import com.example.lupay.R
import kotlinx.coroutines.flow.*
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

    private val _cards = MutableSharedFlow<List<NetworkCard>>(replay = 1)
    val cards: SharedFlow<List<NetworkCard>> = _cards.asSharedFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _addCardSuccess = MutableStateFlow(false)
    val addCardSuccess: StateFlow<Boolean> = _addCardSuccess.asStateFlow()

    init {
        fetchCards()
    }

    fun fetchCards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val fetchedCards = walletRepository.getCards().toList()
                _cards.emit(fetchedCards)
            } catch (e: Exception) {
                _error.value = "${MyApplication.instance.getString(R.string.error_fetching_cards)} ${e.message}"
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
                fetchCards() // Actualiza la lista de tarjetas después de agregar una nueva
                _addCardSuccess.value = true
            } catch (e: Exception) {
                _error.value = "${MyApplication.instance.getString(R.string.failed_to_add_card)} ${e.message}"
                _addCardSuccess.value = false
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            try {
                walletRepository.deleteCard(cardId)
                fetchCards() // Actualiza la lista de tarjetas después de eliminar una
            } catch (e: Exception) {
                _error.value = "${MyApplication.instance.getString(R.string.failed_to_delete_card)} ${e.message}"
            }
        }
    }

    fun toggleHidden() {
        _uiState.value = _uiState.value.copy(isHidden = !_uiState.value.isHidden)
    }

    fun updateError(errorMessage: String?) {
        _error.value = errorMessage
    }

    fun resetAddCardSuccess() {
        _addCardSuccess.value = false
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

