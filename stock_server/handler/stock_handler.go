package handler

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/hyperledger/fabric-gateway/pkg/client"
	"server/model"
)

type UserStocksResponse struct {
	Stocks map[string]int `json:"stocks"`
}

func InitLedger(contract *client.Contract, c *gin.Context) {
	_, err := contract.SubmitTransaction("InitLedger")
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Ledger initialized"})
}

func BuyStock(contract *client.Contract, c *gin.Context) {
	var req model.BuyStockRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	_, err := contract.SubmitTransaction("BuyStock", req.Username, req.StockID, fmt.Sprintf("%d", req.Amount), fmt.Sprintf("%.2f", req.Payment))
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Buy transaction submitted"})
}

func SellStock(contract *client.Contract, c *gin.Context) {
	var req model.SellStockRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	result, err := contract.SubmitTransaction("SellStock", req.Username, req.StockID, fmt.Sprintf("%d", req.Amount))
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"revenue": string(result)})
}

func GetStockPrice(contract *client.Contract, c *gin.Context) {
	stockID := c.Param("stockID")
	result, err := contract.EvaluateTransaction("GetStockPrice", stockID)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"price": string(result)})
}

func GetUserStocks(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	result, err := contract.EvaluateTransaction("GetUserStocks", username)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	var stocks map[string]int
	_ = json.Unmarshal(result, &stocks)

	c.JSON(http.StatusOK, UserStocksResponse{Stocks: stocks})
}

func GetUserTotalValue(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	result, err := contract.EvaluateTransaction("GetUserTotalValue", username)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"totalValue": string(result)})
}