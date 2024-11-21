package com.example.lupay.ui.screens

import components.ConfirmationDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lupay.ui.viewmodels.CreditCardViewModel
import theme.CustomTheme
import androidx.navigation.NavHostController
import com.example.lupay.MyApplication
import components.CreditCard
import kotlinx.coroutines.flow.collectLatest
import network.model.NetworkCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CreditCardViewModel = viewModel(factory = CreditCardViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    CustomTheme {
        val cards = remember { mutableStateListOf<NetworkCard>() }
        val uiState by viewModel.uiState.collectAsState()
        var delete by remember { mutableStateOf(false) }
        var selectedCardId by remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(Unit) {
            viewModel.cards.collectLatest { newCards ->
                cards.clear()
                cards.addAll(newCards)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.fetchCards()
        }

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(65.dp))

                // Row for "Add Card" button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.navigate("add_card") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LazyRow to display cards
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cards) { card ->
                        Box(
                            modifier = Modifier
                                .width(350.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CreditCard(
                                    cardNumber = card.number,
                                    cardName = card.fullName,
                                    cardExpiry = card.expirationDate,
                                    isHidden = false,  // Always show the data
                                    modifier = Modifier
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Delete Button
                                IconButton(
                                    onClick = {
                                        selectedCardId = card.id
                                        delete = true
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
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

                // Confirmation Dialog for Deletion
                if (delete) {
                    ConfirmationDialog(
                        onConfirm = {
                            selectedCardId?.let { viewModel.deleteCard(it) }
                            delete = false
                        },
                        onDismiss = {
                            delete = false
                        },
                        title = "Eliminar tarjeta",
                        message = "¿Estás seguro de eliminar la tarjeta?"
                    )
                }
            }
        }
    }
}
