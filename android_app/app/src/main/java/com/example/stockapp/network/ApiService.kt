package com.example.stockapp.network

import com.example.stockapp.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    companion object {
        // private const val BASE_URL = "http://192.168.112.47:8080/" // 根据实际情况修改
        private const val BASE_URL = "https://6bacc411f2f6.ngrok-free.app/" // 根据实际情况修改

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    // 获取所有股票
    @GET("stocks")
    suspend fun getAllStocks(): Response<Map<String, StockInfo>>

    // 获取所有用户
    @GET("users")
    suspend fun getAllUsers(): Response<Map<String, UserInfo>>

    // 买入股票
    @POST("buy")
    suspend fun buyStock(@Body request: BuyRequest): Response<Map<String, Any>>

    // 卖出股票
    @POST("sell")
    suspend fun sellStock(@Body request: SellRequest): Response<Map<String, Any>>

    // 查询用户持有某股票的数量
    @GET("user/{username}/stock/{stockId}")
    suspend fun getUserStockQuantity(
        @Path("username") username: String,
        @Path("stockId") stockId: String
    ): Response<Map<String, Int>>

    // 查询用户总资产
    @GET("user/{username}/value")
    suspend fun getUserTotalValue(@Path("username") username: String): Response<Map<String, Double>>

    // 查询用户所有持仓
    @GET("user/{username}/stocks")
    suspend fun getUserStocks(@Path("username") username: String): Response<UserStocksResponse>

    // 删除用户的所有持仓和账户信息
    @DELETE("user/{username}")
    suspend fun closeAccount(@Path("username") username: String): Response<Map<String, String>>
}