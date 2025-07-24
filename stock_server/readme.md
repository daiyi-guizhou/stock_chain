server/
├── main.go
├── handler/
│   └── stock_handler.go
├── service/
│   └── chaincode_service.go
├── config/
│   └── fabric_config.go
├── model/
│   └── request.go
└── go.mod

go mod init server
go get github.com/hyperledger/fabric-gateway/pkg/client
go get github.com/gin-gonic/gin


```sh
cd server
go run main.go


# 请确保网络连接正常，能够访问GitHub和Google的代码仓库。如果在大陆地区访问较慢，可以设置GOPROXY：
go env -w GOPROXY=https://goproxy.cn,direct

# 安装所有缺失的依赖
go get github.com/gin-gonic/gin \
github.com/hyperledger/fabric-gateway/pkg/client \
github.com/hyperledger/fabric-gateway/pkg/identity \
google.golang.org/grpc \
google.golang.org/grpc/credentials

# 整理模块依赖
go mod tidy

```