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
