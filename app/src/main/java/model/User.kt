package model

import network.model.NetworkUser
import java.sql.Date
import java.time.LocalDate

class User(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val email: String,
    val password: String?
)
fun asNetworkModel(user: User): NetworkUser {
    return NetworkUser(
        id = user.id,
        firstName = user.firstName,
        lastName = user.lastName,
        birthDate = user.birthDate.toString(),
        email = user.email,
        password = user.password
    )
}