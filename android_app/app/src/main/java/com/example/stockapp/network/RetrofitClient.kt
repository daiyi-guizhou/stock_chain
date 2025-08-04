package com.example.stockapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // private const val BASE_URL = "http://172.25.72.89:8080/" // 使用 Android 模拟器访问本机
    private const val BASE_URL = "http://192.168.112.47:8080/" // 使用 Android 模拟器访问本机

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}