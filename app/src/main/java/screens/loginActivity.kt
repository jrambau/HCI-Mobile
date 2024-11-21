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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var confirmationCode by remember { mutableStateOf("") }
    var confirmationEmail by remember { mutableStateOf("") }

    CustomTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Iniciar sesion",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                InputField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = "Ingrese su mail",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isPassword = false,
                    modifier = Modifier.fillMaxWidth()
                )

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Confirmar cuenta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = { showResetPasswordDialog = true },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "¿Olvido su contraseña?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.onLoginClicked() },
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
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.Black)
                ) {
                    Text("Registrarse", color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                if (uiState.isFetching) {
                    CircularProgressIndicator()
                } else {
                    uiState.error?.let { error ->
                        Text(
                            text = "Error: ${error.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    uiState.successMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color.Green,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirmar cuenta") },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ingrese el código de confirmación:")
                        TextField(
                            value = confirmationCode,
                            onValueChange = { confirmationCode = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        if (uiState.error != null) {
                            Text(
                                text = uiState.error.message ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        if (uiState.successMessage != null) {
                            Text(
                                text = uiState.successMessage!!,
                                color = Color.Green,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                    }}
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onConfirmAccount(confirmationCode)
                        },
                        enabled = !uiState.isFetching
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmationDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showResetPasswordDialog) {
            var resetEmail by remember { mutableStateOf("") }
            var resetCode by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var resetStep by remember { mutableStateOf(0) }

            AlertDialog(
                onDismissRequest = {
                    showResetPasswordDialog = false
                    resetStep = 0
                },
                title = { Text("Restablecer contraseña") },
                text = {
                    Column {
                        when (resetStep) {
                            0 -> {
                                Text("Ingrese su correo electrónico:")
                                TextField(
                                    value = resetEmail,
                                    onValueChange = { resetEmail = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { resetStep = 1 },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Ya tengo mi código")
                                }
                            }
                            1 -> {
                                Text("Ingrese el código recibido por correo:")
                                TextField(
                                    value = resetCode,
                                    onValueChange = { resetCode = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ingrese la nueva contraseña:")
                                TextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            }
                        }
                        if (uiState.error != null) {
                            Text(
                                text = uiState.error.message ?: "Error desconocido",
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        if (uiState.successMessage != null) {
                            Text(
                                text = uiState.successMessage!!,
                                color = Color.Green,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            when (resetStep) {
                                0 -> {
                                    viewModel.onForgotPasswordClicked(resetEmail)
                                }
                                1 -> {
                                    viewModel.onResetPassword(resetEmail, resetCode, newPassword)
                                }
                            }
                        },
                        enabled = !uiState.isFetching
                    ) {
                        Text(if (resetStep == 0) "Enviar código" else "Restablecer")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showResetPasswordDialog = false
                        resetStep = 0
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToMain()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null && uiState.successMessage!!.contains("código de recuperación")) {
            showResetPasswordDialog = true
        }
    }
}