package network.api



import network.model.NetworkUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("user")
    suspend fun registerUser(@Body request: NetworkUser): Response<NetworkUser>
}