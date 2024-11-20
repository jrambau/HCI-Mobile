package com.example.lupay.ui.screens

import android.content.pm.ActivityInfo
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
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import InvestmentViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun InvestmentScreen(
    viewModel: InvestmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp)
    ) {
        // Investment Details
        Text(
            text = "Inversiones",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Mi Inversion")
                Text(
                    text = "$${uiState.myInvestment}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text("Valor Actual")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${uiState.currentValue}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val percentageGain = ((uiState.currentValue - uiState.myInvestment) / uiState.myInvestment * 100).toInt()
                    Text(
                        text = "+$percentageGain%",
                        color = Color.Green,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (uiState.isChartExpanded) 400.dp else 200.dp)
                .padding(vertical = 16.dp)
        ) {
            Chart(
                chart = lineChart(),
                model = entryModelOf(uiState.chartData.map { FloatEntry(it.x, it.y) }),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = {
                    viewModel.toggleChartExpansion()
                    (context as ComponentActivity).requestedOrientation =
                        if (uiState.isChartExpanded) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Expand Chart"
                )
            }
        }

        // Balance
        Text(
            text = "Balance Actual",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "$${uiState.currentBalance}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Input Fields
        OutlinedTextField(
            value = uiState.investmentAmount,
            onValueChange = { viewModel.onInvestmentAmountChanged(it) },
            label = { Text("Monto a invertir") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = { viewModel.onInvest() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Invertir")
        }

        OutlinedTextField(
            value = uiState.withdrawalAmount,
            onValueChange = { viewModel.onWithdrawalAmountChanged(it) },
            label = { Text("Monto a rescatar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = { viewModel.onWithdraw() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Rescatar")
        }
    }
}