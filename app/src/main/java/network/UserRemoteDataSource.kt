package network

import SessionManager
import model.User
import model.asNetworkModel
import network.api.UserApiService
import network.model.asModel
import network.model.NetworkUserResponse

class UserRemoteDataSource (
    private val userApiService: UserApiService,
    private val sessionManager: SessionManager
): RemoteDataSource(){
    suspend fun registerUser(user: User) : User {
        val response = handleApiResponse { userApiService.registerUser(asNetworkModel(user)) }
        return asModel(NetworkUserResponse(user = response))
    }
}
