package com.example.lupay.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupay.MyApplication
import com.example.lupay.R
import com.example.lupay.ui.viewmodels.CreditCardViewModel
import components.CreditCard
import network.model.NetworkCard
import java.text.SimpleDateFormat
import java.util.*
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    navController: NavHostController,
    viewModel: CreditCardViewModel = viewModel(factory = CreditCardViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val deviceType = rememberDeviceType()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.add_card), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        content = { paddingValues ->
            if (deviceType == DeviceType.TABLET) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .width(600.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AddCardContent(
                            navController = navController,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                        )
                    }
                }
            } else {
                AddCardContent(
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    )
}



@Composable
private fun AddCardContent(
    navController: NavHostController,
    viewModel: CreditCardViewModel,
    modifier: Modifier = Modifier
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()
    val addCardSuccess by viewModel.addCardSuccess.collectAsState()

    LaunchedEffect(addCardSuccess) {
        if (addCardSuccess) {
            navController.popBackStack()
        }
    }

    // Luhn Algorithm
    fun luhnAlgorithm(cardNumber: String): Boolean {
        var sum = 0
        var shouldDouble = false
        for (i in cardNumber.length - 1 downTo 0) {
            var digit = cardNumber[i].digitToInt()
            if (shouldDouble) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            sum += digit
            shouldDouble = !shouldDouble
        }
        return sum % 10 == 0
    }

    // Validate Credit Card Number - Luhn Algorithm
    fun isValidCardNumber(cardNumber: String): Boolean {
//        return cardNumber.length == 16 && luhnAlgorithm(cardNumber)
        return cardNumber.length == 16;
    }

    // Validate Expiry Date - MM/YY format
    fun isValidExpiryDate(expiry: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("MM/yy", Locale.getDefault())
            sdf.isLenient = false
            val expirationDate = sdf.parse(expiry)
            val currentDate = Date()
            expirationDate != null && expirationDate.after(currentDate)
        } catch (e: Exception) {
            false
        }
    }

    val err_card_num = stringResource(id = R.string.invalid_card_num)
    val err_card_date = stringResource(id = R.string.invalid_date)
    val err_cvv = stringResource(id = R.string.cvv_invalid)

    // Validate Inputs
    fun validateInputs(): Boolean {
        return when {
            !isValidCardNumber(cardNumber) -> {
                errorMessage = err_card_num
                showErrorDialog = true
                false
            }
            !isValidExpiryDate(cardExpiry) -> {
                errorMessage = err_card_date
                showErrorDialog = true
                false
            }
            cvv.length != 3 -> {
                errorMessage = err_cvv
                showErrorDialog = true
                false
            }
            else -> true
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreditCard(
            cardNumber = cardNumber,
            cardName = cardName,
            cardExpiry = cardExpiry,
            isHidden = false
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Credit Card Number Field
        Text(text = stringResource(id = R.string.card_number), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it.take(16) },
            label = { Text(stringResource(id = R.string.enter)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Cardholder Name Field
        Text(text = stringResource(id = R.string.card_owner), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardName,
            onValueChange = { cardName = it },
            label = { Text(stringResource(id = R.string.enter)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Expiry Date Field
        Text(text = stringResource(id = R.string.expiration), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardExpiry,
            onValueChange = {
                // Add slash ("/") after 2 digits and ensure it's limited to 5 characters (MM/YY format)
                var formattedExpiry = it.replace(" ", "").take(5)
                if (formattedExpiry.length > 2 && formattedExpiry[2] != '/') {
                    formattedExpiry = formattedExpiry.substring(0, 2) + "/" + formattedExpiry.substring(2)
                }
                cardExpiry = formattedExpiry
            },
            label = { Text(stringResource(id = R.string.date)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Add Card Button
        Button(
            onClick = {
                if (validateInputs()) {
                    viewModel.addNewCard(NetworkCard(id = null, number = cardNumber, fullName = cardName, expirationDate = cardExpiry, cvv = cvv, type = "CREDIT", createdAt = null, updatedAt = null))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(stringResource(id = R.string.add))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(it, color = Color.Red)
        }
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(stringResource(id = R.string.error)) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}
