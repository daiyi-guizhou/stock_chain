 # 部署智能合约，启动网络和通道
 ```sh
 cd /mnt/d/project/coin_stock/stock_chain/fabric-samples-main/test-network
./network.sh down

./network.sh up
./network.sh createChannel

# 部署智能合约
./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-go -ccl go
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
# Environment variables for Org1
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n basic --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"function":"InitLedger","Args":[]}'

# 检查数据
peer chaincode query -C mychannel -n basic -c '{"Args":["GetAllAssets"]}'
peer chaincode query -C mychannel -n basic -c '{"Args":["GetAllUser"]}'
peer chaincode query -C mychannel -n basic -c '{"Args":["GetAllStock"]}'
```

# 启动服务端

## 编译准备
```sh 
cd /mnt/d/project/coin_stock/stock_chain/stock_server/
go get github.com/hyperledger/fabric-gateway/pkg/client
go get github.com/gin-gonic/gin
go get github.com/gin-gonic/gin github.com/hyperledger/fabric-gateway/pkg/client github.com/hyperledger/fabric-gateway/pkg/identity google.golang.org/grpc google.golang.org/grpc/credentials
go env -w GOPROXY=https://goproxy.cn,direct
go get github.com/gin-gonic/gin github.com/hyperledger/fabric-gateway/pkg/client github.com/hyperledger/fabric-gateway/pkg/identity google.golang.org/grpc google.golang.org/grpc/credentials

go get github.com/hyperledger/fabric-gateway/pkg/client
go get github.com/gin-gonic/gin
ls
```
# 启动服务
```sh
cd /mnt/d/project/coin_stock/stock_chain/stock_server/
# 整理依赖（在项目根目录执行）
go mod tidy
# 再次运行服务
ps -ef |grep main.go|grep -v color|awk '{print $2}'|xargs kill -9 ;
go run main.go


# 验证数据
curl -XGET http://localhost:8080/assets
curl -XGET http://localhost:8080/stocks
curl -XGET http://localhost:8080/users

# 价格不对，购买失败
curl -X POST http://localhost:8080/buy \  
  -H "Content-Type: application/json" \
  -d '{
    "username": "Alice",
    "stock_id": "TSLA",
    "amount": 1,
    "payment": 100.00
  }'

# 价格正确，购买成功
curl -X POST http://localhost:8080/buy \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Alice",
    "stock_id": "TSLA",
    "amount": 10,
    "payment": 1805.00
  }'
{"message":"Buy transaction submitted successfully"}


curl -X POST http://localhost:8080/sell \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Alice",
    "stock_id": "TSLA",
    "amount": 5
  }'
  {"revenue":902.5}
```

添加数据模型类：定义 StockInfo 和 UserInfo 数据模型类。
修改 MainActivity.kt：在 onCreate 中调用 /stocks 和 /users 接口。
修改 ApiService.kt：添加对应的 API 方法。
修改 StockViewModel.kt：添加加载数据的方法。
android_app/app/src/main/java/com/example/stockapp/ui/MainActivity.kt

android_app/app/src/main/java/com/example/stockapp/adapter/StockAdapter.kt

android_app/app/src/main/res/layout/item_stock.xml
android_app/app/src/main/res/layout/activity_main.xml
修改 activity_main.xml：添加用于展示数据的 RecyclerView。

```xml

    // private const val BASE_URL = "http://172.25.72.89:8080/" // 使用 Android 模拟器访问本机  这是 linux 虚拟机 里的ip
    private const val BASE_URL = "http://192.168.112.47:8080/" // 使用 Android 模拟器访问本机  这是windows 的ip
```

`在Windows管理员权限的命令提示符中执行以下命令`
```cmd
netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=172.25.72.89

netsh advfirewall firewall add rule name="Open Port 8080" dir=in action=allow protocol=TCP localport=8080
```

这是我的项目配置，分为三个部分，
    第一部分：fabric-sample-main 为 开源区块链 hyperledger fabric 的项目, 里面的智能合约  fabric-samples-main\asset-transfer-basic\chaincode-go\chaincode\stockSmartContract.go 是关于股票的；
    第二部分： stock_server 是 go 语言的后端服务端，主要是listen 8080 端口，接收来自前端的请求，然后调用智能合约进行操作；
    第三部分： android_app 是 android 前端项目，主要是展示数据和与后端 stock_server 进行交互；
已知我的服务可以连接打通，android_app 可以连接 stock_server，也可以获取智能合约数据； 现在我希望你根据智能合约的内容，在我的android_app 上提供 用户买入股票，用户卖出股票，查询用户持有某股票的数量，查询用户总资产，删除用户的所有持仓和账户信息 功能，并且可以与后端进行交互，实现数据的实时更新和展示。

# android端开发

app/
├── src/main/
│   ├── java/com/example/stockapp/     # 应用的Java/Kotlin源代码
│   │   ├── adapter/                   # RecyclerView适配器类（类似Django模板标签）
│   │   ├── model/                     # 数据模型类（类似Django Models）
│   │   ├── network/                   # 网络请求相关类（类似Django中调用外部API）
│   │   ├── ui/                        # 界面Activity和Fragment（类似Django Views + Templates）
│   │   └── viewmodel/                # ViewModel类（MVVM架构，类似Django Views的逻辑处理）
│   ├── res/                          # 应用资源文件
│   │   ├── layout/                   # 布局文件（XML）（类似Django Templates）
│   │   └── values/                   # 资源值文件（字符串、颜色、样式等）
│   └── AndroidManifest.xml           # 应用配置文件（类似Django的settings.py）

##  1. 用户界面层 (相当于Django的Templates + Views)
###  AndroidManifest.xml 入口
从 AndroidManifest.xml 可以看出，应用有两个主要界面：

MainActivity - 主界面
BuyStockActivity - 购买股票界面
当用户在手机上进行操作时（比如点击购买按钮），流程如下：

用户点击界面上的按钮（比如"购买"按钮）
Android系统将这个事件传递给对应的Activity（比如BuyStockActivity.kt）
Activity中的事件处理代码会被执行


## 2. 网络请求层 (相当于Django中调用API)
根据项目结构，网络请求相关代码在 network/ 目录中：

ApiService.kt
这是网络请求的接口定义，使用了Retrofit库（类似于Python中的requests库）：
这相当于在Django中定义API端点，只不过Android使用注解方式定义HTTP方法和路径。

ApiService.kt: 定义所有API端点，使用Retrofit注解定义HTTP请求
RetrofitClient.kt (未显示): 创建和配置Retrofit实例，类似于Django中配置API客户端
## 3. 数据模型 (相当于Django Models)
model/ 目录包含了数据模型类，如 BuyRequest、SellRequest、StockInfo、UserInfo 等。这些类定义了与后端API交互的数据结构，类似于Django中的Model。

## 4. 完整的数据流转过程
用户操作（点击按钮等）
    ↓
UI层(Activity/Fragment)捕获事件
    ↓
ViewModel处理业务逻辑
    ↓
调用ApiService发起网络请求
    ↓
Retrofit将请求转换为HTTP请求发送到后端
    ↓
后端处理请求并返回响应
    ↓
Android应用接收响应并更新UI

3. 布局文件 (res/layout/)
activity_main.xml: 主界面布局，类似于Django模板文件
activity_buy.xml: 购买界面布局
item_stock.xml: 股票列表项布局
item_user.xml: 用户列表项布局
4. 配置文件
AndroidManifest.xml: 应用配置文件，声明了应用组件、权限等，类似于Django的settings.py



总结
这个Android项目与Django项目的主要区别在于：

Android	Django
Activity/Fragment	Views
Layout XML	Templates
ApiService	API调用
Model classes	Models
ViewModel	Views中的业务逻辑
AndroidManifest.xml	settings.py
用户在Android应用中的操作流程与Django Web应用类似，只是表现形式不同。Android应用通过原生界面接收用户输入，通过网络层发送HTTP请求到后端，然后根据响应更新界面。


根据检索到的代码信息，我来详细解释 [spinnerSelectUser](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L45-L45) 的数据来源、使用场景和数据流动过程。

## spinnerSelectUser 数据来源

[spinnerSelectUser](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L45-L45) 的数据来源于用户列表，具体流程如下：

### 1. 数据获取源头
- 数据最初从后端API获取，通过 [RetrofitClient.apiService](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\network\RetrofitClient.kt#L11-L17) 谔用接口
- 在 [loadData()](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L203-L206) 方法中调用 `stockViewModel.loadStocksAndUsers(RetrofitClient.apiService)` 获取用户和股票数据

### 2. ViewModel处理
- [StockViewModel](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\viewmodel\StockViewModel.kt#L10-L152) 中的 [users](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\viewmodel\StockViewModel.kt#L15-L15) LiveData 存储用户数据
- 用户数据以 `Map<String, User>` 格式存储，其中 key 是用户标识，value 是用户对象

### 3. 数据转换
- 在 [observeData()](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L163-L198) 方法中监听 `stockViewModel.users` 的变化
- 当用户数据更新时，提取用户列表的键集合：`userList = users.keys.toList()`
- 这个 [userList](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L75-L75) 是一个 `List<String>`，包含了所有用户的标识

### 4. Spinner数据绑定
- 在 [updateSpinners()](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L211-L229) 方法中，使用 [userList](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L75-L75) 创建 `ArrayAdapter`
- 将适配器设置给 [spinnerSelectUser](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L45-L45)：`spinnerSelectUser.adapter = userAdapter`

## spinnerSelectUser 的使用场景

[spinnerSelectUser](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L45-L45) 主要在以下场景中被使用：

### 1. 查询用户持仓
在 [setupClickListeners()](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L238-L307) 方法中：
```kotlin
btnQueryUserStocks.setOnClickListener {
    val selectedUserKey = spinnerSelectUser.selectedItem?.toString() ?: return@setOnClickListener
    // 从用户数据中获取实际用户名
    val actualUsername = stockViewModel.users.value?.get(selectedUserKey)?.name ?: selectedUserKey
    stockViewModel.loadUserStocks(actualUsername, RetrofitClient.apiService)
}
```

当用户点击"查询用户持仓"按钮时，从 [spinnerSelectUser](file://d:\project\coin_stock\stock_chain\android_app\app\src\main\java\com\example\stockapp\ui\MainActivity.kt#L45-L45) 获取选中的用户，然后查询该用户的持仓信息。

### 2. 下拉选择框显示
作为用户界面的一部分，展示所有可用用户供用户选择。

## 数据流动过程

完整的数据流动过程可以用以下图表表示：

```mermaid
graph TD
    A[后端API] --> B[RetrofitClient]
    B --> C[StockViewModel.loadStocksAndUsers]
    C --> D[stockViewModel.users LiveData]
    D --> E[MainActivity.observeData]
    E --> F[userList = users.keys.toList()]
    F --> G[updateSpinners]
    G --> H[ArrayAdapter创建]
    H --> I[spinnerSelectUser.adapter设置]
    I --> J[用户界面显示]
    J --> K[用户选择操作]
    K --> L[btnQueryUserStocks点击事件]
    L --> M[获取selectedItem]
    M --> N[调用loadUserStocks]
    N --> O[查询用户持仓数据]
```

## 详细步骤说明

1. **初始化阶段**：
   - 在 `initViews()` 中通过 `findViewById(R.id.spinnerSelectUser)` 初始化 `spinnerSelectUser`
   - 在 `onCreate()` 中依次调用 `initViews()`、`observeData()`、`loadData()` 和 `setupClickListeners()`

2. **数据加载阶段**：
   - `loadData()` 触发从后端获取用户和股票数据
   - `observeData()` 监听到数据变化后更新 `userList`

3. **UI更新阶段**：
   - `updateSpinners()` 使用 `userList` 创建适配器并绑定到 `spinnerSelectUser`

4. **用户交互阶段**：
   - 用户从 `spinnerSelectUser` 中选择用户
   - 点击 `btnQueryUserStocks` 时获取选中项并触发查询操作

这种设计实现了数据与界面的解耦，通过 ViewModel 管理数据状态，通过 LiveData 实现数据的响应式更新，保证了UI与数据的一致性。

