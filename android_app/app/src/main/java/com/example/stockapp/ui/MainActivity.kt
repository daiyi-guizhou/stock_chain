package com.example.stockapp.ui
// 声明包名，表示这个类属于 com.example.stockapp.ui 包，相当于Python中的模块路径
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

// 导入所需的类和包：

// android.os.Bundle 用于传递数据
// android.util.Log 用于日志输出
// android.widget.* 导入所有UI组件
// AppCompatActivity 是Android Activity的基类
// ViewModelProvider 用于创建和管理ViewModel
// RecyclerView 相关类用于列表展示
// R 是资源文件的引用
// 自定义的适配器、网络客户端和ViewModel


/**
 * 主界面Activity，负责展示用户、股票信息，并处理用户的买入、卖出、查询等操作。
 *
 * 继承自 [AppCompatActivity]，是整个应用的入口页面。
 */
class MainActivity : AppCompatActivity() {
    private lateinit var stockViewModel: StockViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var userStocksRecyclerView: RecyclerView
    // 声明私有变量：
    //     stockViewModel 用于管理应用数据和业务逻辑
    //     三个 RecyclerView 用于显示用户、股票和用户持仓列表


    // Spinners for selections
    // 声明多个 Spinner（下拉选择框），用于用户和股票的选择操作。
    private lateinit var spinnerSelectUser: Spinner
    private lateinit var spinnerBuyUser: Spinner
    private lateinit var spinnerBuyStock: Spinner
    private lateinit var spinnerSellUser: Spinner
    private lateinit var spinnerSellStock: Spinner
    private lateinit var spinnerQueryUser: Spinner
    private lateinit var spinnerQueryStock: Spinner
    private lateinit var spinnerDeleteUser: Spinner

    // Buttons
    // 声明按钮组件，对应界面上的各种操作按钮
    private lateinit var btnQueryUserStocks: Button
    private lateinit var btnBuy: Button
    private lateinit var btnSell: Button
    private lateinit var btnQueryStockQuantity: Button
    private lateinit var btnQueryTotalValue: Button
    private lateinit var btnCloseAccount: Button

    // Input fields
    // 声明输入框组件，用于输入买卖数量。
    private lateinit var etBuyAmount: EditText
    private lateinit var etSellAmount: EditText

    // Result text views
    // 声明文本视图组件，用于显示查询结果。
    private lateinit var tvStockQuantity: TextView
    private lateinit var tvTotalValue: TextView

    // Data lists
    // 声明用户和股票列表，用于存储从服务器获取的数据。
    private var userList: List<String> = listOf()
    private var stockList: List<String> = listOf()

    /**
     * Activity 的入口方法，在此初始化布局、组件、监听器等。
     *
     * @param savedInstanceState 保存的实例状态，可用于恢复之前的状态
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化各种组件并设置监听器，使用try-catch捕获可能的异常
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


    /**
     * 初始化所有UI组件，通过 findViewById 找到布局中定义的控件。
     */
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

        // 初始化所有Spinner和Button组件
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

    /**
     * 初始化 RecyclerView 并设置布局管理器和初始适配器。
     */
    private fun initRecyclerViews() {
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        stockRecyclerView.layoutManager = LinearLayoutManager(this)
        userStocksRecyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化空的适配器以避免"skipping layout"警告
        // 为RecyclerView设置适配器，初始时使用空数据
        userRecyclerView.adapter = UserAdapter(emptyMap())
        stockRecyclerView.adapter = StockAdapter(emptyMap())
        userStocksRecyclerView.adapter = UserStockAdapter(emptyList())
    }

    /**
     * 初始化 ViewModel，用于管理UI相关的数据。
     */
    private fun initViewModel() {
        stockViewModel = ViewModelProvider(this)[StockViewModel::class.java]
    }

    /**
     * 观察 ViewModel 中的数据变化，并更新 UI。
        观察股票数据变化，当数据更新时刷新UI：

        使用 Log.d 输出调试信息
        更新股票列表和下拉框
        设置适配器显示数据
     */
    private fun observeData() {
        stockViewModel.stocks.observe(this) { stocks ->
            Log.d("MainActivity", "Received stocks data: ${stocks?.size ?: 0} items")
            if (stocks != null) {
                stockRecyclerView.adapter = StockAdapter(stocks)
                // 更新股票列表
                // stockList = stocks.keys.toList()  
                stockList = stocks.values.map { it.symbol }.toList()

                updateSpinners()
            } else {
                stockRecyclerView.adapter = StockAdapter(emptyMap())
            }
        }

        // 观察用户数据变化，更新用户列表和相关UI。
        stockViewModel.users.observe(this) { users ->
            Log.d("MainActivity", "Received users data: ${users?.size ?: 0} items")
            if (users != null) {
                userRecyclerView.adapter = UserAdapter(users)
                // 更新用户列表
                // userList = users.keys.toList()
                // userList = users.values.map { it.username }.toList()
                userList = users.values.map { it.name }.toList()
                updateSpinners()
            } else {
                userRecyclerView.adapter = UserAdapter(emptyMap())
            }
        }
        // 观察用户持仓数据变化，更新持仓列表
        stockViewModel.userStocks.observe(this) { userStocks ->
            Log.d("MainActivity", "Received user stocks data: ${userStocks.size} items")
            userStocksRecyclerView.adapter = UserStockAdapter(userStocks)
        }
        // 观察错误信息，当发生错误时显示提示并记录日志。
        stockViewModel.error.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "API Error: $error")
        }
    }

    /**
     * 加载股票和用户数据。  加载股票和用户数据，通过ViewModel调用网络API
     */
    private fun loadData() {
        Log.d("MainActivity", "Loading data...")
        stockViewModel.loadStocksAndUsers(RetrofitClient.apiService)
    }

    /**
     * 更新所有下拉选择框的内容。
     */
    private fun updateSpinners() {
        val userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userList)
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val stockAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stockList)
        stockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 更新下拉框的数据适配器

        spinnerSelectUser.adapter = userAdapter
        spinnerBuyUser.adapter = userAdapter
        spinnerSellUser.adapter = userAdapter
        spinnerQueryUser.adapter = userAdapter
        spinnerDeleteUser.adapter = userAdapter

        spinnerBuyStock.adapter = stockAdapter
        spinnerSellStock.adapter = stockAdapter
        spinnerQueryStock.adapter = stockAdapter
        // 将适配器应用到各个Spinner组件
    }

    /**
     * 设置所有按钮的点击事件监听器。
     设置按钮点击监听器，当用户点击"查询用户持仓"按钮时：

        获取选中的用户
        调用ViewModel加载该用户的持仓数据
     */
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

        // 设置买入按钮的点击监听器：

        // 获取用户选择和输入数量
        // 验证输入有效性
        // 调用ViewModel执行买入操作
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

// 总结
// MainActivity.kt 相当于Django中的视图(View)和模板(Template)的结合体：

// 初始化阶段 - 类似于Django视图函数的开始部分，加载模板和数据
// UI组件管理 - 类似于Django模板中的HTML元素
// 事件处理 - 类似于Django中的表单处理
// 数据绑定 - 通过ViewModel和适配器将数据绑定到UI，类似于Django模板中的变量替换
// 网络请求 - 通过RetrofitClient调用后端API，类似于Django中调用外部服务
// 整体流程是：用户操作界面 → 触发事件监听器 → 调用ViewModel处理业务逻辑 → 通过网络层与后端通信 → 更新UI显示结果。