import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import com.example.lupay.R
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
    private val _hasAttemptedToLoadTransactions = MutableStateFlow(false)
    val hasAttemptedToLoadTransactions: StateFlow<Boolean> = _hasAttemptedToLoadTransactions.asStateFlow()
    var selectedPaymentMethod by mutableStateOf<PaymentMethod>(PaymentMethod.WALLET)
    var selectedCard by mutableStateOf<NetworkCard?>(null)
    var cards by mutableStateOf<List<NetworkCard>>(emptyList())
    var paymentLinkDetails by mutableStateOf<NetworkPaymentInfo?>(null)
    private val _showPaymentLinkDetailsDialog = MutableStateFlow(false)
    val showPaymentLinkDetailsDialog: StateFlow<Boolean> = _showPaymentLinkDetailsDialog.asStateFlow()

    fun showPaymentLinkDetailsDialog(show: Boolean) {
        _showPaymentLinkDetailsDialog.value = show
    }
    private val _showTransferConfirmation = MutableStateFlow<TransferConfirmation?>(null)
    val showTransferConfirmation: StateFlow<TransferConfirmation?> = _showTransferConfirmation.asStateFlow()
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
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_wallet_info)))
            }
        }
    }

    private fun fetchCards() {
        viewModelScope.launch {
            try {
                cards = walletRepository.getCards()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching cards", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_cards)))
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

                    val expensesForMonth = allTransactions.filter {
                        it.timestamp.toLocalDate() in startOfMonth..endOfMonth &&
                                (it.isCost && !it.isInvestment)
                    }.sumOf { abs(it.amount) }

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
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_monthly_expenses)))
            }
        }
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val allTransactions = mutableListOf<Transaction>()
                var currentPage = 1
                var hasMoreTransactions = true

                while (hasMoreTransactions) {
                    val paymentsResponse = paymentRepository.getPaymentsInfo(page = currentPage)
                    val pageTransactions = paymentsResponse.payments.map { it.toTransaction() }

                    if (pageTransactions.isEmpty()) {
                        hasMoreTransactions = false
                    } else {
                        allTransactions.addAll(pageTransactions)
                        currentPage++
                    }
                }

                val (investments, otherTransactions) = allTransactions.partition { it.isInvestment }
                _uiState.update { currentState ->
                    currentState.copy(
                        transactions = otherTransactions + investments.reversed(),
                        filteredTransactions = otherTransactions + investments.reversed()
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching transactions", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_transactions)))
            } finally {
                _hasAttemptedToLoadTransactions.value = true
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

    fun confirmTransfer(amount: Double, receiverEmail: String, description: String) {
        _showTransferConfirmation.value = TransferConfirmation(amount, receiverEmail, description)
    }

    fun cancelTransfer() {
        _showTransferConfirmation.value = null
    }

    fun executeTransfer() {
        val confirmation = _showTransferConfirmation.value ?: return
        transferMoney(confirmation.amount, confirmation.receiverEmail, confirmation.description)
        _showTransferConfirmation.value = null
    }

    private fun transferMoney(amount: Double, receiverEmail: String, description: String) {
        viewModelScope.launch {
            try {
                when (selectedPaymentMethod) {
                    PaymentMethod.WALLET -> paymentRepository.makePayment(amount, "BALANCE", description, null, receiverEmail)
                    PaymentMethod.CARD -> {
                        selectedCard?.let { card ->
                            paymentRepository.makePayment(amount, "CARD", description, card.id, receiverEmail)
                        } ?: run {
                            generalUiState = generalUiState.copy(error = Error(MyApplication.instance.getString(R.string.error_no_card_selected)))
                            return@launch
                        }
                    }
                }
                fetchWalletInfo()
                fetchTransactions()
                fetchMonthlyExpenses()  // Update expenses after transfer
                generalUiState = generalUiState.copy(successMessage = MyApplication.instance.getString(R.string.transfer_successful))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error transferring money", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_transfering_money)))
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
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error generating payment link", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_generating_payment_link)))
            }
        }
    }


    fun refreshData() {
        viewModelScope.launch {
            fetchWalletInfo()
            fetchTransactions()
            fetchMonthlyExpenses()
            fetchCards()
        }
    }

    fun clearGeneratedPaymentLink() {
        _uiState.update { currentState ->
            currentState.copy(generatedPaymentLink = null)
        }
    }

    fun getPaymentLinkDetails(linkUuid: String) {
        viewModelScope.launch {
            try {
                paymentLinkDetails = paymentRepository.getLinkDetails(linkUuid).payment
                showPaymentLinkDetailsDialog(true)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching payment link details", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_fetching_payment_link_details)))
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
                fetchMonthlyExpenses()
                showPaymentLinkDetailsDialog(false)
                generalUiState = generalUiState.copy(successMessage = MyApplication.instance.getString(R.string.payment_successful))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error paying by link", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_paying_by_link)))
            }
        }
    }

    fun rechargeMoney(amount: Double) {
        viewModelScope.launch {
            try {
                walletRepository.rechargeWallet(amount)
                fetchWalletInfo()
                generalUiState = generalUiState.copy(successMessage = MyApplication.instance.getString(R.string.recharge_successful))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error recharging wallet", e)
                generalUiState = generalUiState.copy(error = Error(e.message ?: MyApplication.instance.getString(R.string.error_recharging_wallet)))
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


    private suspend fun NetworkPaymentInfo.toTransaction(): Transaction {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss]")
        val timestamp = try {
            LocalDateTime.parse(this.createdAt, formatter)
        } catch (e: DateTimeParseException) {
            LocalDate.parse(this.createdAt, DateTimeFormatter.ISO_DATE).atStartOfDay()
        }
        val isIncoming = this.receiver?.id == userRepository.getUser().id
        val isInvestment = this.payer?.id == this.receiver?.id
        return Transaction(
            id = this.id ?: 0,
            description = this.description ?: MyApplication.instance.getString(R.string.unknown_description),
            date = this.createdAt,
            userName = if (isIncoming) {
                "${this.payer?.firstName ?: "Unknown"} ${this.payer?.lastName ?: "Unknown"}"
            } else {
                "${this.receiver?.firstName ?: "Unknown"} ${this.receiver?.lastName ?: "Unknown"}"
            },
            timestamp = timestamp,
            amount = this.amount?.toInt() ?: 0,
            type = this.type ?: "Unknown",
            receiverName = if (isIncoming) this.receiver?.firstName else this.payer?.firstName,
            isIncoming = isIncoming,
            lastName = if (isIncoming) this.receiver?.lastName ?: "Unknown" else this.payer?.lastName ?: "Unknown",
            isCost = !isIncoming,
            isInvestment = isInvestment,
            balanceBefore = this.balanceBefore ?: 0.0,
            balanceAfter = this.balanceAfter ?: 0.0
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

    data class TransferConfirmation(val amount: Double, val receiverEmail: String, val description: String)
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
    val isIncoming: Boolean,
    val lastName: String,
    val isCost: Boolean,
    val isInvestment: Boolean,
    val balanceBefore: Double,
    val balanceAfter: Double
) {
    fun getFormattedTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd")
        return timestamp.format(formatter)
    }
}
