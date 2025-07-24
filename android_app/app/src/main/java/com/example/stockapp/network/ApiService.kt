package com.example.stockapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path

import com.example.stockapp.ui.BuyRequest
import com.example.stockapp.ui.SellRequest

interface ApiService {
    @POST("init")
    suspend fun initLedger(): Response<Unit>

    @POST("buy")
    suspend fun buyStock(@Body request: BuyRequest): Response<Unit>

    @POST("sell")
    suspend fun sellStock(@Body request: SellRequest): Response<Double>

    @GET("price/{stockId}")
    suspend fun getStockPrice(@Path("stockId") stockId: String): Response<Double>

    @GET("user/{username}/stocks")
    suspend fun getUserStocks(@Path("username") username: String): Response<Map<String, Int>>

    @GET("user/{username}/value")
    suspend fun getUserTotalValue(@Path("username") username: String): Response<Double>
}