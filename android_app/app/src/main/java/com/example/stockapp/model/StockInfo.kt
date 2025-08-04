// android_app/app/src/main/java/com/example/stockapp/model/StockInfo.kt
package com.example.stockapp.model

data class StockInfo(
    val symbol: String,
    val price: Double,
    val quantity: Int
)