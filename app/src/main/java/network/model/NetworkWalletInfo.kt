package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkWalletInfo (
    val balance: Double?,
    val amout: Double?,
    val newBalance: Double?,
    val invested: Double?,
    val id: Int?,
    val cbu: String?,
    val alias: String?,
    val createdAt: String?,
    val updatedAt: String?
)