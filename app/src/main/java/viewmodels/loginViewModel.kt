import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import com.example.lupay.ui.network.ApiManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlinx.serialization.Serializable
import model.User
import network.Repository.UserRepository
import java.sql.Date


class LoginViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(GeneralUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
        private set
    val loginResult: Any = false
    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var birthDate by mutableStateOf<LocalDate?>(null)
        private set
    var birthDateError by mutableStateOf<String?>(null)
        private set
    var lastname by mutableStateOf("")
        private set

    fun onNameChanged(newName: String) {
        name = newName
    }
    fun onLastNameChanged(newLastName: String) {
        lastname = newLastName
    }
    fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            isLoading = true
            // Simulate a login operation
            kotlinx.coroutines.delay(2000)
            val loginResult = if (email == "usuario@ejemplo.com" && password == "contraseña123") {
                LoginResult.Success
            } else {
                LoginResult.Error("Credenciales inválidas")
            }
            isLoading = false
        }
    }

    fun onBirthDateChanged(newDate: LocalDate?) {
        birthDate = newDate
        birthDateError = validateBirthDate(newDate)
    }

    private fun validateBirthDate(date: LocalDate?): String? {
        if (date == null) {
            return "Por favor, seleccione una fecha de nacimiento."
        }
        val age = Period.between(date, LocalDate.now()).years
        return when {
            age < 18 -> "Debe ser mayor de 18 años para registrarse."
            age > 120 -> "La fecha de nacimiento no es válida."
            else -> null
        }
    }

    fun onForgotPasswordClicked() {
        // Implement password recovery logic
    }




    fun onRegisterClicked() {
        if (name.isBlank() || lastname.isBlank() || birthDate == null || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            uiState = uiState.copy(error = Error("Por favor, complete todos los campos."))
            return
        }

        if (password != confirmPassword) {
            uiState = uiState.copy(error = Error("Las contraseñas no coinciden."))
            return
        }
         val user = User(
            name = name,
            lastname = lastname,
            birthdate = birthDate!!,
            email = email,
             id = null,
            password = password
        )
        runOnViewModelScope(
        {userRepository.registerUser(user)},
            {state, _ -> state.copy(isAuthenticated = false)}
    )
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

private fun<R> runOnViewModelScope(
    block: suspend () -> R,
    updateState: (GeneralUiState, R) -> GeneralUiState
): Job = viewModelScope.launch {
    uiState = uiState.copy(isFetching = true, error = null)
    runCatching { block()
    }.onSuccess { response ->
        uiState = updateState(uiState, response).copy(isFetching = false,success = true)
    }.onFailure {e->
        uiState = uiState.copy(isFetching = false, error = Error(e.message))
        Log.e("LoginViewModel", "Error", e)
    }
}
    companion object {
        fun provideFactory(application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(
                    application.sessionManager,
                    application.userRepository
                ) as T
            }
        }
    }
}
