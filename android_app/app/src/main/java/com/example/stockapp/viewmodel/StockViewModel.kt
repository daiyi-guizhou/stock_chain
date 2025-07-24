package com.example.stockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.stockapp.ui.BuyRequest
import com.example.stockapp.network.RetrofitClient
import kotlinx.coroutines.launch

// 封装结果为 sealed class 提升类型安全
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class StockViewModel : ViewModel() {

    // 使用封装的 Result 类型来统一处理结果
    private val _buyResult = MutableLiveData<Result<String>>()
    val buyResult: LiveData<Result<String>> get() = _buyResult

    /**
     * 购买股票
     */
    fun buyStock(request: BuyRequest) = viewModelScope.launch {
        try {
            RetrofitClient.apiService.buyStock(request)
            emitSuccess("买入成功")
        } catch (e: Exception) {
            emitError(e)
        }
    }

    /**
     * 发送成功事件
     */
    private fun emitSuccess(message: String) {
        _buyResult.postValue(Result.Success(message))
    }

    /**
     * 发送错误事件
     */
    private fun emitError(exception: Exception) {
        _buyResult.postValue(Result.Error(exception))
    }
}