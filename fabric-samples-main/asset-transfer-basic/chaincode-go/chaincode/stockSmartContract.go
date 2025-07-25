package chaincode

import (
	"encoding/json"
	"fmt"

	"github.com/hyperledger/fabric-contract-api-go/v2/contractapi"
)

// StockToken 表示股票代币的基本信息
type StockToken struct {
	Symbol   string  `json:"symbol"`   // 股票代码
	Price    float64 `json:"price"`    // 当前股价
	Quantity int     `json:"quantity"` // 持有数量
}

// UserAccount 表示一个用户的账户信息
type UserAccount struct {
	Name     string         `json:"name"`     // 用户名
	Stocks   map[string]int `json:"stocks"`   // 持有的股票代币: key=stockID, value=数量
	Balance  float64        `json:"balance"`  // 可用余额
	History  []string       `json:"history"`  // 交易历史 ⬅️ 本字段必须初始化
}

// StockSmartContract 实现股票代币化逻辑
type StockSmartContract struct {
	contractapi.Contract
}

// InitLedger 初始化账本，发行多种股票并设置默认价格
func (s *StockSmartContract) InitLedger(ctx contractapi.TransactionContextInterface) error {
	// 初始化多种股票
	stocks := []StockToken{
		{Symbol: "TSLA", Price: 180.5, Quantity: 1000000},   // 特斯拉
		{Symbol: "BABA", Price: 85.2, Quantity: 2000000},    // 阿里巴巴
		{Symbol: "0700.HK", Price: 320.0, Quantity: 500000}, // 腾讯
		{Symbol: "AAPL", Price: 150.0, Quantity: 1500000},   // 苹果
		{Symbol: "META", Price: 280.7, Quantity: 800000},    // Meta(Facebook)
	}

	// 将所有股票存入账本，使用 stock_ 前缀
	for _, stock := range stocks {
		stockJSON, _ := json.Marshal(stock)
		stockKey := "stock_" + stock.Symbol
		err := ctx.GetStub().PutState(stockKey, stockJSON)
		if err != nil {
			return fmt.Errorf("failed to put stock %s into ledger: %v", stock.Symbol, err)
		}
	}

	// 初始化多个测试用户
	users := []UserAccount{
		{
			Name:    "Alice",
			Stocks:  map[string]int{"TSLA": 100, "AAPL": 50},
			Balance: 50000.0,
			History: []string{"Initial account setup"},
		},
		{
			Name:    "Bob",
			Stocks:  map[string]int{"BABA": 200, "META": 80},
			Balance: 75000.0,
			History: []string{"Initial account setup"},
		},
		{
			Name:    "Charlie",
			Stocks:  map[string]int{"0700.HK": 150, "TSLA": 75},
			Balance: 60000.0,
			History: []string{"Initial account setup"},
		},
		{
			Name:    "David",
			Stocks:  map[string]int{"AAPL": 120, "META": 60},
			Balance: 45000.0,
			History: []string{"Initial account setup"},
		},
		{
			Name:    "Eve",
			Stocks:  map[string]int{"BABA": 180, "0700.HK": 90},
			Balance: 55000.0,
			History: []string{"Initial account setup"},
		},
	}

	// 将所有用户存入账本
	for _, user := range users {
		userKey := "user_" + user.Name
		userJSON, _ := json.Marshal(user)
		err := ctx.GetStub().PutState(userKey, userJSON)
		if err != nil {
			return fmt.Errorf("failed to initialize user %s: %v", user.Name, err)
		}
	}

	return nil
}

// GetAllAssets 返回账本中的所有资产（股票和用户账户）
func (s *StockSmartContract) GetAllAssets(ctx contractapi.TransactionContextInterface) (map[string]interface{}, error) {
	resultsIterator, err := ctx.GetStub().GetStateByRange("", "")
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	assets := make(map[string]interface{})
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}

		key := queryResponse.Key
		// 根据键名判断数据类型
		if len(key) > 6 && key[:6] == "stock_" {
			var stock StockToken
			err = json.Unmarshal(queryResponse.Value, &stock)
			if err != nil {
				continue
			}
			assets[key] = stock
		} else if len(key) > 5 && key[:5] == "user_" {
			var user UserAccount
			err = json.Unmarshal(queryResponse.Value, &user)
			if err != nil {
				continue
			}
			assets[key] = user
		}
	}

	return assets, nil
}

// GetAllStock 返回账本中的所有股票信息
func (s *StockSmartContract) GetAllStock(ctx contractapi.TransactionContextInterface) (map[string]StockToken, error) {
	resultsIterator, err := ctx.GetStub().GetStateByRange("", "")
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	stocks := make(map[string]StockToken)
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}

		key := queryResponse.Key
		// 只处理股票数据（使用 stock_ 前缀）
		if len(key) > 6 && key[:6] == "stock_" {
			var stock StockToken
			err = json.Unmarshal(queryResponse.Value, &stock)
			if err != nil {
				continue
			}
			stocks[key] = stock
		}
	}

	return stocks, nil
}

// GetAllUser 返回账本中的所有用户信息
func (s *StockSmartContract) GetAllUser(ctx contractapi.TransactionContextInterface) (map[string]UserAccount, error) {
	resultsIterator, err := ctx.GetStub().GetStateByRange("", "")
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	users := make(map[string]UserAccount)
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}

		key := queryResponse.Key
		// 只处理用户数据
		if len(key) > 5 && key[:5] == "user_" {
			var user UserAccount
			err = json.Unmarshal(queryResponse.Value, &user)
			if err != nil {
				continue
			}
			users[key] = user
		}
	}

	return users, nil
}

// BuyStock 用户买入股票
func (s *StockSmartContract) BuyStock(ctx contractapi.TransactionContextInterface, username string, stockID string, amount int, payment float64) error {
	stockKey := "stock_" + stockID
	stockJSON, err := ctx.GetStub().GetState(stockKey)
	if err != nil || stockJSON == nil {
		return fmt.Errorf("stock %s not found", stockID)
	}

	var stock StockToken
	json.Unmarshal(stockJSON, &stock)

	userKey := "user_" + username
	userJSON, err := ctx.GetStub().GetState(userKey)
	if err != nil || userJSON == nil {
		return fmt.Errorf("user %s not found", username)
	}

	var user UserAccount
	json.Unmarshal(userJSON, &user)

	// 确保 History 字段不为 nil
	if user.History == nil {
		user.History = []string{}
	}

	totalCost := stock.Price * float64(amount)
	if payment < totalCost {
		return fmt.Errorf("insufficient payment. Required: %.2f", totalCost)
	}

	// 更新用户持仓
	user.Stocks[stockID] += amount
	user.Balance -= totalCost
	user.History = append(user.History, fmt.Sprintf("Bought %d shares of %s for $%.2f", amount, stockID, totalCost))

	// 更新股票总流通量
	stock.Quantity -= amount

	// 写回状态
	userJSON, _ = json.Marshal(user)
	stockJSON, _ = json.Marshal(stock)

	err = ctx.GetStub().PutState(userKey, userJSON)
	if err != nil {
		return err
	}
	err = ctx.GetStub().PutState(stockKey, stockJSON)
	return err
}

// SellStock 用户卖出股票
func (s *StockSmartContract) SellStock(ctx contractapi.TransactionContextInterface, username string, stockID string, amount int) (float64, error) {
	stockKey := "stock_" + stockID
	stockJSON, err := ctx.GetStub().GetState(stockKey)
	if err != nil || stockJSON == nil {
		return 0, fmt.Errorf("stock %s not found", stockID)
	}

	var stock StockToken
	json.Unmarshal(stockJSON, &stock)

	userKey := "user_" + username
	userJSON, err := ctx.GetStub().GetState(userKey)
	if err != nil || userJSON == nil {
		return 0, fmt.Errorf("user %s not found", username)
	}

	var user UserAccount
	json.Unmarshal(userJSON, &user)

	// 确保 History 字段不为 nil
	if user.History == nil {
		user.History = []string{}
	}

	if user.Stocks[stockID] < amount {
		return 0, fmt.Errorf("insufficient shares to sell")
	}

	revenue := stock.Price * float64(amount)

	// 更新用户持仓
	user.Stocks[stockID] -= amount
	user.Balance += revenue
	user.History = append(user.History, fmt.Sprintf("Sold %d shares of %s for $%.2f", amount, stockID, revenue))

	// 更新股票总流通量
	stock.Quantity += amount

	// 写回状态
	userJSON, _ = json.Marshal(user)
	stockJSON, _ = json.Marshal(stock)

	err = ctx.GetStub().PutState(userKey, userJSON)
	if err != nil {
		return 0, err
	}
	err = ctx.GetStub().PutState(stockKey, stockJSON)

	return revenue, err
}

// GetStockPrice 查询当前股价
func (s *StockSmartContract) GetStockPrice(ctx contractapi.TransactionContextInterface, stockID string) (float64, error) {
	stockKey := "stock_" + stockID
	stockJSON, err := ctx.GetStub().GetState(stockKey)
	if err != nil || stockJSON == nil {
		return 0, fmt.Errorf("stock %s not found", stockID)
	}

	var stock StockToken
	json.Unmarshal(stockJSON, &stock)
	return stock.Price, nil
}

// GetUserStockCount 查询用户持有某股票的数量
func (s *StockSmartContract) GetUserStockCount(ctx contractapi.TransactionContextInterface, username string, stockID string) (int, error) {
	userKey := "user_" + username
	userJSON, err := ctx.GetStub().GetState(userKey)
	if err != nil || userJSON == nil {
		return 0, fmt.Errorf("user %s not found", username)
	}

	var user UserAccount
	json.Unmarshal(userJSON, &user)

	return user.Stocks[stockID], nil
}

// GetUserTotalValue 查询用户总资产（市值）
func (s *StockSmartContract) GetUserTotalValue(ctx contractapi.TransactionContextInterface, username string) (float64, error) {
	userKey := "user_" + username
	userJSON, err := ctx.GetStub().GetState(userKey)
	if err != nil || userJSON == nil {
		return 0, fmt.Errorf("user %s not found", username)
	}

	var user UserAccount
	json.Unmarshal(userJSON, &user)

	total := user.Balance
	for stockID, count := range user.Stocks {
		stockKey := "stock_" + stockID
		stockJSON, err := ctx.GetStub().GetState(stockKey)
		if err != nil || stockJSON == nil {
			continue
		}
		var stock StockToken
		json.Unmarshal(stockJSON, &stock)
		total += stock.Price * float64(count)
	}

	return total, nil
}

// CloseAccount 销户：删除用户的所有持仓和账户信息
func (s *StockSmartContract) CloseAccount(ctx contractapi.TransactionContextInterface, username string) error {
	userKey := "user_" + username
	err := ctx.GetStub().DelState(userKey)
	return err
}