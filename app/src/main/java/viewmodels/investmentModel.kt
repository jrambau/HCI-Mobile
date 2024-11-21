import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.Repository.WalletRepository
import network.model.NetworkInvestInfo
import network.model.NetworkInterest

class InvestmentViewModel(
    private val sessionManager: SessionManager,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InvestmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInvestmentData()
        loadDailyReturns()
        loadDailyInterest()
    }

    private fun loadInvestmentData() {
        viewModelScope.launch {
            try {
                val investment = walletRepository.getInvestment()
                val walletInfo = walletRepository.getWalletBalance()

                _uiState.update { currentState ->
                    walletInfo.balance?.let {
                        currentState.copy(
                            myInvestment = investment.investment ?: 0.0,
                            currentValue = investment.balanceAfter ?: investment.investment ?: 0.0,
                            currentBalance = it // Asegúrate de que esto viene correctamente de walletInfo
                        )
                    }!!
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: "Error loading investment data")
                }
            }
        }
    }

    private fun loadDailyReturns(page: Int = 1) {
        viewModelScope.launch {
            try {
                val returns = walletRepository.getDailyReturns(page)
                val chartData = returns.dailyReturns.mapIndexed { index, info ->
                    ChartData(
                        x = index.toFloat(),
                        y = info.investment?.toFloat() ?: 0f
                    )
                }
                _uiState.update { currentState ->
                    currentState.copy(chartData = chartData)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: "Error loading daily returns")
                }
            }
        }
    }

    private fun loadDailyInterest() {
        viewModelScope.launch {
            try {
                val interest = walletRepository.getDailyInterest()
                _uiState.update { currentState ->
                    currentState.copy(dailyInterestRate = interest.interest)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: "Error loading daily interest")
                }
            }
        }
    }

    fun onInvestmentAmountChanged(amount: String) {
        _uiState.update { it.copy(investmentAmount = amount) }
    }

    fun onWithdrawalAmountChanged(amount: String) {
        _uiState.update { it.copy(withdrawalAmount = amount) }
    }

    fun onInvest() {
        viewModelScope.launch {
            try {
                val amount = _uiState.value.investmentAmount.toDoubleOrNull() ?: return@launch
                val result = walletRepository.invest(amount)

                loadInvestmentData()
                loadDailyReturns()

                _uiState.update { it.copy(
                    investmentAmount = "",
                    successMessage = "Successfully invested $${amount}"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Error processing investment"
                ) }
            }
        }
    }

    fun onWithdraw() {
        viewModelScope.launch {
            try {
                val amount = _uiState.value.withdrawalAmount.toDoubleOrNull() ?: return@launch
                val result = walletRepository.divest(amount)

                loadInvestmentData()
                loadDailyReturns()

                _uiState.update { it.copy(
                    withdrawalAmount = "",
                    successMessage = "Successfully withdrew $${amount}"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Error processing withdrawal"
                ) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(
            error = null,
            successMessage = null
        ) }
    }

    companion object {
        fun provideFactory(
            application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InvestmentViewModel(
                    application.sessionManager,
                    application.walletRepository
                ) as T
            }
        }
    }
}

data class InvestmentUiState(
    val myInvestment: Double = 0.0,
    val currentValue: Double = 0.0,
    val currentBalance: Double = 0.0, // Asegúrate de que esto está correctamente definido
    val investmentAmount: String = "",
    val withdrawalAmount: String = "",
    val isChartExpanded: Boolean = false,
    val chartData: List<ChartData> = emptyList(),
    val dailyInterestRate: Double = 0.0,
    val error: String? = null,
    val successMessage: String? = null
)

data class ChartData(
    val x: Float,
    val y: Float
)

