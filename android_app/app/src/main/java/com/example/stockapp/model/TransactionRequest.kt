package com.example.stockapp.model

data class TransactionRequest(
    val username: String,
    val stockId: String,
    val quantity: Int
)

data class TransactionResponse(
    val success: Boolean,
    val message: String
)