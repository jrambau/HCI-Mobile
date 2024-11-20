package com.example.lupay.ui.screens

import Components.InputField
import LoginViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.lupay.MyApplication
import theme.CustomTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState = viewModel.uiState
    var showPassword by remember { mutableStateOf(false) }

    // Wrapping the entire screen with the CustomTheme composable
    CustomTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)  // Use the background color from the theme
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Set the title color dynamically based on the theme
                Text(
                    text = "Iniciar sesion",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground // White in dark mode, black in light mode
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Reuse InputField composable for Email
                InputField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = "Ingrese su mail",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isPassword = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Reuse InputField composable for Password
                InputField(
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = "Ingrese su contraseña",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true,
                    showPassword = showPassword,
                    onPasswordVisibilityChange = { showPassword = !showPassword },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { /* Acción al hacer clic en el botón */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "¿Olvido su contraseña?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToMain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Ingresar")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.Black) // Black in light mode, lighter in dark mode
                ) {
                    Text("Registrarse", color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White) // White text on black button in light mode
                }

                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                viewModel.loginResult?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))
//                    when (result) {
//                        is LoginResult.Success -> {
//                            Text(
//                                text = "Login exitoso",
//                                color = Color.Green
//                            )
//                        }
//                        is LoginResult.Error -> {
//                            Text(
//                                text = result.message,
//                                color = Color.Red
//                            )
//                        }
//                    }
                }
            }
        }
    }
}

