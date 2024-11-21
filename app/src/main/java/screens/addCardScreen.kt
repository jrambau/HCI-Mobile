package com.example.lupay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupay.MyApplication
import com.example.lupay.ui.viewmodels.CreditCardViewModel
import components.CreditCard
import network.model.NetworkCard

@Composable
fun AddCardScreen(
    navController: NavHostController,
    viewModel: CreditCardViewModel = viewModel(factory = CreditCardViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreditCard(
            cardNumber = cardNumber,
            cardName = cardName,
            cardExpiry = cardExpiry,
            cvv = cvv,
            isHidden = false
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Número de Tarjeta", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Ingresar...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Nombre del titular", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardName,
            onValueChange = { cardName = it},
            label = { Text("Ingresar") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Vencimiento", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cardExpiry,
            onValueChange = { cardExpiry = it },
            label = { Text("MM/AA") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Código de seguridad", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cvv,
            onValueChange = { cvv = it },
            label = { Text("***") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                viewModel.addNewCard(NetworkCard(id=null, number=cardNumber, fullName=cardName, expirationDate=cardExpiry, cvv=cvv, type = "CREDIT", createdAt=null, updatedAt=null))
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Agregar")
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(it, color = Color.Red)
        }
    }
}

