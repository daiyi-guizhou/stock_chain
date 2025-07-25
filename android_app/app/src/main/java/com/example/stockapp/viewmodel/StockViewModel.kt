package com.example.stockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.stockapp.network.ApiService
import com.example.stockapp.ui.StockInfo
import com.example.stockapp.ui.UserInfo
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    private val _stocks = MutableLiveData<Map<String, StockInfo>?>()
    val stocks: LiveData<Map<String, StockInfo>?> get() = _stocks

    private val _users = MutableLiveData<Map<String, UserInfo>?>()
    val users: LiveData<Map<String, UserInfo>?> get() = _users

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadStocksAndUsers(apiService: ApiService) = viewModelScope.launch {
        try {
            val stocksResponse = apiService.getStocks()
            if (stocksResponse.isSuccessful) {
                _stocks.postValue(stocksResponse.body())
            } else {
                _error.postValue("Failed to load stocks: ${stocksResponse.code()}")
            }

            val usersResponse = apiService.getUsers()
            if (usersResponse.isSuccessful) {
                _users.postValue(usersResponse.body())
            } else {
                _error.postValue("Failed to load users: ${usersResponse.code()}")
            }
        } catch (e: Exception) {
            _error.postValue("Network error: ${e.message}")
        }
    }
}