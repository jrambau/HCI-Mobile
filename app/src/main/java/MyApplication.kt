package com.example.lupay

import SessionManager
import android.app.Application
import android.content.Context
import com.example.lupay.ui.network.ApiManager
import network.PaymentRemoteDataSource
import network.Repository.PaymentRepository
import network.Repository.UserRepository
import network.Repository.WalletRepository
import network.UserRemoteDataSource
import network.WalletRemoteDataSource

class MyApplication : Application() {
    init {
        instance = this
    }
    private val userRemoteDataSource : UserRemoteDataSource
        get() = UserRemoteDataSource(ApiManager.getUserApiService(this),sessionManager)
     val sessionManager : SessionManager
        get() = SessionManager(this)
    val userRepository : UserRepository
        get() = UserRepository(userRemoteDataSource)
    private val walletRemoteDataSource : WalletRemoteDataSource
        get() = WalletRemoteDataSource(ApiManager.getWalletApiService(this),sessionManager)
    private val paymentRemoteDataSource : PaymentRemoteDataSource
        get() = PaymentRemoteDataSource(ApiManager.getPaymentApiService(this),sessionManager)
    val paymentRepository : PaymentRepository
        get() = PaymentRepository(paymentRemoteDataSource)
    val walletRepository : WalletRepository
        get() = WalletRepository(walletRemoteDataSource)

    companion object {
        lateinit var instance: MyApplication
            private set

        val context: Context
            get() = instance.applicationContext
    }

}