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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Use paddingValues here for correct spacing
                    .padding(start = 16.dp, end = 16.dp) // Optional: Add side padding
            ) {
                // Spacer to push content down a bit
                Spacer(modifier = Modifier.height(65.dp)) // Adjust this height as needed

                // My Cards Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Eye Icon Button for toggling visibility (left)
                    IconButton(
                        onClick = { viewModel.toggleHidden() },
                        modifier = Modifier.size(48.dp) // Optional: Adjust size if needed
                    ) {
                        Icon(
                            if (uiState.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle visibility"
                        )
                    }

                    // Add Button (right)
                    Button(
                        onClick = { /* Handle add card */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                // Spacer between buttons and cards
                Spacer(modifier = Modifier.height(16.dp)) // Adds space between buttons and cards

                // Credit Card Display Section
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
