package com.example.lupay.ui.screens

import InvestmentViewModel
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.OrientationEventListener
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.patrykandpatrick.vico.core.entry.FloatEntry
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun InvestmentScreen(
    viewModel: InvestmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Log.d("ContextCheck", "Context: $context")

    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    var isLandscape by remember { mutableStateOf(configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) }


    DisposableEffect(configuration) {
        val orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                isLandscape = orientation in 60..300
            }
        }
        orientationEventListener.enable()
        onDispose {
            orientationEventListener.disable()
        }
    }

    LaunchedEffect(isLandscape) {
        activity?.requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

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
                    .height(if (isLandscape) screenHeight * 0.7f else screenHeight * 0.3f)
                    .padding(vertical = 16.dp)
            ) {
                Chart(
                    chart = lineChart(),
                    model = entryModelOf(uiState.chartData.map { FloatEntry(it.x, it.y) }),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier.fillMaxSize()
                )
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
}