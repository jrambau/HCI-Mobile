package network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Serializable
data class NetworkUserResponse(
    val user: NetworkUser
)

@Serializable
data class NetworkUser(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val password: String? = null
) {
    fun getBirthdateAsLocalDate(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(birthDate, formatter)
    }
}

fun asModel(userResponse: NetworkUserResponse): model.User {
    return model.User(
        id = userResponse.user.id,
        firstName = userResponse.user.firstName,
        lastName = userResponse.user.lastName,
        birthDate = userResponse.user.getBirthdateAsLocalDate(),
        email = userResponse.user.email,
        password = userResponse.user.password
    )
}