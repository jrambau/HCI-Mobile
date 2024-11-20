package network.api

import network.model.NetworkCard
import network.model.NetworkInterest
import network.model.NetworkInvestInfo
import network.model.NetworkWalletInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WalletApiService {
    @GET("wallet/balance")
    suspend fun getWalletBalance(): Response<NetworkWalletInfo>
    @POST("wallet/recharge")
    suspend fun rechargeWallet(@Body request: NetworkWalletInfo ): Response<NetworkWalletInfo>
    @GET("wallet/investment")
    suspend fun getInvestment(): Response<NetworkWalletInfo>
    @POST("wallet/invest")
    suspend fun invest(@Body request: NetworkWalletInfo ): Response<NetworkWalletInfo>
    @POST("wallet/divest")
    suspend fun divest(@Body request: NetworkWalletInfo ): Response<NetworkWalletInfo>
    @GET("wallet/cards")
    suspend fun getCards(): Response<Array<NetworkCard>>
    @POST("wallet/cards")
    suspend fun addCard(@Body request: NetworkCard ): Response<NetworkCard>
    @DELETE("wallet/cards/{cardId}")
    suspend fun deleteCard(@Path("cardId") cardId: Int): Unit
    @GET("wallet/daily-returns")
    suspend fun getDailyReturns(@Query("page") page: Int): Response<Array<NetworkInvestInfo>>
    @GET("wallet/daily-interest")
    suspend fun getDailyInterest(): Response<Array<NetworkInterest>>
    @GET("wallet/details")
    suspend fun getWalletDetails(): Response<NetworkWalletInfo>
}