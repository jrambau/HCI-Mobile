package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkUserCredentials (
    val email: String?,
    val password: String?,
    val code: String?
)
