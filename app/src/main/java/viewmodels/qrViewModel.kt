package viewmodels

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import network.Repository.UserRepository
import network.Repository.PaymentRepository
import network.Repository.WalletRepository
import network.model.NetworkCard

class QrViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    var uiState by mutableStateOf(GeneralUiState())
        private set

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _cards = MutableStateFlow<List<NetworkCard>>(emptyList())
    val cards: StateFlow<List<NetworkCard>> = _cards.asStateFlow()

    var selectedPaymentMethod by mutableStateOf<PaymentMethod>(PaymentMethod.WALLET)
    var selectedCard by mutableStateOf<NetworkCard?>(null)

    init {
        getUserEmail()
        getCards()
    }

    private fun getUserEmail() {
        viewModelScope.launch {
            uiState = uiState.copy(isFetching = true, error = null, successMessage = null)
            runCatching { userRepository.getUser() }
                .onSuccess { user ->
                    _userEmail.value = user.email
                    uiState = uiState.copy(isFetching = false, successMessage = user.email)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isFetching = false, error = Error(e.message ?: MyApplication.instance.getString(R.string.error_generic)))
                }
        }
    }

    private fun getCards() {
        viewModelScope.launch {
            runCatching { walletRepository.getCards() }
                .onSuccess { cardList ->
                    _cards.value = cardList
                }
                .onFailure { e ->
                    uiState = uiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_cards)))
                }
        }
    }

    fun makePayment(receiverEmail: String, amount: Double, description: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isFetching = true, error = null, successMessage = null)
            runCatching {
                when (selectedPaymentMethod) {
                    PaymentMethod.WALLET -> paymentRepository.makePayment(amount, "BALANCE", description, null, receiverEmail)
                    PaymentMethod.CARD -> {
                        selectedCard?.let { card ->
                            paymentRepository.makePayment(amount, "CARD", description, card.id, receiverEmail)
                        } ?: throw IllegalStateException(MyApplication.instance.getString(R.string.error_no_card_selected))
                    }
                }
            }
                .onSuccess {
                    uiState = uiState.copy(isFetching = false, successMessage = MyApplication.instance.getString(R.string.payment_successful))
                }
                .onFailure { e ->
                    uiState = uiState.copy(isFetching = false, error = Error(e.message ?: MyApplication.instance.getString(R.string.error_transfering_money)))
                }
        }
    }

    fun setPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod = method
    }

    fun updateSelectedCard(card: NetworkCard) {
        selectedCard = card
    }

    enum class PaymentMethod {
        WALLET, CARD
    }

    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QrViewModel(
                        application.sessionManager,
                        application.userRepository,
                        application.paymentRepository,
                        application.walletRepository
                    ) as T
                }
            }
    }
}

