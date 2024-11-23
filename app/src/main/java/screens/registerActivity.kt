import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lupay.MyApplication
import com.example.lupay.R
import theme.CustomTheme
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import android.content.res.Configuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState = viewModel.uiState
    var showPassword by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val deviceType = rememberDeviceType()

    CustomTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.height(48.dp)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isLandscape && deviceType != DeviceType.TABLET) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 32.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left column for title and messages
                        Column(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(id = R.string.register_title),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.welcome),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Status messages
                            if (viewModel.isLoading) {
                                Spacer(modifier = Modifier.height(16.dp))
                                CircularProgressIndicator()
                            }
                            if (showDatePicker) {
                                val datePickerState = rememberDatePickerState()
                                val configuration = LocalConfiguration.current
                                val screenHeight = configuration.screenHeightDp.dp
                                val screenWidth = configuration.screenWidthDp.dp
                                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                                val deviceType = rememberDeviceType()

                                DatePickerDialog(
                                    onDismissRequest = { showDatePicker = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                datePickerState.selectedDateMillis?.let { millis ->
                                                    val selectedDate = Instant.ofEpochMilli(millis)
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                    viewModel.onBirthDateChanged(selectedDate)
                                                }
                                                showDatePicker = false
                                            }
                                        ) {
                                            Text("OK")
                                        }
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .then(
                                                if (isLandscape && deviceType != DeviceType.TABLET) {
                                                    Modifier
                                                        .width(screenWidth * 0.7f)
                                                        .height(screenHeight * 0.8f)
                                                } else {
                                                    Modifier.fillMaxSize()
                                                }
                                            )
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        DatePicker(
                                            state = datePickerState,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                            if (uiState.isFetching) {
                                CircularProgressIndicator()
                            } else {
                                uiState.error?.let { error ->
                                    Text(
                                        text = "Error: ${error.message}",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                uiState.successMessage?.let { message ->
                                    Text(
                                        text = message,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        // Right column for form fields
                        Column(
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomOutlinedTextField(
                                value = viewModel.name,
                                onValueChange = { viewModel.onNameChanged(it) },
                                label = stringResource(id = R.string.enter_name),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth()
                            )

                            CustomOutlinedTextField(
                                value = viewModel.lastname,
                                onValueChange = { viewModel.onLastNameChanged(it) },
                                label = stringResource(id = R.string.enter_lastname),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth()
                            )

                            CustomOutlinedTextField(
                                value = viewModel.birthDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                                onValueChange = { },
                                label = stringResource(id = R.string.enter_bday),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = stringResource(id = R.string.date)
                                        )
                                    }
                                },
                                isError = viewModel.birthDateError != null
                            )

                            viewModel.birthDateError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            CustomOutlinedTextField(
                                value = viewModel.email,
                                onValueChange = { viewModel.onEmailChanged(it) },
                                label = stringResource(id = R.string.enter_mail),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth()
                            )

                            CustomOutlinedTextField(
                                value = viewModel.password,
                                onValueChange = { viewModel.onPasswordChanged(it) },
                                label = stringResource(id = R.string.enter_pass),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                isPassword = true,
                                showPassword = showPassword,
                                onPasswordVisibilityChange = { showPassword = it }
                            )

                            CustomOutlinedTextField(
                                value = viewModel.confirmPassword,
                                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                                label = stringResource(id = R.string.reenter_pass),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                isPassword = true,
                                showPassword = showPassword,
                                onPasswordVisibilityChange = { showPassword = it }
                            )

                            Button(
                                onClick = { viewModel.onRegisterClicked() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(stringResource(id = R.string.register_title))
                            }

                            TextButton(
                                onClick = onNavigateToLogin,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.has_account),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.register_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.welcome),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        CustomOutlinedTextField(
                            value = viewModel.name,
                            onValueChange = { viewModel.onNameChanged(it) },
                            label = stringResource(id = R.string.enter_name),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )

                        CustomOutlinedTextField(
                            value = viewModel.lastname,
                            onValueChange = { viewModel.onLastNameChanged(it) },
                            label = stringResource(id = R.string.enter_lastname),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )

                        CustomOutlinedTextField(
                            value = viewModel.birthDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                            onValueChange = { },
                            label = stringResource(id = R.string.enter_bday),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = stringResource(id = R.string.date)
                                    )
                                }
                            },
                            isError = viewModel.birthDateError != null
                        )

                        viewModel.birthDateError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        CustomOutlinedTextField(
                            value = viewModel.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = stringResource(id = R.string.enter_mail),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        CustomOutlinedTextField(
                            value = viewModel.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = stringResource(id = R.string.enter_pass),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            showPassword = showPassword,
                            onPasswordVisibilityChange = { showPassword = it }
                        )

                        CustomOutlinedTextField(
                            value = viewModel.confirmPassword,
                            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                            label = stringResource(id = R.string.reenter_pass),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            showPassword = showPassword,
                            onPasswordVisibilityChange = { showPassword = it }
                        )

                        Button(
                            onClick = { viewModel.onRegisterClicked() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(stringResource(id = R.string.register_title))
                        }

                        TextButton(
                            onClick = onNavigateToLogin
                        ) {
                            Text(
                                text = stringResource(id = R.string.has_account),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }


// Status messages
                        if (viewModel.isLoading || uiState.isFetching) {
                            CircularProgressIndicator()
                        }

                        uiState.error?.let { error ->
                            Text(
                                text = "Error: ${error.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        uiState.successMessage?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onBirthDateChanged(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onPasswordVisibilityChange: ((Boolean) -> Unit)? = null,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        readOnly = readOnly,
        trailingIcon = trailingIcon ?: {
            if (isPassword) {
                IconButton(onClick = { onPasswordVisibilityChange?.invoke(!showPassword) }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )
                }
            }
        },
        isError = isError
    )
}

