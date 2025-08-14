package com.example.stockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R
import com.example.stockapp.model.UserStocksResponse

// UserStocksResponse
// {
//     "AAPL": 50,
//     "TSLA": 100
// }
class UserStockAdapter(private val userStocksResp: UserStocksResponse) :
    RecyclerView.Adapter<UserStockAdapter.UserStockViewHolder>() {

    // 将 Map 转换为 List 以便于处理
    // private val stockEntries = userStocksResp.toList()
    // private val stockEntries = userStocksResp.entries.toList()
    private val stockEntries = userStocksResp.stocks.entries.toList()

    class UserStockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockIdTextView: TextView = itemView.findViewById(R.id.stockIdTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_stock, parent, false)
        return UserStockViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserStockViewHolder, position: Int) {
        // val userStock = userStocksResp[position]
        // holder.stockIdTextView.text = userStock.stockId
        // holder.quantityTextView.text = "数量: ${userStock.quantity}"

        // // 从转换后的列表中获取数据
        // val (stockId, quantity) = stockEntries[position]
        // holder.stockIdTextView.text = stockId
        // holder.quantityTextView.text = "数量: $quantity"
        
        val entry = stockEntries[position]
        holder.stockIdTextView.text = entry.key
        holder.quantityTextView.text = "数量: ${entry.value}"
    }

    override fun getItemCount(): Int = stockEntries.size
}