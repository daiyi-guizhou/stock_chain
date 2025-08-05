package com.example.stockapp.model

data class UserStocksResponse(
    val stocks: Map<String, Int> = emptyMap()
)