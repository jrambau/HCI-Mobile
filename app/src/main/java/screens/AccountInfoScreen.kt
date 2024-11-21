package com.example.lupay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lupay.ui.viewmodels.ProfileViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.lupay.MyApplication
import components.ReadOnlyInfoField
import components.PersonalInfoField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Datos de tu cuenta",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Account Information Fields
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Display user information
                ReadOnlyInfoField(
                    label = "Usuario",
                    value = uiState.userName.ifBlank { "No especificado" }
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                ReadOnlyInfoField(
                    label = "Email",
                    value = uiState.email.ifBlank { "No especificado" }
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                ReadOnlyInfoField(
                    label = "CBU",
                    value = uiState.cbu?.ifBlank { "No especificado" } ?: "No especificado"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Alias - This can be edited, so using PersonalInfoField instead of ReadOnlyInfoField
                PersonalInfoField(
                    label = "Alias",
                    value = uiState.alias.ifBlank { "No especificado" },
                    onEditClick = { /* Handle alias edit */ }
                )
            }
        }
    }
}
