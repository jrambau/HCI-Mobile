package com.example.lupay.ui.screens

import components.InputField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.lupay.MyApplication
import com.example.lupay.R
import theme.CustomTheme
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration

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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    CustomTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(id = R.string.login_title),
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Start,
                            color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.welcome),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (viewModel.isLoading) {
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

                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy((-6).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        InputField(
                            value = viewModel.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = stringResource(id = R.string.enter_mail),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isPassword = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        InputField(
                            value = viewModel.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = stringResource(id = R.string.enter_pass),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isPassword = true,
                            showPassword = showPassword,
                            onPasswordVisibilityChange = { showPassword = !showPassword },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { showConfirmationDialog = true },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.validate),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(
                                onClick = { showResetPasswordDialog = true },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.forgot_pass),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.onLoginClicked() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text(stringResource(id = R.string.login_title), color = Color.White)
                        }

                        Button(
                            onClick = onNavigateToRegister,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemInDarkTheme()) 
                                    MaterialTheme.colorScheme.surfaceVariant 
                                else Color.Black
                            )
                        ) {
                            Text(
                                stringResource(id = R.string.register_title),
                                color = if (isSystemInDarkTheme()) 
                                    MaterialTheme.colorScheme.onSurface 
                                else Color.White
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.login_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.welcome),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    InputField(
                        value = viewModel.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = stringResource(id = R.string.enter_mail),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isPassword = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InputField(
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = stringResource(id = R.string.enter_pass),
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
                                text = stringResource(id = R.string.validate),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(
                            onClick = { showResetPasswordDialog = true },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = stringResource(id = R.string.forgot_pass),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
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
                        Text(stringResource(id = R.string.login_title), color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.Black)
                    ) {
                        Text(stringResource(id = R.string.register_title), color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White)
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
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(stringResource(id = R.string.validate)) },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.val_code))
                        TextField(
                            value = confirmationCode,
                            onValueChange = { confirmationCode = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        if (uiState.error != null) {
                            Text(
                                text = uiState.error.message ?: stringResource(id = R.string.unkown_error),
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
                            viewModel.onConfirmAccount(confirmationCode)
                        },
                        enabled = !uiState.isFetching,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmationDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(stringResource(id = R.string.cancel))
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
                title = { Text(stringResource(id = R.string.reset_pass)) },
                text = {
                    Column {
                        when (resetStep) {
                            0 -> {
                                Text(stringResource(id = R.string.enter_mail))
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
                                    Text(stringResource(id = R.string.got_code))
                                }
                            }
                            1 -> {
                                Text(stringResource(id = R.string.write_code))
                                TextField(
                                    value = resetCode,
                                    onValueChange = { resetCode = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(stringResource(id = R.string.enter_pass))
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
                                text = uiState.error.message ?: stringResource(id = R.string.unkown_error),
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
                        enabled = !uiState.isFetching,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (resetStep == 0) stringResource(id = R.string.send_code) else stringResource(id = R.string.reset))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showResetPasswordDialog = false
                            resetStep = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(stringResource(id = R.string.cancel))
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





