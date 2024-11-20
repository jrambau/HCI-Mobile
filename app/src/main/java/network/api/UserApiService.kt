package network.api



import network.model.NetworkAlias
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

interface UserApiService {
    @POST("user")
    suspend fun registerUser(@Body request: NetworkUser): Response<NetworkUser>
    @PUT("wallet/update-alias")
    suspend fun updateAlias(@Body request: NetworkAlias): Response<NetworkWalletInfo>
    @GET("user")
    suspend fun getUser(): Response<NetworkUser>
    @POST("user/login")
    suspend fun loginUser(@Body request: NetworkUserCredentials): Response<NetworkToken>
    @POST("user/verify")
    suspend fun verifyUser(@Body request: NetworkVerificationCode): Response<NetworkUser>
    @POST("user/recover-password")
    suspend fun recoverPassword(@Body request: NetworkUserCredentials): Response<Unit>
    @POST("user/reset-password")
    suspend fun resetPassword(@Body request: NetworkUserCredentials): Response<Unit>
    @POST("user/logout")
    suspend fun logoutUser(): Response<Unit>
}