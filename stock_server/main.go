package main

import (
	"fmt"
	"log"

	"github.com/gin-gonic/gin"
	"github.com/hyperledger/fabric-gateway/pkg/client"
	"server/config"
	"server/handler"
)

func main() {
	// 初始化 Gateway 连接
	conn := config.NewGrpcConnection()
	defer conn.Close()

	id := config.NewIdentity()
	sign := config.NewSign()

	gw, err := client.Connect(
		id,
		client.WithSign(sign),
		client.WithClientConnection(conn),
	)
	if err != nil {
		log.Fatalf("Failed to connect to gateway: %v", err)
	}
	defer gw.Close()

	network := gw.GetNetwork(config.ChannelName)
	contract := network.GetContract(config.ChaincodeName)

	r := gin.Default()

	// 初始化账本
	r.POST("/init", func(c *gin.Context) {
		handler.InitLedger(contract, c)
	})

	// 买入股票
	r.POST("/buy", func(c *gin.Context) {
		handler.BuyStock(contract, c)
	})

	// 卖出股票
	r.POST("/sell", func(c *gin.Context) {
		handler.SellStock(contract, c)
	})

	// 查询股价
	r.GET("/price/:stockID", func(c *gin.Context) {
		handler.GetStockPrice(contract, c)
	})

	// 查询用户持仓数量
	r.GET("/user/:username/stocks/:stockID", func(c *gin.Context) {
		handler.GetUserStockCount(contract, c)
	})

	// 查询用户所有持仓
	r.GET("/user/:username/stocks", func(c *gin.Context) {
		handler.GetUserStocks(contract, c)
	})

	// 查询用户总资产
	r.GET("/user/:username/value", func(c *gin.Context) {
		handler.GetUserTotalValue(contract, c)
	})

	// 获取账本中所有资产（股票 + 用户）
	r.GET("/assets", func(c *gin.Context) {
		handler.GetAllAssets(contract, c)
	})

	// 获取账本中所有股票
	r.GET("/stocks", func(c *gin.Context) {
		handler.GetAllStocks(contract, c)
	})

	// 获取账本中所有用户
	r.GET("/users", func(c *gin.Context) {
		handler.GetAllUsers(contract, c)
	})

	// 关闭用户账户
	r.DELETE("/user/:username", func(c *gin.Context) {
		handler.CloseAccount(contract, c)
	})

	fmt.Println("Server running on :8080")
	if err := r.Run(":8080"); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}