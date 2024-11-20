import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupay.ui.network.ApiManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.Period
import retrofit2.http.Body
import retrofit2.http.POST
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

    var registerResult by mutableStateOf<RegisterResult?>(null)
        private set
    var registerReturn by mutableStateOf<RegisterReturn?>(null)
        private set

    fun onRegisterClicked() {
        if (name.isBlank() || lastname.isBlank() || birthDate == null || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            registerResult = RegisterResult(
                success = false,
                message = "Todos los campos son obligatorios",
            )
            return
        }

        if (password != confirmPassword) {
            registerResult = RegisterResult(
                success = false,
                message = "Las contraseñas no coinciden",
            )
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                val request = RegisterRequest(
                    firstName = name.split(" ").firstOrNull() ?: "",
                    lastName = name.split(" ").drop(1).joinToString(" "),
                    birthDate = birthDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "",
                    email = email,
                    password = password
                )
                val result = ApiManager.apiService.registerUser(request)
                registerReturn = result
                registerResult = RegisterResult(success = true, message = "Registro exitoso")
            } catch (e: Exception) {
                registerReturn = RegisterReturn(
                    id = -1,
                    firstName = "",
                    lastName = "",
                    birthdate = "",
                    email = ""
                )
                registerResult = RegisterResult(success = false, message = "Error al registrar ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

@Serializable
data class RegisterReturn(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val email: String
)
data class RegisterResult(
    val success: Boolean,
    val message: String
)