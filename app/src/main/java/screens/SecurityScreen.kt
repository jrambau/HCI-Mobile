import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lupay.MyApplication
import com.example.lupay.ui.viewmodels.ProfileViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.example.lupay.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Assuming the email is available from the UI state
    val passwordChangeSuccess = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Custom Back Button
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
                text = stringResource(id = R.string.security),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Text(
            text = stringResource(id = R.string.change_pass),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // New password input
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.n_pass),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
            )
        }
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(stringResource(id = R.string.n_pass)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm password input
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.confirm_pass),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
            )
        }
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(id = R.string.confirm_pass)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val err= stringResource(id = R.string.match_pass)
        // Submit button to reset password
        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    // Use the setError method to update the error message
                    viewModel.setError(err)
                } else {
                    viewModel.resetPassword(email, newPassword, "resetCode") // Pass actual reset code if needed
                    passwordChangeSuccess.value = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(id = R.string.update_pass))
        }

        // Show loading state if password is being updated
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Show success or error message
        if (passwordChangeSuccess.value) {
            Text(
                text = stringResource(id = R.string.success_pass),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
