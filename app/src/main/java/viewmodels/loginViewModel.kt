import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.Period
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.format.DateTimeFormatter

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val password: String
)

interface UserService {
    @POST("api/user")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResult
}

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
    var registerResult by mutableStateOf<RegisterResult?>(null)
        private set
    var birthDate by mutableStateOf<LocalDate?>(null)
        private set
    var birthDateError by mutableStateOf<String?>(null)
        private set

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    fun onNameChanged(newName: String) {
        name = newName
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
                val result = userService.registerUser(request)
                registerResult = result
            } catch (e: Exception) {
                registerResult = RegisterResult.Error("Failed to register: ${e.message}")
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

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}