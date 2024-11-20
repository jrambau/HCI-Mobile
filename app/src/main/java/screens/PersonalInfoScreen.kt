package com.example.lupay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lupay.ui.viewmodels.ProfileViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.lupay.MyApplication
import com.example.lupay.ui.Components.ReadOnlyInfoField
import com.example.lupay.ui.Components.PersonalInfoField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                text = "Información personal",
                style = MaterialTheme.typography.headlineMedium
            )
        }

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
                    label = "Nombre",
                    value = uiState.firstName
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ReadOnlyInfoField(
                    label = "Apellido",
                    value = uiState.lastName
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                ReadOnlyInfoField(
                    label = "Fecha de Nacimiento",
                    value = uiState.birthDate ?: "No especificado"
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                PersonalInfoField(
                    label = "Actividad Fiscal",
                    value = uiState.fiscalActivity ?: "No especificado",
                    onEditClick = { /* Handle edit */ }
                )
            }
        }
    }
}
