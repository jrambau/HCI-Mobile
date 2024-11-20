

import Components.ConfirmationDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.lupay.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import network.Repository.PaymentRepository
import network.Repository.UserRepository
import network.Repository.WalletRepository

class InvestmentViewModel(
    private val sessionManager: SessionManager,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    var generalUiState by mutableStateOf(GeneralUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
        private set
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

    public final fun onInvest() {
        //
    }

    fun onWithdraw() {
        // Implement withdrawal logic here
    }

    fun toggleChartExpansion() {
        _uiState.value = _uiState.value.copy(isChartExpanded = !_uiState.value.isChartExpanded)
    }
    companion object {
        fun provideFactory(application: MyApplication
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
    val chartData: List<ChartData> = emptyList()
)

data class ChartData(
    val x: Float,
    val y: Float
)