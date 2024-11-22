import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import com.example.lupay.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import model.User
import network.Repository.UserRepository

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(GeneralUiState())
        private set
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


    fun onBirthDateChanged(newDate: LocalDate?) {
        birthDate = newDate
        birthDateError = validateBirthDate(newDate)
    }
    fun checkSavedToken(): Boolean {
        val savedToken = sessionManager.loadAuthToken()
        if (savedToken != null) {
            uiState = uiState.copy(isAuthenticated = true)
            return true
        }
        return false
    }

    private fun validateBirthDate(date: LocalDate?): String? {
        if (date == null) {
            return MyApplication.instance.getString(R.string.error_birthdate)
        }
        val age = Period.between(date, LocalDate.now()).years
        return when {
            age < 18 -> MyApplication.instance.getString(R.string.error_underage)
            age > 120 -> MyApplication.instance.getString(R.string.error_overage)
            else -> null
        }
    }


    fun onLoginClicked() {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_empty_fields)))
            return
        }
        runOnViewModelScope(
            { userRepository.loginUser(email, password) },
            { state, token ->
                sessionManager.saveAuthToken(token.token)
                state.copy(isAuthenticated = true, successMessage = MyApplication.instance.getString(R.string.success_login))
            }
        )
    }

    fun onConfirmAccount(code: String) {
        if (code.isBlank()) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_empty_fields)))
            return
        }
        runOnViewModelScope(
            { userRepository.verifyUser(code=code) },
            { state, _ ->
                state.copy(successMessage = MyApplication.instance.getString(R.string.success_account_confirmation))
            }
        )
    }

    fun onForgotPasswordClicked(email: String) {
        if (email.isBlank()) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_empty_email)))
            return
        }
        runOnViewModelScope(
            { userRepository.recoverPassword(email) },
            { state, _ -> state.copy(successMessage = MyApplication.instance.getString(R.string.email_sended)) }
        )
    }

    fun onResetPassword(email: String, code: String, newPassword: String) {
        if (email.isBlank() || code.isBlank() || newPassword.isBlank()) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_empty_fields)))
            return
        }
        runOnViewModelScope(
            { userRepository.resetPassword(email, newPassword, code) },
            { state, _ -> state.copy(successMessage = MyApplication.instance.getString(R.string.success_password_reset)) }
        )
    }

    fun onRegisterClicked() {
        if (name.isBlank() || lastname.isBlank() || birthDate == null || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_empty_fields)))
            return
        }

        if (password != confirmPassword) {
            uiState = uiState.copy(error = Error(MyApplication.instance.getString(R.string.error_password_mismatch)))
            return
        }
        val user = User(
            firstName = name,
            lastName = lastname,
            birthDate = birthDate!!,
            email = email,
            id = null,
            password = password
        )
        runOnViewModelScope(
            { userRepository.registerUser(user) },
            { state, _ -> state.copy(successMessage = MyApplication.instance.getString(R.string.success_register)) }
        )
    }

    private fun<F> runOnViewModelScope(
        block: suspend () -> F,
        updateState: (GeneralUiState, F) -> GeneralUiState
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isFetching = true, error = null, successMessage = null)
            runCatching { block() }
                .onSuccess { response ->
                    uiState = updateState(uiState, response).copy(isFetching = false)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isFetching = false, error = Error(e.message ?: MyApplication.instance.getString(R.string.error_generic)))
                    Log.e("LoginViewModel", "Error", e)
                }
        }
    }

    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
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

