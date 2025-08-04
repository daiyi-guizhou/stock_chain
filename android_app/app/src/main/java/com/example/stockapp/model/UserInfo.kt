// android_app/app/src/main/java/com/example/stockapp/model/UserInfo.kt
package com.example.stockapp.model

data class UserInfo(
    val name: String,
    val stocks: Map<String, Int>,
    val balance: Double
)