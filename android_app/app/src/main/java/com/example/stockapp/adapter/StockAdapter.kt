package com.example.stockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R
import com.example.stockapp.model.StockInfo

class StockAdapter(private val stocks: Map<String, StockInfo>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val symbolTextView: TextView = itemView.findViewById(R.id.symbolTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stocks.values.toList()[position]
        holder.symbolTextView.text = stock.symbol
        holder.priceTextView.text = "Price: $${String.format("%.2f", stock.price)}"
        holder.quantityTextView.text = "Quantity: ${stock.quantity}"
    }

    override fun getItemCount(): Int {
        return stocks.size
    }
}