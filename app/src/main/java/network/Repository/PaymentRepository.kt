package network.Repository

import kotlinx.coroutines.sync.Mutex
import network.PaymentRemoteDataSource
import network.model.NetworkError
import network.model.NetworkPaymentInfo
import network.model.NetworkSuccess

class PaymentRepository(
    private val remoteDataSource: PaymentRemoteDataSource
) {
    private val mutex = Mutex()
    suspend fun makePayment(amount: Double, type: String, description: String, cardId: Int?, receiverEmail: String?) {
        remoteDataSource.makePayment(amount, type, description, cardId, receiverEmail)
    }
    suspend fun getPaymentsInfo(page: Int? = 1, direction: String? = "ASC", pending: Boolean? = null, type: String? = null, range: String? = null, source: String? = null, cardId: Int? = null) : Array<NetworkPaymentInfo> {
      return  remoteDataSource.getPaymentsInfo(page, direction, pending, type, range, source, cardId)
    }
    suspend fun getPaymentDetails(paymentId: Int) : NetworkPaymentInfo {
      return  remoteDataSource.getPaymentDetails(paymentId)
    }
    suspend fun payByLink(linkUuid: String) {
        remoteDataSource.payByLink(linkUuid)
    }
    suspend fun generatePaymentLink(linkUuid: String) : NetworkSuccess {
       return remoteDataSource.generatePaymentLink(linkUuid)
    }

}