package network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Serializable
data class NetworkUser (
    val id: Int?,
    val name: String,
    val lastname: String,
    val birthdate: String,
    val email: String
) {
    fun getBirthdateAsLocalDate(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(birthdate, formatter)

    }
}
fun asModel(user: NetworkUser): model.User {
    return model.User(
        id = user.id,
        name = user.name,
        lastname = user.lastname,
        birthdate = user.getBirthdateAsLocalDate(),
        email = user.email
    )
}