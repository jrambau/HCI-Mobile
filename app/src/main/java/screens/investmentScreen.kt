package com.example.lupay.ui.screens

import InvestmentViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.lupay.MyApplication
import theme.CustomTheme

@Composable
fun InvestmentScreen(
    viewModel: InvestmentViewModel = viewModel(factory = InvestmentViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication) )
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for confirmation dialogs
    var showInvestConfirmation by remember { mutableStateOf(false) }
    var showWithdrawConfirmation by remember { mutableStateOf(false) }

    CustomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp), // Lower content further
                verticalArrangement = Arrangement.spacedBy(20.dp) // Uniform spacing
            ) {
                // Header
                Text(
                    text = "Inversiones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Investment Details
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Consistent spacing between sections
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Invertido",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp
                            )
                            Text(
                                "$${uiState.myInvestment}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Valor Actual",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "$${uiState.currentValue}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val percentageGain = if (uiState.myInvestment > 0) {
                                    ((uiState.currentValue - uiState.myInvestment) / uiState.myInvestment * 100).toInt()
                                } else 0
                                Text(
                                    text = "+$percentageGain%",
                                    color = Color.Green,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Increased space before the chart for balance
                }

                // Chart
                Chart(
                    chart = lineChart(),
                    model = entryModelOf(uiState.chartData.map { FloatEntry(it.x, it.y) }),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(top = 16.dp) // Add space to ensure no crowding
                )

                // Balance and Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Balance
                    Column {
                        Text(
                            "Balance Actual",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "$${uiState.currentBalance}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Input Fields and Buttons
                    OutlinedTextField(
                        value = uiState.investmentAmount,
                        onValueChange = viewModel::onInvestmentAmountChanged,
                        label = { Text("Monto a invertir") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Button(
                        onClick = { showInvestConfirmation = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Invertir")
                    }

                    OutlinedTextField(
                        value = uiState.withdrawalAmount,
                        onValueChange = viewModel::onWithdrawalAmountChanged,
                        label = { Text("Monto a rescatar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Button(
                        onClick = { showWithdrawConfirmation = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Rescatar", color = Color.White)
                    }
                }
            }

            // Confirmation Dialog for Invest
            if (showInvestConfirmation) {
                AlertDialog(
                    onDismissRequest = { showInvestConfirmation = false },
                    title = { Text("Confirmar Inversión") },
                    text = { Text("¿Estás seguro de que deseas invertir $${uiState.investmentAmount}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showInvestConfirmation = false
                            viewModel.onInvest()
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showInvestConfirmation = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Confirmation Dialog for Withdraw
            if (showWithdrawConfirmation) {
                AlertDialog(
                    onDismissRequest = { showWithdrawConfirmation = false },
                    title = { Text("Confirmar Rescate") },
                    text = { Text("¿Estás seguro de que deseas rescatar $${uiState.withdrawalAmount}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showWithdrawConfirmation = false
                            viewModel.onWithdraw()
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showWithdrawConfirmation = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}