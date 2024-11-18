import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var loginResult by mutableStateOf<LoginResult?>(null)
        private set

    fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            isLoading = true
            // Simular una operación de inicio de sesión
            kotlinx.coroutines.delay(2000)
            loginResult = if (email == "usuario@ejemplo.com" && password == "contraseña123") {
                LoginResult.Success
            } else {
                LoginResult.Error("Credenciales inválidas")
            }
            isLoading = false
        }
    }

    fun onForgotPasswordClicked() {
        // Implementar lógica para recuperar contraseña
    }

    fun onRegisterClicked() {
        // Implementar lógica para navegar a la pantalla de registro
    }

    fun onGoogleLoginClicked() {
        // Implementar lógica para inicio de sesión con Google
    }

    fun onOtherLoginClicked() {
        // Implementar lógica para otro método de inicio de sesión
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}