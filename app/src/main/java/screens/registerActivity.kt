import Components.InputField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.lupay.MyApplication
import theme.CustomTheme
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState = viewModel.uiState
    var showPassword by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    CustomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Name Input
                InputField(
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = "Ingrese su nombre",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                // Lastname Input
                InputField(
                    value = viewModel.lastname,
                    onValueChange = { viewModel.onLastNameChanged(it) },
                    label = "Ingrese su apellido",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                // Email Input
                InputField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = "Ingrese su mail",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password Input
                InputField(
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = "Ingrese su contraseña",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    isPassword = true,
                    showPassword = showPassword,
                    onPasswordVisibilityChange = { showPassword = !showPassword },
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm Password Input
                InputField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    label = "Vuelva a ingresar la contraseña",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    isPassword = true,
                    showPassword = showPassword,
                    onPasswordVisibilityChange = { showPassword = !showPassword },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.onRegisterClicked() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Ingrese aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                if (uiState.isFetching) {
                    CircularProgressIndicator()
                } else {
                    if (uiState.error != null) {
                        Text(
                            text = "Error: ${uiState.error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (uiState.success) {
                        onNavigateToLogin()
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = viewModel.birthDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                viewModel.onBirthDateChanged(selectedDate)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
