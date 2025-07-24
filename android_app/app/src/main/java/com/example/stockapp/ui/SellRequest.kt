package com.example.stockapp.ui

import com.google.gson.annotations.SerializedName



data class SellRequest(
    val username: String,
    @SerializedName("stock_id") val stockId: String,
    val amount: Int
)