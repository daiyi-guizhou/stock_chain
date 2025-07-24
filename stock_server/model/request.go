package model

type BuyStockRequest struct {
	Username string  `json:"username"`
	StockID  string  `json:"stock_id"`
	Amount   int     `json:"amount"`
	Payment  float64 `json:"payment"`
}

type SellStockRequest struct {
	Username string `json:"username"`
	StockID  string `json:"stock_id"`
	Amount   int    `json:"amount"`
}