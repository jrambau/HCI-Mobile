package network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.TemporalAmount

@Serializable
data class NetworkPaymentInfo(
    val id: Int? = null,
    val type: String? = null,
    val amount: Double? = null,
    val balanceBefore: Double? = null,
    val balanceAfter: Double? = null,
    val receiverBalanceBefore: Double? = null,
    val receiverBalanceAfter: Double? = null,
    val pending: Boolean? = null,
    val linkUuid: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val card: NetworkCard? = null,
    val description: String? = null,
    val receiverEmail: String? = null,
    val cardId: Int? = null,
    val payer: NetworkUser? = null,
    val receiver: NetworkUser? = null
)
@Serializable
data class NetworkPaymentInfoResponse(
    val payments: List<NetworkPaymentInfo>,
)
@Serializable
data class NetworkSinglePayment(
val payment: NetworkPaymentInfo
)