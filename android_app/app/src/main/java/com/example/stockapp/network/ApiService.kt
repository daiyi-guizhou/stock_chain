package com.example.stockapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

import com.example.stockapp.ui.StockInfo
import com.example.stockapp.ui.UserInfo

interface ApiService {
    @GET("stocks")
    suspend fun getStocks(): Response<Map<String, StockInfo>>

    @GET("users")
    suspend fun getUsers(): Response<Map<String, UserInfo>>
}