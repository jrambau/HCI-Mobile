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
import androidx.compose.ui.res.stringResource
import com.example.lupay.R
import components.ConfirmationDialog
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType

@Composable
fun InvestmentScreen(
    modifier: Modifier = Modifier,
    viewModel: InvestmentViewModel = viewModel(factory = InvestmentViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()
    var showInvestConfirmation by remember { mutableStateOf(false) }
    var showWithdrawConfirmation by remember { mutableStateOf(false) }
    var isInvestAction by remember { mutableStateOf(true) }
    val deviceType = rememberDeviceType()

    CustomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (deviceType == DeviceType.TABLET) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.investment),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Top row: Investment cards and chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left side: Investment cards in a column
                        Column(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InvestmentCard(
                                modifier = Modifier.fillMaxWidth(),
                                title = stringResource(R.string.Invested),
                                amount = uiState.myInvestment,
                                subtitle = {
                                    val percentageGain = if (uiState.myInvestment > 0) {
                                        ((uiState.currentValue - uiState.myInvestment) / uiState.myInvestment * 100).toInt()
                                    } else 0
                                    Text(
                                        text = "+$percentageGain%",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 14.sp
                                    )
                                }
                            )
                            
                            InvestmentCard(
                                modifier = Modifier.fillMaxWidth(),
                                title = stringResource(R.string.current_inve),
                                amount = uiState.currentValue,
                                subtitle = {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            )
                        }

                        // Right side: Chart
                        Card(
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Historial de inversiones",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Chart(
                                    chart = lineChart(),
                                    model = entryModelOf(uiState.chartData.map { FloatEntry(it.x, it.y) }),
                                    startAxis = rememberStartAxis(),
                                    bottomAxis = rememberBottomAxis(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }

                    // Bottom section: Investment actions
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.current_money),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$${uiState.currentBalance}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = if (isInvestAction) uiState.investmentAmount else uiState.withdrawalAmount,
                                onValueChange = { 
                                    if (isInvestAction) {
                                        viewModel.onInvestmentAmountChanged(it)
                                    } else {
                                        viewModel.onWithdrawalAmountChanged(it)
                                    }
                                },
                                label = { 
                                    Text(
                                        if (isInvestAction) 
                                            stringResource(R.string.to_invest)
                                        else 
                                            stringResource(R.string.to_withdraw)
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        isInvestAction = true
                                        showInvestConfirmation = true 
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isInvestAction) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.invest),
                                        color = Color.White
                                    )
                                }

                                Button(
                                    onClick = { 
                                        isInvestAction = false
                                        showWithdrawConfirmation = true 
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isInvestAction) 
                                            Color.Red 
                                        else 
                                            Color.Red.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.withdraw),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Original phone layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.investment),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Investment Summary Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Invested Card
                        InvestmentCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.Invested),
                            amount = uiState.myInvestment,
                            subtitle = {
                                val percentageGain = if (uiState.myInvestment > 0) {
                                    ((uiState.currentValue - uiState.myInvestment) / uiState.myInvestment * 100).toInt()
                                } else 0
                                Text(
                                    text = "+$percentageGain%",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp
                                )
                            }
                        )
                        
                        InvestmentCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.current_inve),
                            amount = uiState.currentValue,
                            subtitle = {
                                // Espacio vacío para mantener la misma altura
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        )
                    }

                    // Chart Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Historial de inversiones",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Chart(
                                chart = lineChart(),
                                model = entryModelOf(uiState.chartData.map { FloatEntry(it.x, it.y) }),
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.current_money),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$${uiState.currentBalance}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Shared Input Field
                            OutlinedTextField(
                                value = if (isInvestAction) uiState.investmentAmount else uiState.withdrawalAmount,
                                onValueChange = { 
                                    if (isInvestAction) {
                                        viewModel.onInvestmentAmountChanged(it)
                                    } else {
                                        viewModel.onWithdrawalAmountChanged(it)
                                    }
                                },
                                label = { 
                                    Text(
                                        if (isInvestAction) 
                                            stringResource(R.string.to_invest)
                                        else 
                                            stringResource(R.string.to_withdraw)
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Invest Button
                                Button(
                                    onClick = { 
                                        isInvestAction = true
                                        showInvestConfirmation = true 
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isInvestAction) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.invest),
                                        color = Color.White
                                    )
                                }

                                // Withdraw Button
                                Button(
                                    onClick = { 
                                        isInvestAction = false
                                        showWithdrawConfirmation = true 
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isInvestAction) 
                                            Color.Red 
                                        else 
                                            Color.Red.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.withdraw),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Confirmation Dialogs
            if (showInvestConfirmation) {
                ConfirmationDialog(
                    onConfirm = {
                        showInvestConfirmation = false
                        viewModel.onInvest()
                    },
                    onDismiss = { showInvestConfirmation = false },
                    title = stringResource(R.string.confirm_investment),
                    message = stringResource(R.string.confirm_invest_desc) + " $${uiState.investmentAmount}?"
                )
            }

            if (showWithdrawConfirmation) {
                ConfirmationDialog(
                    onConfirm = {
                        showWithdrawConfirmation = false
                        viewModel.onWithdraw()
                    },
                    onDismiss = { showWithdrawConfirmation = false },
                    title = stringResource(R.string.confirm_withdraw),
                    message = stringResource(R.string.confirm_withdraw_desc) + " $${uiState.withdrawalAmount}?"
                )
            }
        }
    }
}

@Composable
private fun InvestmentCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    subtitle: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "$${amount}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            subtitle?.invoke()
        }
    }
}