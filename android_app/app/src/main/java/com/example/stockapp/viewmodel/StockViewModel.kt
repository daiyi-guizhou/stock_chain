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
    // 用户数据以 Map<String, User> 格式存储，其中 key 是用户标识，value 是用户对象

    // private: 访问修饰符，表示这个变量只能在 StockViewModel 类内部访问
    // val: 声明一个只读属性（不可重新赋值）
    // _users: 变量名，下划线前缀是约定俗成的命名方式，表示这是内部使用的变量
    // MutableLiveData<Map<String, UserInfo>?>: 变量类型，具体含义如下：
    //     MutableLiveData: Android架构组件，用于存储可变数据并通知UI更新
    //     <Map<String, UserInfo>?>: 泛型参数，表示存储的数据类型
    //         Map<String, UserInfo>: 键值对集合，键是字符串，值是用户信息对象
    //         ?: 可空标记，表示这个Map可能为null

    // val: 声明一个只读属性
    // users: 对外暴露的属性名（没有下划线）
    // LiveData<Map<String, UserInfo>?>: 只读数据类型（与上面的MutableLiveData兼容）
    // get() = _users: 自定义getter，当访问users时实际返回_users的值

    // 封装性:
    // _users 是私有的，外部无法直接修改
    // users 是公开的，但只能读取不能修改

    // 数据驱动UI:
    // 使用 MutableLiveData 可以在数据变化时自动通知UI更新
    // 当调用 _users.postValue() 时，所有观察 users 的UI组件会自动刷新

    // MVVM架构:
    // 这是Android推荐的MVVM架构模式的核心实现
    // ViewModel管理数据，UI层通过观察LiveData获取最新数据

        // // 在ViewModel内部更新数据
        // _users.postValue(usersResponse.body()) // 安全地更新数据

        // // 在Activity/Fragment中观察数据
        // stockViewModel.users.observe(this) { users -> 
        //     // 当_users数据更新时，这里会自动执行
        //     updateUI(userData) 
        // }


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

        /**
         * 从API加载所有用户数据
         * 通过网络请求获取用户列表，并将结果发布到相应的LiveData中
         * 如果请求成功，将用户数据发布到_users LiveData
         * 如果请求失败或发生网络异常，将错误信息发布到_error LiveData
         */
        // Load users
        viewModelScope.launch {
            try {
                // 发起网络请求获取所有用户数据
                val usersResponse = apiService.getAllUsers()
                if (usersResponse.isSuccessful) {
                    // 请求成功，将用户数据发布到LiveData
                    _users.postValue(usersResponse.body())
                    // 各部分详解
                    // _users
                    // 这是一个 MutableLiveData<Map<String, UserInfo>?> 类型的私有变量
                    // 在 MVVM 架构中，它用于存储用户数据并在数据变化时通知观察者
                    // 命名约定中下划线前缀表示这是 ViewModel 内部使用的可变数据源

                    // postValue()
                    // 这是 MutableLiveData 的一个方法
                    // 用于在后台线程中更新 LiveData 的值
                    // 与 setValue() 不同，postValue() 可以安全地在任何线程调用，它会将值传递到主线程

                    // usersResponse.body()
                    // usersResponse 是一个网络请求的响应对象
                    // body() 方法提取响应中的实际数据内容
                    // 返回类型是 Map<String, UserInfo>?，即用户信息映射

                    // 整体作用
                    //     这行代码的作用是：
                        //     将从网络 API 获取到的用户数据更新到 _users 这个 LiveData 中
                        //     一旦更新，所有观察这个 LiveData 的 UI 组件（如 RecyclerView）会自动收到通知并更新显示
                        //     使用 postValue() 确保即使在后台线程中调用也能安全地更新 UI 数据

                    //     在应用中的意义
                        //     在股票交易应用中，这行代码负责：
                        //     更新用户列表数据
                        //     触发 UI 自动刷新，显示最新的用户信息
                        //     保证线程安全，避免在后台线程直接操作 UI
                    //     这是 Android MVVM 架构中数据驱动 UI 更新的核心机制之一
                } else {
                    // 请求失败，将错误码信息发布到错误LiveData
                    _error.postValue("Failed to load users: ${usersResponse.code()}")
                }
            } catch (e: Exception) {
                // 网络异常处理，将异常信息发布到错误LiveData
                _error.postValue("Network error loading users: ${e.message}")
            }
        }
    }

    fun loadUserStocks(username: String, apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStocks(username)
                if (response.isSuccessful) {
                    // {
                    //     "stocks": {
                    //         "AAPL": 50,
                    //         "TSLA": 100
                    //     }
                    // }
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