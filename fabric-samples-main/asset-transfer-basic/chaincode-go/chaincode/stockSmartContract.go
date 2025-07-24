package chaincode

import (
	"encoding/json"
	"fmt"

	"github.com/hyperledger/fabric-contract-api-go/v2/contractapi"
)

// StockToken 表示特斯拉股票代币的基本信息 
type StockToken struct {
	Symbol   string  `json:"symbol"`   // 股票代码 (TSLA)
	Price    float64 `json:"price"`    // 当前股价
	Quantity int     `json:"quantity"` // 持有数量
}

// UserAccount 表示一个用户的账户信息
type UserAccount struct {
	Name     string             `json:"name"`     // 用户名
	Stocks   map[string]int     `json:"stocks"`   // 持有的股票代币: key=stockID, value=数量
	Balance  float64            `json:"balance"`  // 可用余额
	History  []string           `json:"history"`  // 交易历史
}

// StockSmartContract 实现股票代币化逻辑
type StockSmartContract struct {
	contractapi.Contract
}

// InitLedger 初始化账本，发行特斯拉股票并设置默认价格
func (s *StockSmartContract) InitLedger(ctx contractapi.TransactionContextInterface) error {
	stock := StockToken{
		Symbol:   "TSLA",
		Price:    180.5,
		Quantity: 1000000, // 初始供应量
	}
	stockJSON, _ := json.Marshal(stock)
	err := ctx.GetStub().PutState("TSLA", stockJSON)
	if err != nil {
		return fmt.Errorf("failed to put initial Tesla stock into ledger: %v", err)
	}

	// 初始化几个测试用户
	users := []UserAccount{
		{Name: "Alice", Stocks: make(map[string]int)},
		{Name: "Bob", Stocks: make(map[string]int)},
	}

	for _, user := range users {
		userJSON, _ := json.Marshal(user)
		err = ctx.GetStub().PutState("user_"+user.Name, userJSON)
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
        if key == "TSLA" {
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
        // 只处理股票数据（目前只有TSLA，但可以扩展）
        if key == "TSLA" {
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
	stockJSON, err := ctx.GetStub().GetState(stockID)
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
	err = ctx.GetStub().PutState(stockID, stockJSON)
	return err
}

// SellStock 用户卖出股票
func (s *StockSmartContract) SellStock(ctx contractapi.TransactionContextInterface, username string, stockID string, amount int) (float64, error) {
	stockJSON, err := ctx.GetStub().GetState(stockID)
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
	err = ctx.GetStub().PutState(stockID, stockJSON)

	return revenue, err
}

// GetStockPrice 查询当前股价
func (s *StockSmartContract) GetStockPrice(ctx contractapi.TransactionContextInterface, stockID string) (float64, error) {
	stockJSON, err := ctx.GetStub().GetState(stockID)
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
		stockJSON, err := ctx.GetStub().GetState(stockID)
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