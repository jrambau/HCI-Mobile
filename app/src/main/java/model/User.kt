package model

import network.model.NetworkUser
import java.sql.Date
import java.time.LocalDate

class User(
    val id: Int?,
    val name: String,
    val lastname: String,
    val birthdate: LocalDate,
    val email: String,
    val password: String?
)
fun asNetworkModel(user: User): NetworkUser {
    return NetworkUser(
        id = user.id,
        name = user.name,
        lastname = user.lastname,
        birthdate = user.birthdate.toString(),
        email = user.email,
        password = user.password
    )
}