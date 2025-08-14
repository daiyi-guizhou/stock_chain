// android_app/app/src/main/java/com/example/stockapp/model/BuyRequest.kt
package com.example.stockapp.model
import com.google.gson.annotations.SerializedName

data class BuyRequest(
    val username: String,
    val stock_id: String,
    // @SerializedName("stock_id") val stockId: String,
    val amount: Int,
    val payment: Double
)