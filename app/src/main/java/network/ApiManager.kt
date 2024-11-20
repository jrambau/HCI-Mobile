package com.example.lupay.ui.network


import android.content.Context

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import AuthInterceptor
import network.api.UserApiService

object ApiManager {
private const val BASE_URL = "http://10.0.2.2:8080/api/"
private var instance: Retrofit? = null
private fun getInstance(context: Context): Retrofit =
     instance ?: synchronized(this) {
        instance ?: buildRetroFit(context).also { instance = it }
}

private fun buildRetroFit(context: Context): Retrofit {
     val httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

     val okHttpClient = OkHttpClient.Builder()
         .addInterceptor(AuthInterceptor(context))
        .addInterceptor(httpLoggingInterceptor)
        .build()

     val json = Json { ignoreUnknownKeys = true }
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()
}




fun getUserApiService(context: Context): UserApiService{
      return  getInstance(context).create(UserApiService::class.java)
}}
