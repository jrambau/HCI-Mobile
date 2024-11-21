package network.Repository

import kotlinx.coroutines.sync.Mutex
import network.PaymentRemoteDataSource
import network.model.NetworkError
import network.model.NetworkPaymentInfo
import network.model.NetworkPaymentInfoResponse
import network.model.NetworkPaymentLink
import network.model.NetworkSinglePayment
import network.model.NetworkSuccess

class PaymentRepository(
    private val remoteDataSource: PaymentRemoteDataSource
) {
    private val mutex = Mutex()
    suspend fun makePayment(amount: Double, type: String, description: String, cardId: Int?, receiverEmail: String?) {
        remoteDataSource.makePayment(amount, type, description, cardId, receiverEmail)
    }
    suspend fun getPaymentsInfo(page: Int? = 1, direction: String? = "ASC", pending: Boolean? = null, type: String? = null, range: String? = null, source: String? = null, cardId: Int? = null) : NetworkPaymentInfoResponse {
      return  remoteDataSource.getPaymentsInfo(page, direction, pending, type, range, source, cardId)
    }
    suspend fun getPaymentDetails(paymentId: Int) : NetworkPaymentInfo {
      return  remoteDataSource.getPaymentDetails(paymentId)
    }

    suspend fun getLinkDetails(linkUuid: String) : NetworkSinglePayment {
      return  remoteDataSource.getLinkDetails(linkUuid)
    }
    suspend fun generateLink(amount: Double,description: String) : NetworkPaymentLink {
      return  remoteDataSource.generateLink(amount,description)
    }
    suspend fun payByLink(linkUuid: String) : NetworkPaymentInfo {
        return remoteDataSource.payByLink(linkUuid)
    }

    suspend fun payByLinkWithCard(linkUuid: String, cardId: Int) : NetworkPaymentInfo {
        return remoteDataSource.payByLinkWithCard(linkUuid, cardId)
    }

}