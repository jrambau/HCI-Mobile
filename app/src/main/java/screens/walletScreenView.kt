package com.example.lupay.ui.screens

import CreditCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lupay.ui.viewmodels.CreditCardViewModel
import theme.CustomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: CreditCardViewModel = viewModel()
) {
    CustomTheme {
        val cards by viewModel.cards.collectAsState()
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(

        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // My Cards Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* Handle add card */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Eye Icon Button for toggling visibility, moved below the "Agregar" button
                IconButton(
                    onClick = { viewModel.toggleHidden() },
                    modifier = Modifier
                        .align(Alignment.End) // Aligns the button to the end (right side)
                ) {
                    Icon(
                        if (uiState.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle visibility"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Credit Card Display
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cards) { card ->
                        Box(
                            modifier = Modifier
                                .width(350.dp) // Ensure consistent card width
                                .padding(8.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Credit Card Component
                                CreditCard(
                                    cardNumber = card.cardNumber,
                                    cardName = card.cardName,
                                    cardExpiry = card.cardExpiry,
                                    cvv = card.cvv,
                                    isHidden = uiState.isHidden,
                                    modifier = Modifier
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Circular Red Delete Button
                                IconButton(
                                    onClick = { /* Handle remove card */ },
                                    modifier = Modifier
                                        .size(48.dp) // Ensures the button is a circle
                                        .clip(CircleShape) // Clips the button into a circle
                                        .background(Color.Red) // Background color red
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Card",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}