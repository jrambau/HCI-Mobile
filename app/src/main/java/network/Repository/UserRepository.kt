package network.Repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import model.User
import network.UserRemoteDataSource
import network.model.asModel

class UserRepository (
    private val remoteDataSource: UserRemoteDataSource
){
    private var registeredUser: User? = null
    private val mutex = Mutex()
    suspend fun registerUser(user: User) : User {
        val response = remoteDataSource.registerUser(user)
        mutex.withLock {
            this.registeredUser = response
        }
        return response
    }
}