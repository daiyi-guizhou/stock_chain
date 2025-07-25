package com.example.stockapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R
import com.example.stockapp.adapter.StockAdapter
import com.example.stockapp.adapter.UserAdapter
import com.example.stockapp.network.RetrofitClient
import com.example.stockapp.viewmodel.StockViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var stockViewModel: StockViewModel
    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var userRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initViews()
            initViewModel()
            observeData()
            loadData()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
        }
    }

    private fun initViews() {
        stockRecyclerView = findViewById(R.id.stockRecyclerView)
        userRecyclerView = findViewById(R.id.userRecyclerView)

        stockRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initViewModel() {
        stockViewModel = ViewModelProvider(this)[StockViewModel::class.java]
    }

    private fun observeData() {
        stockViewModel.stocks.observe(this) { stocks ->
            stockRecyclerView.adapter = StockAdapter(stocks ?: emptyMap())
        }

        stockViewModel.users.observe(this) { users ->
            userRecyclerView.adapter = UserAdapter(users ?: emptyMap())
        }

        stockViewModel.error.observe(this) { error ->
            Log.e("MainActivity", "API Error: $error")
        }
    }

    private fun loadData() {
        stockViewModel.loadStocksAndUsers(RetrofitClient.apiService)
    }
}