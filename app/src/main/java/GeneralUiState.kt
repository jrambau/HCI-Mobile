import androidx.core.app.NotificationCompat.MessagingStyle.Message
import model.Card
import model.User

data class GeneralUiState (
    val isAuthenticated: Boolean = false,
    val isFetching: Boolean = false,
    val currentUser: User? = null,
    val cards: List<Card>? = null,
    val error: Error? = null,
    val currentCard: Card? = null,
    var success: Boolean = false,
    var successMessage: String? = null
) {

}

val GeneralUiState.canAddCard: Boolean get() = isAuthenticated
val GeneralUiState.canFetchCards: Boolean get() = isAuthenticated
val GeneralUiState.canFetchUser: Boolean get() = isAuthenticated
val GeneralUiState.canGetCurrentCard: Boolean get() = isAuthenticated && currentCard != null
val GeneralUiState.canGetCurrentUser: Boolean get() = isAuthenticated && currentUser != null
