package com.example.lupay
import SessionManager
import android.app.Application
import com.example.lupay.ui.network.ApiManager
import network.Repository.UserRepository
import network.UserRemoteDataSource

class MyApplication : Application() {
    private val userRemoteDataSource : UserRemoteDataSource
        get() = UserRemoteDataSource(ApiManager.getUserApiService(this),sessionManager)
     val sessionManager : SessionManager
        get() = SessionManager(this)
    val userRepository : UserRepository
        get() = UserRepository(userRemoteDataSource)

}