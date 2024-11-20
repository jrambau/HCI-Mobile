

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InvestmentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InvestmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // In a real app, you'd fetch this data from a repository
        viewModelScope.launch {
            _uiState.value = InvestmentUiState(
                myInvestment = 30000.0,
                currentValue = 50000.0,
                currentBalance = 20000.0,
                chartData = listOf(
                    ChartData(1f, 100f),
                    ChartData(2f, 200f),
                    ChartData(3f, 150f)
                )
            )
        }
    }

    fun onInvestmentAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(investmentAmount = amount)
    }

    fun onWithdrawalAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(withdrawalAmount = amount)
    }

    fun onInvest() {
        // Implement investment logic here
    }

    fun onWithdraw() {
        // Implement withdrawal logic here
    }

    fun toggleChartExpansion() {
        _uiState.value = _uiState.value.copy(isChartExpanded = !_uiState.value.isChartExpanded)
    }
}

data class InvestmentUiState(
    val myInvestment: Double = 0.0,
    val currentValue: Double = 0.0,
    val currentBalance: Double = 0.0,
    val investmentAmount: String = "",
    val withdrawalAmount: String = "",
    val isChartExpanded: Boolean = false,
    val chartData: List<ChartData> = emptyList()
)

data class ChartData(
    val x: Float,
    val y: Float
)