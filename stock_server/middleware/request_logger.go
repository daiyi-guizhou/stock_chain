
package middleware

import (
	"bytes"
	"encoding/json"
	"io"
	"log"
	"time"

	"github.com/gin-gonic/gin"
)

type RequestLog struct {
	Timestamp time.Time `json:"timestamp"`
	Method    string    `json:"method"`
	URL       string    `json:"url"`
	IP        string    `json:"ip"`
	Params    string    `json:"params"`
	Data      string    `json:"data"`
	UserAgent string    `json:"user_agent"`
}

func RequestLogger() gin.HandlerFunc {
	return func(c *gin.Context) {
		// 记录请求开始时间
		startTime := time.Now()

		// 读取请求体
		var bodyBytes []byte
		if c.Request.Body != nil {
			bodyBytes, _ = io.ReadAll(c.Request.Body)
		}

		// 将原始请求体重新写回，以便后续处理
		c.Request.Body = io.NopCloser(bytes.NewBuffer(bodyBytes))

		// 处理请求
		c.Next()

		// 记录请求信息
		requestLog := RequestLog{
			Timestamp: startTime,
			Method:    c.Request.Method,
			URL:       c.Request.URL.Path,
			IP:        c.ClientIP(),
			Params:    c.Request.URL.RawQuery,
			Data:      string(bodyBytes),
			UserAgent: c.Request.UserAgent(),
		}

		// 将日志转换为 JSON 格式并输出
		logJSON, err := json.Marshal(requestLog)
		if err != nil {
			log.Printf("Failed to marshal request log: %v", err)
		} else {
			log.Printf("[REQUEST] %s", string(logJSON))
		}
	}
}