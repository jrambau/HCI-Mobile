import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.lupay.R
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.lupay.MyApplication
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()
    val deviceType = rememberDeviceType()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        if (deviceType == DeviceType.TABLET) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                PanelSection(
                    availableBalance = uiState.availableBalance,
                )
                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        ExpensesSection(
                            expenses = uiState.expenses,
                            monthlyExpenses = uiState.monthlyExpenses
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Transacciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SearchBar(
                            onSearchQueryChange = { query ->
                                viewModel.updateSearchQuery(query)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            items(uiState.filteredTransactions) { transaction ->
                                TransactionItem(transaction = transaction)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelSection(
                        availableBalance = uiState.availableBalance,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    ExpensesSection(
                        expenses = uiState.expenses,
                        monthlyExpenses = uiState.monthlyExpenses
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Transacciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBar(
                        onSearchQueryChange = { query ->
                            viewModel.updateSearchQuery(query)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(uiState.filteredTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PanelSection(
    availableBalance: Int,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) 
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp) // Add padding to the Row
            ) {
                Text(
                    text = if (uiState.isHidden) "********" else "$ $availableBalance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { viewModel.toggleHidden() },
                ) {
                    Icon(
                        imageVector = if (uiState.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (uiState.isHidden) "Ocultar" else "Mostrar"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "Ingresar dinero",
                    onClick = { /* TODO */ }
                )
                ActionButton(
                    icon = Icons.Default.CompareArrows,
                    label = "Transferir dinero",
                    onClick = { /* TODO */ }
                )
                ActionButton(
                    icon = Icons.Default.Payments,
                    label = "Link de pago",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .background(Color(0xFF4CAF50), CircleShape)
                .padding(12.dp)
                .size(24.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ExpensesSection(
    expenses: Int,
    monthlyExpenses: List<MonthlyExpense>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Gastos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$$expenses",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "últimos 6 meses",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        val maxExpense = monthlyExpenses.maxOf { it.amount }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyExpenses.take(6).forEach { monthlyExpense ->
                val height = monthlyExpense.amount / maxExpense
                ExpenseBar(
                    height = height,
                    month = monthlyExpense.month,
                    amount = monthlyExpense.amount.toInt(),
                    barWidth = 40.dp
                )
            }
        }
    }
}

@Composable
fun ExpenseBar(
    height: Float,
    month: String,
    amount: Int,
    barWidth: Dp
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(barWidth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(100.dp * height)
                .background(Color(0xFF4CAF50), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .clickable { showDialog = true }
        )
        Text(
            text = month,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f), // Make dialog smaller on tablets
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gasto en $month",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$$amount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearchQueryChange: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onSearchQueryChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text("Buscar") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar(transaction.userName.first().toString())
            Column {
                Text(
                    text = transaction.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.getFormattedTimestamp(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "+$${transaction.amount}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Text(
                text = "Transferencia",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CircleAvatar(
    initial: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}