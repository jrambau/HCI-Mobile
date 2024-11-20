package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkCard (
    val id: Int?,
    val number: String,
    val expirationDate: String,
    val cvv: String?,
    val fullName: String,
    val type: String,
    val createdAt: String?,
    val updatedAt: String?,

)