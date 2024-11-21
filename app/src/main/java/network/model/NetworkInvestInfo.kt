package network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkInvestInfo (
    val id : Int?=null,
    val returnGiven: Double? =null,
    val balanceBefore: Double?=null,
    val balanceAfter: Double?=null,
    val createdAt: String?=null,
    val updatedAt: String?=null,
    val investment: Double?=null,
)
@Serializable
class NetworkInvestInfoList (
    val dailyReturns: List<NetworkInvestInfo>
)