package network

import SessionManager
import android.util.Log
import model.User
import model.asNetworkModel
import network.api.UserApiService
import network.model.NetworkAlias
import network.model.NetworkToken
import network.model.NetworkUser
import network.model.NetworkUserCredentials
import network.model.asModel
import network.model.NetworkUserResponse
import network.model.NetworkWalletInfo

class UserRemoteDataSource (
    private val userApiService: UserApiService,
    private val sessionManager: SessionManager
): RemoteDataSource(){
    suspend fun registerUser(user: User) : User {
        val response = handleApiResponse { userApiService.registerUser(asNetworkModel(user)) }
        Log.e("UserRemoteDataSource", "registerUser: $response")
        return asModel(response)
    }
    suspend fun loginUser(email: String, password: String) : NetworkToken {
        val response = handleApiResponse { userApiService.loginUser(NetworkUserCredentials(email,password,code = null)) }
        sessionManager.saveAuthToken(response.token)
        return response
    }
    suspend fun verifyUser(email: String?, password: String?, code: String) : NetworkToken {
        val response = handleApiResponse { userApiService.loginUser(NetworkUserCredentials(email,password,code)) }
        return response
    }
    suspend fun logoutUser() {
        handleApiResponse { userApiService.logoutUser() }
        sessionManager.removeAuthToken()
    }
    suspend fun recoverPassword(email: String) {
        handleApiResponse { userApiService.recoverPassword(NetworkUserCredentials(email,password = null,code = null)) }
    }
    suspend fun resetPassword(email: String, password: String, code: String) {
        handleApiResponse { userApiService.resetPassword(NetworkUserCredentials(email,password,code)) }
    }
    suspend fun getUser() : NetworkUser {
        val response = handleApiResponse { userApiService.getUser() }
        return response
    }
    suspend fun updateAlias(alias: String) : NetworkWalletInfo {
        return handleApiResponse { userApiService.updateAlias(NetworkAlias(alias)) }

    }
}
