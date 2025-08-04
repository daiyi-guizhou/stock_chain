package com.example.stockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.stockapp.network.ApiService
import com.example.stockapp.model.*
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    private val _stocks = MutableLiveData<Map<String, StockInfo>?>()
    val stocks: LiveData<Map<String, StockInfo>?> get() = _stocks

    private val _users = MutableLiveData<Map<String, UserInfo>?>()
    val users: LiveData<Map<String, UserInfo>?> get() = _users

    private val _userStocks = MutableLiveData<List<UserStock>>()
    val userStocks: LiveData<List<UserStock>> get() = _userStocks

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadStocksAndUsers(apiService: ApiService) {
        // Load stocks
        viewModelScope.launch {
            try {
                val stocksResponse = apiService.getAllStocks()
                if (stocksResponse.isSuccessful) {
                    _stocks.postValue(stocksResponse.body())
                } else {
                    _error.postValue("Failed to load stocks: ${stocksResponse.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error loading stocks: ${e.message}")
            }
        }

        // Load users
        viewModelScope.launch {
            try {
                val usersResponse = apiService.getAllUsers()
                if (usersResponse.isSuccessful) {
                    _users.postValue(usersResponse.body())
                } else {
                    _error.postValue("Failed to load users: ${usersResponse.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error loading users: ${e.message}")
            }
        }
    }

    fun loadUserStocks(username: String, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStocks(username)
                if (response.isSuccessful) {
                    _userStocks.postValue(response.body() ?: emptyList())
                } else {
                    _error.postValue("Failed to load user stocks: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error loading user stocks: ${e.message}")
            }
        }
    }

    fun buyStock(username: String, stockID: String, amount: Int, payment: Double, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val request = BuyRequest(username, stockID, amount, payment)
                val response = apiService.buyStock(request)
                if (response.isSuccessful) {
                    // Reload data after successful purchase
                    loadStocksAndUsers(apiService)
                    loadUserStocks(username, apiService)
                } else {
                    _error.postValue("Failed to buy stock: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error buying stock: ${e.message}")
            }
        }
    }

    fun sellStock(username: String, stockID: String, amount: Int, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val request = SellRequest(username, stockID, amount)
                val response = apiService.sellStock(request)
                if (response.isSuccessful) {
                    // Reload data after successful sale
                    loadStocksAndUsers(apiService)
                    loadUserStocks(username, apiService)
                } else {
                    _error.postValue("Failed to sell stock: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error selling stock: ${e.message}")
            }
        }
    }

    fun getUserStockCount(username: String, stockID: String, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStockQuantity(username, stockID)
                if (response.isSuccessful) {
                    val quantity = response.body()?.get("quantity") ?: 0
                    _error.postValue("User $username holds $quantity shares of $stockID")
                } else {
                    _error.postValue("Failed to get stock count: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error getting stock count: ${e.message}")
            }
        }
    }

    fun getUserTotalValue(username: String, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserTotalValue(username)
                if (response.isSuccessful) {
                    val totalValue = response.body()?.get("totalValue") ?: 0.0
                    _error.postValue("User $username total value: $${String.format("%.2f", totalValue)}")
                } else {
                    _error.postValue("Failed to get total value: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error getting total value: ${e.message}")
            }
        }
    }

    fun closeAccount(username: String, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.closeAccount(username)
                if (response.isSuccessful) {
                    // Reload data after closing account
                    loadStocksAndUsers(apiService)
                    _error.postValue("Account $username closed successfully")
                } else {
                    _error.postValue("Failed to close account: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error closing account: ${e.message}")
            }
        }
    }
}