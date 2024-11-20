import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupay.ui.model.UserAnswer
import com.example.lupay.ui.network.ApiManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val password: String
)



class LoginViewModel : ViewModel() {
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

    var registerResult: RegisterResult by mutableStateOf(RegisterResult.Loading)
        private set


    fun onRegisterClicked() {
        if (name.isBlank() || lastname.isBlank() || birthDate == null || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            registerResult = RegisterResult.Error("Por favor, complete todos los campos")
            return
        }

        if (password != confirmPassword) {
            registerResult = RegisterResult.Error("Las contraseñas no coinciden")
            return
        }
        viewModelScope.launch {
            registerResult = RegisterResult.Loading
            try {
                val request = RegisterRequest(
                    firstName = name,
                    lastName = lastname,
                    birthDate = birthDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "",
                    email = email,
                    password = password
                )
                val result = ApiManager.apiService.registerUser(request)
                registerResult= RegisterResult.Success(result)
            } catch (e: Exception) {
                registerResult = RegisterResult.Error(e.message ?: "Error desconocido")
        }
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}


sealed interface RegisterResult{
   data class Success(val answer: UserAnswer) : RegisterResult
    data class Error(val message: String) : RegisterResult
    data object Loading : RegisterResult
}
}