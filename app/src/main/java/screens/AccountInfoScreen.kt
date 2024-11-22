package com.example.lupay.ui.screens

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.lupay.R
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()

    // Trigger fetchUserData when the screen is first loaded
    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

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
                text = stringResource(id = R.string.user_info),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Show a loading indicator if data is still being fetched
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            // Display an error message if there was an error fetching the data
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
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
                    val configuration = LocalConfiguration.current
                    val dividerPadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4.dp else 12.dp

                    ReadOnlyInfoField(
                        label = stringResource(id = R.string.user),
                        value = uiState.userName.ifBlank { stringResource(id = R.string.no_specified)}
                    )

                    Divider(modifier = Modifier.padding(vertical = dividerPadding))

                    ReadOnlyInfoField(
                        label = stringResource(id = R.string.mail),
                        value = uiState.email.ifBlank { stringResource(id = R.string.no_specified) }
                    )

                    Divider(modifier = Modifier.padding(vertical = dividerPadding))

                    ReadOnlyInfoField(
                        label = stringResource(id = R.string.cbu),
                        value = uiState.cbu.ifBlank { stringResource(id = R.string.no_specified) }
                    )

                    Divider(modifier = Modifier.padding(vertical = dividerPadding))

                    PersonalInfoField(
                        label = stringResource(id = R.string.alias),
                        value = uiState.alias.ifBlank { stringResource(id = R.string.no_specified) },
                        onEditClick = { alias: String ->
                            viewModel.updateAlias(alias)
                        },
                    )
                }
            }
        }
    }
}
