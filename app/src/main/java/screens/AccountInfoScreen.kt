package com.example.lupay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.foundation.shape.RoundedCornerShape



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Personal Information Fields
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
                ReadOnlyInfoField(
                    label = "Usuario",
                    value = uiState.userName
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ReadOnlyInfoField(
                    label = "Email",
                    value = uiState.email
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                PersonalInfoField(
                    label = "Teléfono",
                    value = uiState.phone ?: "No especificado",
                    onEditClick = { /* Handle edit */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ReadOnlyInfoField(
                    label = "CVU",
                    value = uiState.cvu
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                PersonalInfoField(
                    label = "Alias",
                    value = uiState.alias,
                    onEditClick = { /* Handle edit */ }
                )
            }
        }
    }
}

@Composable
private fun ReadOnlyInfoField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PersonalInfoField(
    label: String,
    value: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit $label",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 