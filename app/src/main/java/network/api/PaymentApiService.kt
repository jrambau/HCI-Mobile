package network.api
import network.model.NetworkAlias
import network.model.NetworkError
import network.model.NetworkPaymentInfo
import network.model.NetworkPaymentInfoResponse
import network.model.NetworkPaymentLink
import network.model.NetworkSinglePayment
import network.model.NetworkSuccess
import network.model.NetworkToken
import network.model.NetworkUser
import network.model.NetworkUserCredentials
import network.model.NetworkVerificationCode
import network.model.NetworkWalletInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentApiService {
    @POST("payment")
    suspend fun makePayment(@Body request: NetworkPaymentInfo): Response<Unit>
    @POST("payment")
    suspend fun generateLink(@Body request: NetworkPaymentInfo): Response<NetworkPaymentLink>
    @GET("payment")
    suspend fun getPaymentsInfo(
        @Query("page") page: Int? = 1,
        @Query("direction") direction: String? = "DESC",
        @Query("pending") pending: Boolean? = null,
        @Query("type") type: String? = null,
        @Query("range") range: String? = null,
        @Query("source") source: String? = null,
        @Query("cardId") cardId: Int? = null
    ): Response<NetworkPaymentInfoResponse>
    @GET("payment/{paymentId}")
    suspend fun getPaymentDetails(@Path("paymentId") paymentId: Int): Response<NetworkPaymentInfo>
    @GET("payment/link/{linkUuid}")
    suspend fun getLinkDetails(@Path("linkUuid") linkUuid: String): Response<NetworkSinglePayment>
    @POST("payment/link/{linkUuid}")
    suspend fun payByLink(@Path("linkUuid") linkUuid: String,@Body request: NetworkPaymentInfo): Response<NetworkPaymentInfo>
}