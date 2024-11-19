package com.example.lupay.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

data class DeletedCardStatus(
    val success: Boolean,
    val message: String
)

// Commenting out the API service interface
/*
interface WalletApiService {
    @GET("api/wallet/cards")
    suspend fun getCards(): List<Card>

    @POST("api/wallet/cards")
    suspend fun addNewCard(@Body newCardData: NewCardData): Card

    @DELETE("api/wallet/cards/{cardId}")
    suspend fun deleteCard(@Path("cardId") cardId: Int): DeletedCardStatus
}
*/

data class UiState(
    val cardName: String = "",
    val isHidden: Boolean = true
)

class CreditCardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Commenting out the Retrofit initialization
    /*
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val walletApiService = retrofit.create(WalletApiService::class.java)
    */

    init {
        fetchCards()
    }

    fun fetchCards() {
        viewModelScope.launch {
            try {
                // Using fake data instead of fetching from API
                val fakeCards = listOf(
                    Card(1, "1234 5678 9012 3456", "John Doe", "12/23", "123"),
                    Card(2, "9876 5432 1098 7654", "Jane Smith", "11/24", "456")
                )
                _cards.value = fakeCards
            } catch (e: Exception) {
                _error.value = "Failed to fetch cards: ${e.message}"
            }
        }
    }

    fun addNewCard(newCardData: NewCardData) {
        viewModelScope.launch {
            try {
                // Adding fake card data
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
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            try {
                // Removing card from fake data
                _cards.value = _cards.value.filter { it.id != cardId }
            } catch (e: Exception) {
                _error.value = "Failed to delete card: ${e.message}"
            }
        }
    }

    fun toggleHidden() {
        _uiState.value = _uiState.value.copy(isHidden = !_uiState.value.isHidden)
    }
}