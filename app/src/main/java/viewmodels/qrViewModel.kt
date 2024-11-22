package viewmodels


import GeneralUiState
import SessionManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lupay.MyApplication
import com.example.lupay.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import network.Repository.UserRepository
import network.Repository.PaymentRepository
import network.model.NetworkPaymentInfo

class QrViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    var uiState by mutableStateOf(GeneralUiState())
        private set

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _paymentResult = MutableStateFlow<NetworkPaymentInfo?>(null)
    val paymentResult: StateFlow<NetworkPaymentInfo?> = _paymentResult.asStateFlow()

    init {
        getUserEmail()
    }

    private fun getUserEmail() {
        viewModelScope.launch {
            uiState = uiState.copy(isFetching = true, error = null, successMessage = null)
            runCatching { userRepository.getUser() }
                .onSuccess { user ->
                    _userEmail.value = user.email
                    uiState = uiState.copy(isFetching = false, successMessage = user.email)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isFetching = false, error = Error(e.message ?: MyApplication.instance.getString(R.string.error_generic)))
                }
        }
    }

    fun makePayment(receiverEmail: String, amount: Double, description: String, isAccount: Boolean, cardId: Int?) {
        viewModelScope.launch {
            uiState = uiState.copy(isFetching = true, error = null, successMessage = null)
            runCatching {
                paymentRepository.makePayment(
                    amount = amount,
                    type = if (isAccount) "ACCOUNT" else "CARD",
                    description = description,
                    cardId = cardId,
                    receiverEmail = receiverEmail
                )
            }
                .onSuccess {
                    uiState = uiState.copy(isFetching = false, successMessage = "Payment successful")
                    // Fetch payment details if needed
                    // _paymentResult.value = paymentRepository.getPaymentDetails(paymentId)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isFetching = false, error = Error(e.message ?: MyApplication.instance.getString(R.string.error_generic)))
                }
        }
    }

    companion object {
        fun provideFactory(application: MyApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QrViewModel(
                        application.sessionManager,
                        application.userRepository,
                        application.paymentRepository
                    ) as T
                }
            }
    }
}