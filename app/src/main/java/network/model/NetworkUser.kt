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

fun asModel(userResponse: NetworkUser): model.User {
    return model.User(
        id = userResponse.id,
        firstName = userResponse.firstName,
        lastName = userResponse.lastName,
        birthDate = userResponse.getBirthdateAsLocalDate(),
        email = userResponse.email,
        password = userResponse.password
    )
}