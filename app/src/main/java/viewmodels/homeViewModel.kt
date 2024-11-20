import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // En una aplicación real, estos datos vendrían de un repositorio
        val currentDate = LocalDate.now()
        val monthlyExpenses = (0..11).map { i ->
            val date = currentDate.minusMonths(i.toLong())
            MonthlyExpense(
                month = date.format(DateTimeFormatter.ofPattern("MMM")),
                amount = (50..100).random().toFloat(),
                date = date
            )
        }.sortedBy { it.date }

        val transactions = listOf(
            Transaction(
                userName = "John Doe",
                timestamp = LocalDateTime.now().minusHours(2),
                amount = 90
            ),
            Transaction(
                userName = "Jim Doe",
                timestamp = LocalDateTime.now().minusHours(5),
                amount = 200
            ),
            Transaction(
                userName = "Jane Smith",
                timestamp = LocalDateTime.now().minusDays(1),
                amount = 150
            )
        ).sortedByDescending { it.timestamp }

        _uiState.value = HomeUiState(
            availableBalance = 10000,
            expenses = 20000,
            monthlyExpenses = monthlyExpenses,
            transactions = transactions,
            filteredTransactions = transactions
        )
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
}

data class HomeUiState(
    val availableBalance: Int = 0,
    val expenses: Int = 0,
    val monthlyExpenses: List<MonthlyExpense> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val filteredTransactions: List<Transaction> = emptyList(),
    val isHidden: Boolean = true
)

data class MonthlyExpense(
    val month: String,
    val amount: Float,
    val date: LocalDate
)

data class Transaction(
    val userName: String,
    val timestamp: LocalDateTime,
    val amount: Int
) {
    fun getFormattedTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, HH:mm a")
        return timestamp.format(formatter)
    }
}