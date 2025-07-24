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
```