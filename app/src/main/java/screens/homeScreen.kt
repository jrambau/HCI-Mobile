import android.content.ClipboardManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.example.lupay.MyApplication
import network.model.NetworkCard
import network.model.NetworkPaymentInfo
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()
    var generalUiState = viewModel.generalUiState

    var showTransferDialog by remember { mutableStateOf(false) }
    var showPaymentLinkDialog by remember { mutableStateOf(false) }
    var showRechargeDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showPaymentLinkDetailsDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
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
                    onTransferClick = { showTransferDialog = true },
                    onPaymentLinkClick = { showPaymentLinkDialog = true },
                    onRechargeClick = { showRechargeDialog = true },
                    viewModel = viewModel
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
                TransactionItem(
                    transaction = transaction,
                    onClick = { selectedTransaction = transaction }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showTransferDialog) {
            TransferDialog(
                onDismiss = { showTransferDialog = false },
                onTransfer = { amount, email, description ->
                    viewModel.transferMoney(amount, email, description)
                    showTransferDialog = false
                },
                viewModel = viewModel
            )
        }

        if (showPaymentLinkDialog) {
            PaymentLinkDialog(
                onDismiss = { showPaymentLinkDialog = false },
                onGenerate = { amount, description ->
                    viewModel.generatePaymentLink(amount, description)
                },
                onPay = { linkUuid ->
                    viewModel.getPaymentLinkDetails(linkUuid)
                    showPaymentLinkDetailsDialog = true
                },
                generatedLink = uiState.generatedPaymentLink
            )
        }

        if (showPaymentLinkDetailsDialog) {
            PaymentLinkDetailsDialog(
                paymentInfo = viewModel.paymentLinkDetails,
                onDismiss = { showPaymentLinkDetailsDialog = false },
                onPay = { linkUuid, paymentMetod, cardId ->
                    viewModel.payByLink(linkUuid,paymentMetod,cardId)
                    showPaymentLinkDetailsDialog = false
                },
                viewModel = viewModel
            )
        }

        if (showRechargeDialog) {
            RechargeDialog(
                onDismiss = { showRechargeDialog = false },
                onRecharge = { amount ->
                    viewModel.rechargeMoney(amount)
                    showRechargeDialog = false
                }
            )
        }

        if (selectedTransaction != null) {
            TransactionDetailsDialog(
                transaction = selectedTransaction!!,
                onDismiss = { selectedTransaction = null }
            )
        }

        if (generalUiState.error != null) {
            ErrorDialog(
                error = generalUiState.error!!,
                onDismiss = {
                    viewModel.clearError()
                }
            )
        }

        if (generalUiState.successMessage != null) {
            SuccessDialog(
                message = generalUiState.successMessage!!,
                onDismiss = {
                    viewModel.clearSuccessMessage()
                }
            )
        }
    }
}

@Composable
fun PanelSection(
    availableBalance: Int,
    onTransferClick: () -> Unit,
    onPaymentLinkClick: () -> Unit,
    onRechargeClick: () -> Unit,
    viewModel: HomeViewModel
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
                modifier = Modifier.padding(top = 8.dp)
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
                    onClick = onRechargeClick
                )
                ActionButton(
                    icon = Icons.Default.CompareArrows,
                    label = "Transferir dinero",
                    onClick = onTransferClick
                )
                ActionButton(
                    icon = Icons.Default.Payments,
                    label = "Link de pago",
                    onClick = onPaymentLinkClick
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

    if (monthlyExpenses.isEmpty()) {
        Text(
            text = "No hay datos de gastos disponibles",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    } else {
        val maxExpense = monthlyExpenses.maxOf { it.amount }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyExpenses.takeLast(6).forEach { expense ->
                ExpenseBar(
                    height = (expense.amount / maxExpense),
                    month = expense.month,
                    amount = expense.amount.toInt()
                )
            }
        }
    }
}

@Composable
fun ExpenseBar(
    height: Float,
    month: String,
    amount: Int
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(80.dp * height)
                .background(Color(0xFF4CAF50), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .clickable { showDialog = true }
        )
        Text(
            text = month,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
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
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                text = "$${abs(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncoming) Color.Green else Color.Red
            )
            Text(
                text = transaction.type,
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

@Composable
fun TransactionDetailsDialog(transaction: Transaction, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalles de la transacción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TransactionDetailItem("Tipo", transaction.type)
                TransactionDetailItem("Monto", "$${abs(transaction.amount)}")
                TransactionDetailItem("Fecha", transaction.getFormattedTimestamp())
                TransactionDetailItem("Usuario", transaction.userName)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun TransactionDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(onDismiss: () -> Unit, onTransfer: (Double, String, String) -> Unit, viewModel: HomeViewModel) {
    var amount by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Transferir dinero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email del destinatario") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.setPaymentMethod(HomeViewModel.PaymentMethod.WALLET) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedPaymentMethod == HomeViewModel.PaymentMethod.WALLET)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Cuenta")
                    }
                    Button(
                        onClick = { viewModel.setPaymentMethod(HomeViewModel.PaymentMethod.CARD) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedPaymentMethod == HomeViewModel.PaymentMethod.CARD)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Tarjeta")
                    }
                }
                if (viewModel.selectedPaymentMethod == HomeViewModel.PaymentMethod.CARD) {
                    Spacer(modifier = Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            readOnly = true,
                            value = viewModel.selectedCard?.number?.let { it } ?: "Seleccionar tarjeta",
                            onValueChange = { },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            viewModel.cards.forEach { card ->
                                DropdownMenuItem(
                                    text = { Text(card.number) },
                                    onClick = {
                                        viewModel.updateSelectedCard(card)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble != null && email.isNotBlank() && description.isNotBlank()) {
                            onTransfer(amountDouble, email, description)
                        }
                    }
                ) {
                    Text("Transferir")
                }
            }
        }
    }
}

@Composable
fun PaymentLinkDialog(
    onDismiss: () -> Unit,
    onGenerate: (Double, String) -> Unit,
    onPay: (String) -> Unit,
    generatedLink: String?
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var linkUuid by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(true) }
    val clipboardManager: androidx.compose.ui.platform.ClipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isGenerating) "Generar link de pago" else "Pagar con link",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isGenerating) {
                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Monto") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") }
                    )
                    if (generatedLink != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Link generado: $generatedLink")
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedLink))
                            }
                        ) {
                            Text("Copiar al portapapeles")
                        }
                    }
                } else {
                    TextField(
                        value = linkUuid,
                        onValueChange = { linkUuid = it },
                        label = { Text("UUID del link de pago") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isGenerating = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGenerating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Generar")
                    }
                    Button(
                        onClick = { isGenerating = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isGenerating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Pagar")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isGenerating) {
                            val amountDouble = amount.toDoubleOrNull()
                            if (amountDouble != null && description.isNotBlank()) {
                                onGenerate(amountDouble, description)
                            }
                        } else {
                            if (linkUuid.isNotBlank()) {
                                onPay(linkUuid)
                            }
                        }
                    }
                ) {
                    Text(if (isGenerating) "Generar link" else "Obtener detalles")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentLinkDetailsDialog(
    paymentInfo: NetworkPaymentInfo?,
    onDismiss: () -> Unit,
    onPay: (String, HomeViewModel.PaymentMethod, Int?) -> Unit,
    viewModel: HomeViewModel
) {
    var selectedPaymentMethod by remember { mutableStateOf<HomeViewModel.PaymentMethod>(HomeViewModel.PaymentMethod.WALLET) }
    var selectedCard by remember { mutableStateOf<NetworkCard?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalles del pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                paymentInfo?.let {
                    Text("Monto: $${it.amount}")
                    Text("Pagar a: ${it.receiver?.firstName} ${it.receiver?.lastName}")
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { selectedPaymentMethod = HomeViewModel.PaymentMethod.WALLET },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPaymentMethod == HomeViewModel.PaymentMethod.WALLET)
                                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text("Cuenta")
                        }
                        Button(
                            onClick = { selectedPaymentMethod = HomeViewModel.PaymentMethod.CARD },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPaymentMethod == HomeViewModel.PaymentMethod.CARD)
                                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text("Tarjeta")
                        }
                    }

                    if (selectedPaymentMethod == HomeViewModel.PaymentMethod.CARD) {
                        Spacer(modifier = Modifier.height(8.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                readOnly = true,
                                value = selectedCard?.number ?: "Seleccionar tarjeta",
                                onValueChange = { },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                viewModel.cards.forEach { card ->
                                    DropdownMenuItem(
                                        text = { Text(card.number) },
                                        onClick = {
                                            selectedCard = card
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onPay(
                                it.linkUuid ?: "",
                                selectedPaymentMethod,
                                if (selectedPaymentMethod == HomeViewModel.PaymentMethod.CARD) selectedCard?.id else null
                            )
                        }
                    ) {
                        Text("Pagar")
                    }
                } ?: Text("No se pudieron cargar los detalles del pago")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun RechargeDialog(onDismiss: () -> Unit, onRecharge: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ingresar dinero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble != null) {
                            onRecharge(amountDouble)
                        }
                    }
                ) {
                    Text("Recargar")
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(error: Error, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(error.message ?: "An unknown error occurred") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éxito") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
