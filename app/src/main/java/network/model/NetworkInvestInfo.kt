package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkInvestInfo (
    val id : Int?,
    val returnGiven: Double?,
    val balanceBefore: Double?,
    val balanceAfter: Double?,
    val createdAt: String?,
    val updatedAt: String?,
    val investment: Double?,
)
