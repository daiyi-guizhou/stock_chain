// android_app/app/src/main/java/com/example/stockapp/model/SellRequest.kt
package com.example.stockapp.model

data class SellRequest(
    val username: String,
    val stockID: String,
    val amount: Int
)