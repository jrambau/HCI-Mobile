import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lupay.ui.viewmodels.ProfileViewModel
import androidx.navigation.NavController
import com.example.lupay.MyApplication
import com.example.lupay.R
import components.ConfirmationDialog
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.navigationBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogOutConf by remember { mutableStateOf(false) }

    // Add configuration to check orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Fetch user data on screen load
    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = if (isLandscape) 0.dp else 16.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        // Show loading indicator if data is being fetched
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // Show error if fetching failed
        if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@Column
        }

        Spacer(modifier = Modifier.height((-4).dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 0.dp,
                    bottom = if (isLandscape) 8.dp else 24.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (uiState.userName.isNotBlank()) {
                    val names = uiState.userName.split(" ")
                    names.joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                } else {
                    stringResource(id = R.string.charging)
                },
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            // Show logout button here if in landscape
            if (isLandscape) {
                TextButton(
                    onClick = { showLogOutConf = true },
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Profile menu items
        ProfileMenuItem(
            title = stringResource(id = R.string.personal_info),
            description = stringResource(id = R.string.personal_info_desc),
            onClick = { navController.navigate("personal_info") },
        )

        // Fix: Show email data in account info
        ProfileMenuItem(
            title = stringResource(id = R.string.user_info),
            description = stringResource(id = R.string.user_info_desc),
            onClick = { navController.navigate("account_info") }
        )

        ProfileMenuItem(
            title = stringResource(id = R.string.security),
            description = stringResource(id = R.string.security_desc),
            onClick = { navController.navigate("security_screen") } // Navigate to Seguridad screen
        )

        // Show logout button at bottom only if NOT in landscape
        if (!isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showLogOutConf = true },
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if(showLogOutConf){
        ConfirmationDialog(
            onConfirm = {navController.navigate("login")},
            onDismiss = {navController.navigate("profile")},
            title = stringResource(id = R.string.log_out_confirm),
            message = stringResource(id = R.string.log_out_desc)
        )
        showLogOutConf = false
    }
}


@Composable
fun ProfileMenuItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
