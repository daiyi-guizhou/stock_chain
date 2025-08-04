// android_app/app/src/main/java/com/example/stockapp/model/BuyRequest.kt
package com.example.stockapp.model

data class BuyRequest(
    val username: String,
    val stockID: String,
    val amount: Int,
    val payment: Double
)