package network

import SessionManager
import model.User
import model.asNetworkModel
import network.api.PaymentApiService
import network.api.UserApiService
import network.api.WalletApiService
import network.model.NetworkError
import network.model.NetworkPaymentInfo
import network.model.NetworkSuccess
import network.model.asModel

import org.hamcrest.Description

class PaymentRemoteDataSource (
    private val paymentApiService: PaymentApiService,
    private val sessionManager: SessionManager
): RemoteDataSource(){
suspend fun makePayment(amount: Double, type: String,description: String, cardId: Int?, receiverEmail:String?) {
    handleApiResponse { paymentApiService.makePayment(NetworkPaymentInfo(amount=amount, type = type, description = description, cardId = cardId, receiverEmail = receiverEmail)) }
}
    suspend fun getPaymentsInfo(page: Int? = 1, direction: String? = "ASC", pending: Boolean? = null, type: String? = null, range: String? = null, source: String? = null, cardId: Int? = null): Array<NetworkPaymentInfo> {
        val response = handleApiResponse { paymentApiService.getPaymentsInfo(page, direction, pending, type, range, source, cardId) }
        return response
    }
    suspend fun getPaymentDetails(paymentId: Int): NetworkPaymentInfo {
        val response = handleApiResponse { paymentApiService.getPaymentDetails(paymentId) }
        return response
    }
    suspend fun payByLink(linkUuid: String): NetworkPaymentInfo {
        val response = handleApiResponse { paymentApiService.payByLink(linkUuid) }
        return response
    }
    suspend fun generatePaymentLink(linkUuid: String): NetworkSuccess {
        val response = handleApiResponse { paymentApiService.generatePaymentLink(linkUuid) }
        return response
    }
}
