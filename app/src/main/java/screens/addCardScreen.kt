package com.example.lupay.ui.screens

import CreditCard
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lupay.ui.viewmodels.CreditCardViewModel
import com.example.lupay.ui.viewmodels.NewCardData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(navController: NavController, viewModel: CreditCardViewModel) {
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVV by remember { mutableStateOf("") }

    // Observing the loading and error states from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Nueva tarjeta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card Preview
            CreditCard(
                cardNumber = cardNumber,
                cardName = cardName,
                cardExpiry = cardExpiry,
                cvv = cardCVV,
                isHidden = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Form Fields
            Column {
                Text(
                    "Número de tarjeta",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Ingresar...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Nombre del titular de la tarjeta",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Vencimiento",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = { if (it.length <= 5) cardExpiry = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("MM/AA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Código de seguridad",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = cardCVV,
                    onValueChange = { if (it.length <= 3) cardCVV = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Ingresar...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error message if any
                if (error != null) {
                    Text(error!!, color = Color.Red)
                }

                // Save Card Button
                Button(
                    onClick = {
                        if (cardNumber.isNotBlank() && cardName.isNotBlank() && cardExpiry.isNotBlank() && cardCVV.isNotBlank()) {
                            val newCardData = NewCardData(
                                cardNumber = cardNumber,
                                cardName = cardName,
                                cardExpiry = cardExpiry,
                                cvv = cardCVV
                            )
                            viewModel.addNewCard(newCardData)
                            navController.popBackStack()
                        } else {
                            // Display an error message when the form is incomplete
                           viewModel.updateError( "Por favor complete todos los campos");
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text("Guardar")
                }

                // Show loading spinner if the ViewModel is loading
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
