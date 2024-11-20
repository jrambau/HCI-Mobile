package network.Repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import model.User
import network.UserRemoteDataSource
import network.model.NetworkAlias
import network.model.NetworkToken
import network.model.NetworkUser
import network.model.NetworkWalletInfo
import network.model.asModel

class UserRepository (
    private val remoteDataSource: UserRemoteDataSource
){
    private var registeredUser: User? = null
    private var currentUser: NetworkUser? = null
    private var token: NetworkToken? = null
    private val mutex = Mutex()
    suspend fun registerUser(user: User) : User {
        val response = remoteDataSource.registerUser(user)
        mutex.withLock {
            this.registeredUser = response
        }
        return response
    }
    suspend fun loginUser(email: String, password: String) : NetworkToken {
       val response =  remoteDataSource.loginUser(email,password)
        mutex.withLock {
            this.token = response
        }
        return response
    }
    suspend fun getUser() : NetworkUser {
        val response = remoteDataSource.getUser()
        mutex.withLock {
            this.currentUser = response
        }
        return response
    }
    suspend fun logoutUser() {
        remoteDataSource.logoutUser()
        mutex.withLock {
            this.token = null
            this.currentUser = null
        }
    }
    suspend fun recoverPassword(email: String) {
        remoteDataSource.recoverPassword(email)
    }
    suspend fun resetPassword(email: String, password: String, code: String) {
        remoteDataSource.resetPassword(email,password,code)
    }
    suspend fun updateAlias(alias: String) : NetworkWalletInfo {
        return remoteDataSource.updateAlias(alias)
    }
    suspend fun verifyUser(email: String, password: String, code: String) : NetworkToken {
        return remoteDataSource.verifyUser(email,password,code)
    }
}