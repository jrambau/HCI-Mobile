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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.Repository.PaymentRepository
import network.Repository.UserRepository
import network.Repository.WalletRepository
import network.model.NetworkCard
import network.model.NetworkPaymentInfo
import network.model.NetworkPaymentInfoResponse
import network.model.NetworkWalletInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.abs

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    var generalUiState by mutableStateOf(GeneralUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
        private set
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var selectedPaymentMethod by mutableStateOf<PaymentMethod>(PaymentMethod.WALLET)
    var selectedCard by mutableStateOf<NetworkCard?>(null)
    var cards by mutableStateOf<List<NetworkCard>>(emptyList())
    var paymentLinkDetails by mutableStateOf<NetworkPaymentInfo?>(null)

    init {
        if (generalUiState.isAuthenticated) {
            viewModelScope.launch {
                fetchWalletInfo()
                fetchTransactions()
                fetchMonthlyExpenses()
                fetchCards()
            }
        }
    }

    private fun fetchWalletInfo() {
        viewModelScope.launch {
            try {
                val walletInfo = walletRepository.getWalletBalance()
                _uiState.update { currentState ->
                    currentState.copy(
                        availableBalance = walletInfo.balance?.toInt() ?: 0
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching wallet info", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error fetching wallet info"))
            }
        }
    }

    private fun fetchCards() {
        viewModelScope.launch {
            try {
                cards = walletRepository.getCards()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching cards", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error fetching cards"))
            }
        }
    }

    private fun fetchMonthlyExpenses() {
        viewModelScope.launch {
            try {
                val paymentsResponse = paymentRepository.getPaymentsInfo()
                val allTransactions = paymentsResponse.payments.map { it.toTransaction() }

                val currentDate = LocalDate.now()
                val sixMonthsAgo = currentDate.minusMonths(5)

                val monthlyExpenses = (0..5).map { i ->
                    val date = currentDate.minusMonths(i.toLong())
                    val startOfMonth = date.withDayOfMonth(1)
                    val endOfMonth = date.withDayOfMonth(date.lengthOfMonth())

                    val expensesForMonth = allTransactions.filter { it.timestamp.toLocalDate() in startOfMonth..endOfMonth && !it.isIncoming }
                        .sumOf { abs(it.amount) }

                    MonthlyExpense(
                        month = date.format(DateTimeFormatter.ofPattern("MMM")),
                        amount = expensesForMonth.toFloat(),
                        date = date
                    )
                }.sortedBy { it.date }

                _uiState.update { currentState ->
                    currentState.copy(
                        monthlyExpenses = monthlyExpenses,
                        expenses = monthlyExpenses.sumOf { it.amount.toInt() }
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching monthly expenses", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error fetching monthly expenses"))
            }
        }
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val paymentsResponse = paymentRepository.getPaymentsInfo()
                val allTransactions = paymentsResponse.payments.map { it.toTransaction() }
                _uiState.update { currentState ->
                    currentState.copy(
                        transactions = allTransactions,
                        filteredTransactions = allTransactions
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching transactions", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error fetching transactions"))
            }
        }
    }

    fun toggleHidden() {
        _uiState.update { currentState ->
            currentState.copy(isHidden = !currentState.isHidden)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            val filteredTransactions = if (query.isBlank()) {
                currentState.transactions
            } else {
                currentState.transactions.filter {
                    it.userName.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                filteredTransactions = filteredTransactions
            )
        }
    }

    fun transferMoney(amount: Double, receiverEmail: String, description: String) {
        viewModelScope.launch {
            try {
                when (selectedPaymentMethod) {
                    PaymentMethod.WALLET -> paymentRepository.makePayment(amount, "BALANCE", description, null, receiverEmail)
                    PaymentMethod.CARD -> {
                        selectedCard?.let { card ->
                            paymentRepository.makePayment(amount, "CARD", description, card.id, receiverEmail)
                        } ?: run {
                            generalUiState = generalUiState.copy(error = Error("No card selected"))
                            return@launch
                        }
                    }
                }
                fetchWalletInfo()
                fetchTransactions()
                generalUiState = generalUiState.copy(successMessage = "Transferencia exitosa")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error transferring money", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error transferring money"))
            }
        }
    }

    fun generatePaymentLink(amount: Double, description: String) {
        viewModelScope.launch {
            try {
                val response = paymentRepository.generateLink(amount, description)
                _uiState.update { currentState ->
                    currentState.copy(generatedPaymentLink = response.linkUuid)
                }
                generalUiState = generalUiState.copy(successMessage = "Link de pago generado: ${response.linkUuid}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error generating payment link", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error generating payment link"))
            }
        }
    }

    fun getPaymentLinkDetails(linkUuid: String) {
        viewModelScope.launch {
            try {
                paymentLinkDetails = paymentRepository.getLinkDetails(linkUuid).payment
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching payment link details", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error fetching payment link details"))
            }
        }
    }

    fun payByLink(linkUuid: String, paymentMethod: PaymentMethod, cardId: Int?) {
        viewModelScope.launch {
            try {
                val paymentInfo = when (paymentMethod) {
                    PaymentMethod.WALLET -> paymentRepository.payByLink(linkUuid)
                    PaymentMethod.CARD -> {
                        if (cardId != null) {
                            paymentRepository.payByLinkWithCard(linkUuid, cardId)
                        } else {
                            throw Exception("Card ID is required for card payment")
                        }
                    }
                }
                fetchWalletInfo()
                fetchTransactions()
                generalUiState = generalUiState.copy(successMessage = "Pago realizado con éxito")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error paying by link", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error paying by link"))
            }
        }
    }

    fun rechargeMoney(amount: Double) {
        viewModelScope.launch {
            try {
                walletRepository.rechargeWallet(amount)
                fetchWalletInfo()
                generalUiState = generalUiState.copy(successMessage = "Recarga exitosa")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error recharging wallet", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: "Error recharging wallet"))
            }
        }
    }

    fun clearError() {
        generalUiState = generalUiState.copy(error = null)
    }

    fun clearSuccessMessage() {
        generalUiState = generalUiState.copy(successMessage = null)
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
    suspend fun NetworkPaymentInfo.toTransaction(): Transaction {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss]")
        val timestamp = try {
            LocalDateTime.parse(this.createdAt, formatter)
        } catch (e: DateTimeParseException) {
            LocalDate.parse(this.createdAt, DateTimeFormatter.ISO_DATE).atStartOfDay()
        }
        val isIncoming = this.receiver?.id == userRepository.getUser().id
        return Transaction(
            id = this.id ?: 0,
            description = this.description ?: "No description",
            date = this.createdAt,
            userName = if (isIncoming) this.payer?.firstName ?: "Unknown" else this.receiver?.firstName ?: "Unknown",
            timestamp = timestamp,
            amount = this.amount?.toInt() ?: 0,
            type = this.type ?: "Unknown",
            receiverName = if (isIncoming) this.receiver?.firstName else this.payer?.firstName,
            isIncoming = isIncoming
        )
    }
    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    application.sessionManager,
                    application.userRepository,
                    application.paymentRepository,
                    application.walletRepository
                ) as T
            }
        }
    }
}

data class HomeUiState(
    val availableBalance: Int = 0,
    val expenses: Int = 0,
    val monthlyExpenses: List<MonthlyExpense> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val filteredTransactions: List<Transaction> = emptyList(),
    val isHidden: Boolean = true,
    val paymentLinkDetails: NetworkPaymentInfo? = null,
    val generatedPaymentLink: String? = null
)

data class MonthlyExpense(
    val month: String,
    val amount: Float,
    val date: LocalDate
)

data class Transaction(
    val id: Int,
    val description: String,
    val date: String?,
    val userName: String,
    val timestamp: LocalDateTime,
    val amount: Int,
    val type: String,
    val receiverName: String?,
    val isIncoming: Boolean
) {
    fun getFormattedTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd")
        return timestamp.format(formatter)
    }
}





