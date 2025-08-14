
// 总结
// MainActivity.kt 相当于Django中的视图(View)和模板(Template)的结合体：

// 初始化阶段 - 类似于Django视图函数的开始部分，加载模板和数据
// UI组件管理 - 类似于Django模板中的HTML元素
// 事件处理 - 类似于Django中的表单处理
// 数据绑定 - 通过ViewModel和适配器将数据绑定到UI，类似于Django模板中的变量替换
// 网络请求 - 通过RetrofitClient调用后端API，类似于Django中调用外部服务
// 整体流程是：用户操作界面 → 触发事件监听器 → 调用ViewModel处理业务逻辑 → 通过网络层与后端通信 → 更新UI显示结果。

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
import com.example.stockapp.model.UserStocksResponse

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
    /**
        * 1. 属性修饰符
        *     private: 访问修饰符，表示这个变量只能在 MainActivity 类内部访问
        *     lateinit: Kotlin特有的委托属性，表示延迟初始化
        * 2. 变量类型
        *     var: 表示这是一个可变变量（相对于 val 是不可变的）
        *     spinnerSelectUser: 变量名，表示"选择用户的下拉框"
        *     Spinner: 变量类型，是Android中的UI组件，提供下拉选择功能

        基础UI组件
            TextView - 用于显示文本内容
            EditText - 可编辑的文本输入框
            Button - 按钮组件，用于触发操作
            ImageButton - 显示图片的按钮
            CheckBox - 复选框，可选择多个选项
            RadioButton - 单选按钮，通常在RadioGroup中使用
            Switch - 开关按钮
            ToggleButton - 双状态按钮
        选择类组件
            Spinner - 下拉选择框
            DatePicker - 日期选择器
            TimePicker - 时间选择器
            SeekBar - 拖动条，用于选择数值
            RatingBar - 评分条
        布局容器组件
            LinearLayout - 线性布局，子元素按水平或垂直排列
            RelativeLayout - 相对布局，子元素相对定位
            ConstraintLayout - 约束布局，更灵活的布局方式
            FrameLayout - 帧布局，子元素可以重叠
            GridLayout - 网格布局
            ScrollView - 滚动视图，用于内容超出屏幕时滚动显示
        列表和适配器组件
            ListView - 列表视图（较老的组件）
            RecyclerView - 更高效的列表组件
            GridView - 网格视图
            ExpandableListView - 可展开的列表视图
        */


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
    // 初始化 = listOf(): 使用 listOf() 函数创建一个空的只读字符串列表

    /**
     * 
     Kotlin中的变量类型非常丰富，主要可以分为以下几类：

        ## 1. 基本数据类型
            ### 数字类型
            - `Int`: 32位整数 (例如: 42)
            - `Long`: 64位整数 (例如: 42L)
            - `Short`: 16位整数 (例如: 42)
            - `Byte`: 8位整数 (例如: 42)
            - `Double`: 64位浮点数 (例如: 3.14)
            - `Float`: 32位浮点数 (例如: 3.14f)

            ### 布尔类型
            - `Boolean`: 布尔值 (true 或 false)

            ### 字符类型
            - `Char`: 单个字符 (例如: 'A')

            ### 字符串类型
            - `String`: 字符串 (例如: "Hello World")

        ## 2. 集合类型
            ### 列表
            - `List<T>`: 只读列表
            - `MutableList<T>`: 可变列表
            - `ArrayList<T>`: 基于数组实现的可变列表

            ### 集合
            - `Set<T>`: 只读集合（无重复元素）
            - `MutableSet<T>`: 可变集合
            - `HashSet<T>`: 基于哈希表的可变集合

            ### 映射
            - `Map<K, V>`: 只读映射（键值对）
            - `MutableMap<K, V>`: 可变映射
            - `HashMap<K, V>`: 基于哈希表的可变映射

        ## 3. 特殊类型
            ### 空类型
            - `null`: 表示空值
            - `T?`: 可空类型（任何类型后面加?表示可以为null）

            ### Unit类型
            - `Unit`: 类似于Java中的void，表示无返回值

            ### Nothing类型
            - `Nothing`: 表示无返回值且无返回（通常用于异常抛出）

        ## 4. 函数类型
            ### 函数引用
            - `(参数类型) -> 返回类型`: 函数类型
            - 例如: `(Int, Int) -> Int` 表示接受两个Int参数返回Int的函数

            ### 高阶函数
            - `FunctionN<T>`: 函数类型接口

        ## 5. 数组类型
            - `Array<T>`: 泛型数组
            - `IntArray`: 整数数组
            - `DoubleArray`: 双精度浮点数组
            - `BooleanArray`: 布尔数组等

        ## 6. 可空和非空类型
            ```kotlin
            // 非空类型
            val name: String = "Kotlin"
            // 可空类型
            val nullableName: String? = null

            // 安全调用
            val length = nullableName?.length

            // Elvis操作符
            val len = nullableName?.length ?: 0
            ```

        ## 7. 泛型类型
            List<String>        // 字符串列表
            Map<String, Int>    // 字符串到整数的映射
            MutableList<User>   // 可变的用户列表

        ## 8. 平台类型
        - 当与Java代码交互时，Kotlin会自动推断Java类型为平台类型（用!表示）

        ## 9. 自定义类型

            - 类（Class）
            - 接口（Interface）
            - 对象（Object）
            - 枚举（Enum）
     */

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
        // 通过资源ID绑定XML布局中的RecyclerView组件

        /**
        * R.id.userRecyclerView 是在XML布局文件中定义的RecyclerView组件的资源ID  --android:id="@+id/userRecyclerView"
        userRecyclerView: 一个RecyclerView类型的变量
        userRecyclerView.layoutManager = LinearLayoutManager(this)   为RecyclerView设置布局管理器  LinearLayoutManager使列表项垂直排列  this表示当前的Activity上下文
        userRecyclerView.adapter = UserAdapter(emptyMap()) // 设置适配器 UserAdapter是自定义的适配器类，负责将数据绑定到列表项，初始时使用空数据
        */
        // 
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
        userStocksRecyclerView.adapter = UserStockAdapter(UserStocksResponse())
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
        // stockViewModel.userStocks.observe(this) { userStocks ->
        //     Log.d("MainActivity", "Received user stocks data: ${userStocks ?.size ?:0} items")
        //     userStocksRecyclerView.adapter = UserStockAdapter(userStocks)
        // }
        stockViewModel.userStocksResp.observe(this) { userStocksResp ->
            Log.d("MainActivity", "Received user stocks data: ${userStocksResp ?:0} items")
            userStocksRecyclerView.adapter = UserStockAdapter(userStocksResp)
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
        /** 
        ArrayAdapter(this, android.R.layout.simple_spinner_item, userList) 创建了一个数组适配器
            this 表示当前的上下文（MainActivity）
            android.R.layout.simple_spinner_item 是Android系统提供的标准下拉项布局
            userList 是要显示在下拉框中的用户数据列表
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) 设置下拉框展开后的样式
            使用系统提供的 android.R.layout.simple_spinner_dropdown_item 作为下拉项的显示样式
            这个样式通常比普通项更大，更适合触摸选择
         */
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
        // 为 btnQueryUserStocks 按钮设置点击事件监听器
        // 当用户点击这个按钮时，花括号 {} 内的代码会被执行
        btnQueryUserStocks.setOnClickListener {
            // 从 spinnerSelectUser 下拉选择框中获取当前选中的项目
            // 使用安全调用操作符 ?. 避免空指针异常，将其转换为字符串
            // 使用 Elvis 操作符 ?: 进行空值检查：如果选中项为空，则直接从当前的点击监听器返回，不执行后续代码
            val selectedUser = spinnerSelectUser.selectedItem?.toString() ?: return@setOnClickListener
            // 调用 stockViewModel 的 loadUserStocks 方法
            // 传入选中的用户名和 API 服务实例，加载该用户的股票持仓数据
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
