package com.example.stockapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R
import com.example.stockapp.adapter.StockAdapter
import com.example.stockapp.adapter.UserAdapter
import com.example.stockapp.adapter.UserStockAdapter
import com.example.stockapp.network.RetrofitClient
import com.example.stockapp.viewmodel.StockViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var stockViewModel: StockViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var userStocksRecyclerView: RecyclerView

    // Spinners for selections
    private lateinit var spinnerSelectUser: Spinner
    private lateinit var spinnerBuyUser: Spinner
    private lateinit var spinnerBuyStock: Spinner
    private lateinit var spinnerSellUser: Spinner
    private lateinit var spinnerSellStock: Spinner
    private lateinit var spinnerQueryUser: Spinner
    private lateinit var spinnerQueryStock: Spinner
    private lateinit var spinnerDeleteUser: Spinner

    // Buttons
    private lateinit var btnQueryUserStocks: Button
    private lateinit var btnBuy: Button
    private lateinit var btnSell: Button
    private lateinit var btnQueryStockQuantity: Button
    private lateinit var btnQueryTotalValue: Button
    private lateinit var btnCloseAccount: Button

    // Input fields
    private lateinit var etBuyAmount: EditText
    private lateinit var etSellAmount: EditText

    // Result text views
    private lateinit var tvStockQuantity: TextView
    private lateinit var tvTotalValue: TextView

    // Data lists
    private var userList: List<String> = listOf()
    private var stockList: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initViews()
            initRecyclerViews()
            initViewModel()
            observeData()
            loadData()
            setupClickListeners()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
        }
    }

    private fun initViews() {
        userRecyclerView = findViewById(R.id.userRecyclerView)
        stockRecyclerView = findViewById(R.id.stockRecyclerView)
        userStocksRecyclerView = findViewById(R.id.userStocksRecyclerView)

        spinnerSelectUser = findViewById(R.id.spinnerSelectUser)
        spinnerBuyUser = findViewById(R.id.spinnerBuyUser)
        spinnerBuyStock = findViewById(R.id.spinnerBuyStock)
        spinnerSellUser = findViewById(R.id.spinnerSellUser)
        spinnerSellStock = findViewById(R.id.spinnerSellStock)
        spinnerQueryUser = findViewById(R.id.spinnerQueryUser)
        spinnerQueryStock = findViewById(R.id.spinnerQueryStock)
        spinnerDeleteUser = findViewById(R.id.spinnerDeleteUser)

        btnQueryUserStocks = findViewById(R.id.btnQueryUserStocks)
        btnBuy = findViewById(R.id.btnBuy)
        btnSell = findViewById(R.id.btnSell)
        btnQueryStockQuantity = findViewById(R.id.btnQueryStockQuantity)
        btnQueryTotalValue = findViewById(R.id.btnQueryTotalValue)
        btnCloseAccount = findViewById(R.id.btnCloseAccount)

        etBuyAmount = findViewById(R.id.etBuyAmount)
        etSellAmount = findViewById(R.id.etSellAmount)

        tvStockQuantity = findViewById(R.id.tvStockQuantity)
        tvTotalValue = findViewById(R.id.tvTotalValue)
    }

    private fun initRecyclerViews() {
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        stockRecyclerView.layoutManager = LinearLayoutManager(this)
        userStocksRecyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化空的适配器以避免"skipping layout"警告
        userRecyclerView.adapter = UserAdapter(emptyMap())
        stockRecyclerView.adapter = StockAdapter(emptyMap())
        userStocksRecyclerView.adapter = UserStockAdapter(emptyList())
    }

    private fun initViewModel() {
        stockViewModel = ViewModelProvider(this)[StockViewModel::class.java]
    }

    private fun observeData() {
        stockViewModel.stocks.observe(this) { stocks ->
            Log.d("MainActivity", "Received stocks data: ${stocks?.size ?: 0} items")
            if (stocks != null) {
                stockRecyclerView.adapter = StockAdapter(stocks)
                // 更新股票列表
                stockList = stocks.keys.toList()
                updateSpinners()
            } else {
                stockRecyclerView.adapter = StockAdapter(emptyMap())
            }
        }

        stockViewModel.users.observe(this) { users ->
            Log.d("MainActivity", "Received users data: ${users?.size ?: 0} items")
            if (users != null) {
                userRecyclerView.adapter = UserAdapter(users)
                // 更新用户列表
                userList = users.keys.toList()
                updateSpinners()
            } else {
                userRecyclerView.adapter = UserAdapter(emptyMap())
            }
        }

        stockViewModel.userStocks.observe(this) { userStocks ->
            Log.d("MainActivity", "Received user stocks data: ${userStocks.size} items")
            userStocksRecyclerView.adapter = UserStockAdapter(userStocks)
        }

        stockViewModel.error.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "API Error: $error")
        }
    }

    private fun loadData() {
        Log.d("MainActivity", "Loading data...")
        stockViewModel.loadStocksAndUsers(RetrofitClient.apiService)
    }

    private fun updateSpinners() {
        val userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userList)
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val stockAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stockList)
        stockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSelectUser.adapter = userAdapter
        spinnerBuyUser.adapter = userAdapter
        spinnerSellUser.adapter = userAdapter
        spinnerQueryUser.adapter = userAdapter
        spinnerDeleteUser.adapter = userAdapter

        spinnerBuyStock.adapter = stockAdapter
        spinnerSellStock.adapter = stockAdapter
        spinnerQueryStock.adapter = stockAdapter
    }

    private fun setupClickListeners() {
        btnQueryUserStocks.setOnClickListener {
            val selectedUser = spinnerSelectUser.selectedItem?.toString() ?: return@setOnClickListener
            stockViewModel.loadUserStocks(selectedUser, RetrofitClient.apiService)
        }

        btnBuy.setOnClickListener {
            val selectedUser = spinnerBuyUser.selectedItem?.toString() ?: return@setOnClickListener
            val selectedStock = spinnerBuyStock.selectedItem?.toString() ?: return@setOnClickListener
            val amountStr = etBuyAmount.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入买入数量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toIntOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "请输入有效的买入数量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 简化处理，假设股价为100
            val payment = amount * 100.0
            stockViewModel.buyStock(selectedUser, selectedStock, amount, payment, RetrofitClient.apiService)
        }

        btnSell.setOnClickListener {
            val selectedUser = spinnerSellUser.selectedItem?.toString() ?: return@setOnClickListener
            val selectedStock = spinnerSellStock.selectedItem?.toString() ?: return@setOnClickListener
            val amountStr = etSellAmount.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入卖出数量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toIntOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "请输入有效的卖出数量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            stockViewModel.sellStock(selectedUser, selectedStock, amount, RetrofitClient.apiService)
        }

        btnQueryStockQuantity.setOnClickListener {
            val selectedUser = spinnerQueryUser.selectedItem?.toString() ?: return@setOnClickListener
            val selectedStock = spinnerQueryStock.selectedItem?.toString() ?: return@setOnClickListener

            stockViewModel.getUserStockCount(selectedUser, selectedStock, RetrofitClient.apiService)
        }

        btnQueryTotalValue.setOnClickListener {
            val selectedUser = spinnerQueryUser.selectedItem?.toString() ?: return@setOnClickListener

            stockViewModel.getUserTotalValue(selectedUser, RetrofitClient.apiService)
        }

        btnCloseAccount.setOnClickListener {
            val selectedUser = spinnerDeleteUser.selectedItem?.toString() ?: return@setOnClickListener

            stockViewModel.closeAccount(selectedUser, RetrofitClient.apiService)
        }
    }
}