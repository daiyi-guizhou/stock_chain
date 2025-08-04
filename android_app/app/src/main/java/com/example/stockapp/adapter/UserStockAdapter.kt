package com.example.stockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R
import com.example.stockapp.model.UserStock

class UserStockAdapter(private val userStocks: List<UserStock>) :
    RecyclerView.Adapter<UserStockAdapter.UserStockViewHolder>() {

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
        val userStock = userStocks[position]
        holder.stockIdTextView.text = userStock.stockId
        holder.quantityTextView.text = "数量: ${userStock.quantity}"
    }

    override fun getItemCount(): Int = userStocks.size
}