package handler

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/hyperledger/fabric-gateway/pkg/client"
	"server/model"
)

type UserStocksResponse struct {
	Stocks map[string]int `json:"stocks"`
}

type UserTotalValueResponse struct {
	TotalValue float64 `json:"totalValue"`
}

type StockPriceResponse struct {
	Price float64 `json:"price"`
}

type SellStockResponse struct {
	Revenue float64 `json:"revenue"`
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

	// 调用智能合约的 BuyStock 函数
	_, err := contract.SubmitTransaction("BuyStock", req.Username, req.StockID, strconv.Itoa(req.Amount), fmt.Sprintf("%.2f", req.Payment))
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Buy transaction submitted successfully"})
}

func SellStock(contract *client.Contract, c *gin.Context) {
	var req model.SellStockRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// 调用智能合约的 SellStock 函数
	result, err := contract.SubmitTransaction("SellStock", req.Username, req.StockID, strconv.Itoa(req.Amount))
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	// 解析返回的收入金额
	revenue, err := strconv.ParseFloat(string(result), 64)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse revenue value"})
		return
	}
	
	c.JSON(http.StatusOK, SellStockResponse{Revenue: revenue})
}

func GetStockPrice(contract *client.Contract, c *gin.Context) {
	stockID := c.Param("stockID")
	
	// 调用智能合约的 GetStockPrice 函数
	result, err := contract.EvaluateTransaction("GetStockPrice", stockID)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	// 解析返回的价格
	price, err := strconv.ParseFloat(string(result), 64)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse price value"})
		return
	}
	
	c.JSON(http.StatusOK, StockPriceResponse{Price: price})
}

func GetUserStockCount(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	stockID := c.Param("stockID")
	
	// 调用智能合约的 GetUserStockCount 函数
	result, err := contract.EvaluateTransaction("GetUserStockCount", username, stockID)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	// 解析返回的股票数量
	count, err := strconv.Atoi(string(result))
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse stock count"})
		return
	}
	
	c.JSON(http.StatusOK, gin.H{"count": count})
}

func GetUserStocks(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	
	// 调用智能合约的 GetUserStockCount 函数来获取每只股票的数量
	// 但我们还需要一个方法来获取用户持有的所有股票ID
	// 这里我们假设前端知道要查询哪些股票，或者我们提供一个获取所有股票的方法
	
	// 作为替代方案，我们可以添加一个 GetAllStockIDs 方法到智能合约中
	// 或者在这里查询所有股票然后逐一检查用户是否持有
	
	// 目前直接使用 GetUserStockCount 对每只股票进行查询
	// 更好的方法是在智能合约中添加一个 GetUserStocks 方法
	
	// 为了保持一致性，这里暂时保留原逻辑，但建议在智能合约中添加 GetUserStocks 方法
	result, err := contract.EvaluateTransaction("GetAllStock")
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	var allStocks map[string]interface{}
	_ = json.Unmarshal(result, &allStocks)

	userStocks := make(map[string]int)
	for key := range allStocks {
		if len(key) > 6 && key[:6] == "stock_" {
			stockID := key[6:] // 移除 "stock_" 前缀
			countResult, err := contract.EvaluateTransaction("GetUserStockCount", username, stockID)
			if err != nil {
				continue // 用户可能不持有这只股票
			}
			
			count, err := strconv.Atoi(string(countResult))
			if err != nil {
				continue
			}
			
			if count > 0 { // 只返回持有数量大于0的股票
				userStocks[stockID] = count
			}
		}
	}

	c.JSON(http.StatusOK, UserStocksResponse{Stocks: userStocks})
}

func GetUserTotalValue(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	
	// 调用智能合约的 GetUserTotalValue 函数
	result, err := contract.EvaluateTransaction("GetUserTotalValue", username)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	// 解析返回的总价值
	totalValue, err := strconv.ParseFloat(string(result), 64)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse total value"})
		return
	}
	
	c.JSON(http.StatusOK, UserTotalValueResponse{TotalValue: totalValue})
}

func GetAllAssets(contract *client.Contract, c *gin.Context) {
	// 调用智能合约的 GetAllAssets 函数
	result, err := contract.EvaluateTransaction("GetAllAssets")
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	var assets map[string]interface{}
	err = json.Unmarshal(result, &assets)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse assets"})
		return
	}
	
	c.JSON(http.StatusOK, assets)
}

func GetAllStocks(contract *client.Contract, c *gin.Context) {
	// 调用智能合约的 GetAllStock 函数
	result, err := contract.EvaluateTransaction("GetAllStock")
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	var stocks map[string]interface{}
	err = json.Unmarshal(result, &stocks)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse stocks"})
		return
	}
	
	c.JSON(http.StatusOK, stocks)
}

func GetAllUsers(contract *client.Contract, c *gin.Context) {
	// 调用智能合约的 GetAllUser 函数
	result, err := contract.EvaluateTransaction("GetAllUser")
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	var users map[string]interface{}
	err = json.Unmarshal(result, &users)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse users"})
		return
	}
	
	c.JSON(http.StatusOK, users)
}

func CloseAccount(contract *client.Contract, c *gin.Context) {
	username := c.Param("username")
	
	// 调用智能合约的 CloseAccount 函数
	_, err := contract.SubmitTransaction("CloseAccount", username)
	if err != nil {
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	c.JSON(http.StatusOK, gin.H{"message": fmt.Sprintf("Account %s closed successfully", username)})
}