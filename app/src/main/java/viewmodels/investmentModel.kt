import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import com.example.lupay.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.Repository.WalletRepository
import network.model.NetworkInvestInfo
import network.model.NetworkInterest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InvestmentViewModel(
    private val sessionManager: SessionManager,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InvestmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInvestmentData()
        loadAllDailyReturns()
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
                            currentBalance = it
                        )
                    } ?: currentState
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: MyApplication.instance.getString(R.string.error_investment))
                }
            }
        }
    }

    private fun loadAllDailyReturns() {
        viewModelScope.launch {
            try {
                val allReturns = mutableListOf<NetworkInvestInfo>()
                var page = 1
                var hasMoreData = true

                while (hasMoreData) {
                    val returns = walletRepository.getDailyReturns(page)
                    allReturns.addAll(returns.dailyReturns)
                    hasMoreData = returns.dailyReturns.isNotEmpty()
                    page++
                }

                if (allReturns.isEmpty()) {
                    _uiState.update { it.copy(noDataMessage = MyApplication.instance.getString(R.string.no_daily_returns)) }
                } else {
                    processAndUpdateChartData(allReturns)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: MyApplication.instance.getString(R.string.error_daily_returns))
                }
            }
        }
    }

    private fun processAndUpdateChartData(allReturns: List<NetworkInvestInfo>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        val chartData = allReturns
            .sortedByDescending { it.createdAt }
            .take(30)
            .reversed()
            .mapIndexed { index, info ->
                val date = LocalDate.parse(info.createdAt?.split("T")?.get(0), formatter)
                val daysAgo = today.toEpochDay() - date.toEpochDay()
                ChartData(
                    x = daysAgo.toFloat(),
                    y = info.balanceAfter?.toFloat() ?: 0f,
                    label = "${30 - index} ${MyApplication.instance.getString(R.string.days_ago)}"
                )
            }

        _uiState.update { currentState ->
            currentState.copy(chartData = chartData)
        }
    }

    private fun loadDailyInterest() {
        viewModelScope.launch {
            try {
                val interest = walletRepository.getDailyInterest()
                _uiState.update { currentState ->
                    currentState.copy(dailyInterestRate = interest.interest * 100)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = e.message ?: MyApplication.instance.getString(R.string.error_interest))
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
                updateChartWithNewInvestment(amount)

                _uiState.update { it.copy(
                    investmentAmount = "",
                    successMessage = "${MyApplication.instance.getString(R.string.investment_succesful)}${amount}"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: MyApplication.instance.getString(R.string.error_invest)
                ) }
            }
        }
    }

    private fun updateChartWithNewInvestment(amount: Double) {
        val currentChartData = _uiState.value.chartData.toMutableList()
        if (currentChartData.isNotEmpty()) {
            val lastEntry = currentChartData.last()
            val newEntry = ChartData(
                x = lastEntry.x,
                y = lastEntry.y + amount.toFloat(),
                label = MyApplication.instance.getString(R.string.today)
            )
            currentChartData[currentChartData.lastIndex] = newEntry
            _uiState.update { it.copy(chartData = currentChartData) }
        }
    }

    fun onWithdraw() {
        viewModelScope.launch {
            try {
                val amount = _uiState.value.withdrawalAmount.toDoubleOrNull() ?: return@launch
                val result = walletRepository.divest(amount)

                loadInvestmentData()
                updateChartWithWithdrawal(amount)

                _uiState.update { it.copy(
                    withdrawalAmount = "",
                    successMessage = "${MyApplication.instance.getString(R.string.withdraw_succesful)}${amount}"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: MyApplication.instance.getString(R.string.error_withdraw)
                ) }
            }
        }
    }

    private fun updateChartWithWithdrawal(amount: Double) {
        val currentChartData = _uiState.value.chartData.toMutableList()
        if (currentChartData.isNotEmpty()) {
            val lastEntry = currentChartData.last()
            val newEntry = ChartData(
                x = lastEntry.x,
                y = (lastEntry.y - amount.toFloat()).coerceAtLeast(0f),
                label = "Today"
            )
            currentChartData[currentChartData.lastIndex] = newEntry
            _uiState.update { it.copy(chartData = currentChartData) }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(
            error = null,
            successMessage = null,
            noDataMessage = null
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
    val currentBalance: Double = 0.0,
    val investmentAmount: String = "",
    val withdrawalAmount: String = "",
    val isChartExpanded: Boolean = false,
    val chartData: List<ChartData> = emptyList(),
    val dailyInterestRate: Double = 0.0,
    val error: String? = null,
    val successMessage: String? = null,
    val noDataMessage: String? = null
)

data class ChartData(
    val x: Float,
    val y: Float,
    val label: String
)

