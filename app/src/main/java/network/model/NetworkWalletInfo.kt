package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkWalletInfo (
    val balance: Double? = null,
    val amount: Double? = null,
    val newBalance: Double? = null,
    val invested: Double? = null,
    val id: Int? = null,
    val cbu: String? = null,
    val alias: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)