package com.example.stockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R 
import com.example.stockapp.ui.UserInfo
import java.util.*

class UserAdapter(private val users: Map<String, UserInfo>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val balanceTextView: TextView = itemView.findViewById(R.id.balanceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users.values.toList()[position]
        holder.nameTextView.text = user.name
        holder.balanceTextView.text = "Balance: ${user.balance}"
    }

    override fun getItemCount(): Int {
        return users.size
    }
}