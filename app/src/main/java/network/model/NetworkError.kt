package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkError(
    val message: String,
)