package com.example.lupay.ui.network

import RegisterRequest
import RegisterResult
import RegisterReturn
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = "http://localhost:8080/"
private val httpLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .build()
private val json = Json { ignoreUnknownKeys = true }
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .client(okHttpClient)
    .build()

interface ApiService {
    @POST("api/user")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterReturn
}

object ApiManager {
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}