package com.example.stockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapp.R 
import com.example.stockapp.model.UserInfo
// 说明：
// android.view.LayoutInflater：用于将 XML 布局文件转换为 View 对象。
// android.view.View 和 android.view.ViewGroup：UI 组件的基础类。
// android.widget.TextView：用于显示文本的控件。
// androidx.recyclerview.widget.RecyclerView：用于显示可滚动的列表。
// R：资源类，用于访问布局、字符串、图片等资源。
// UserInfo：自定义数据模型类，表示用户信息


class UserAdapter(private val users: Map<String, UserInfo>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    // 作用：定义一个 UserAdapter 类，继承自 RecyclerView.Adapter。
    // 说明：
        // UserAdapter 是一个适配器，用于将 users 数据绑定到 RecyclerView 中的每一项。
        // 构造函数接收一个 Map<String, UserInfo> 类型的参数 users，其中：
            // String 是键（通常是用户名或用户 ID）。
            // UserInfo 是值（用户的具体信息，如姓名、余额等）。
        // RecyclerView.Adapter<UserAdapter.UserViewHolder> 表示该适配器使用 UserViewHolder 作为 ViewHolder。

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val balanceTextView: TextView = itemView.findViewById(R.id.balanceTextView)
        // 移除了 stocksTextView 的引用，因为布局文件中没有这个元素
    }
    // RecyclerView 需要新的 ViewHolder 来显示数据（通常是首次显示或滚动时） 
    // 作用：定义 UserViewHolder，用于缓存 RecyclerView 中每一项的视图组件。
    // 说明：
    //     UserViewHolder 继承自 RecyclerView.ViewHolder，是 RecyclerView 必须使用的类。
    //     itemView: View 是 RecyclerView 每一项的布局视图。
    //     nameTextView 和 balanceTextView 是布局中的 TextView 控件，通过 findViewById 获取。
    //     R.id.nameTextView 和 R.id.balanceTextView 是在 item_user.xml 中定义的控件 ID。 item_user 是在 下面 onCreateViewHolder 中定义的。

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        // LayoutInflater.from(parent.context).inflate(...)：将 item_user.xml 布局文件转换为 View 对象
        // parent：父容器，通常是 RecyclerView。
        // false：表示不立即添加到父容器，因为 RecyclerView 会在稍后调用 addView 方法添加。

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users.values.toList()[position]
        holder.nameTextView.text = user.name
        holder.balanceTextView.text = "Balance: $${String.format("%.2f", user.balance)}"
        // 移除了 stocksTextView 的设置
    }
        // 作用：将数据绑定到 RecyclerView 的每一项。
        // 说明：
        //     holder: UserViewHolder：表示当前项的 ViewHolder，包含视图组件。
        //     position: Int：表示当前项的位置（索引）。
        //     val user = users.values.toList()[position]：将 users Map 转换为 List，然后通过 position 获取当前用户。
        //     holder.nameTextView.text = user.name：将用户的 name 显示在 nameTextView 中。
        //     holder.balanceTextView.text = "Balance: $${String.format("%.2f", user.balance)}"：
        //     显示用户的 balance，格式化为两位小数，并加上美元符号

    override fun getItemCount(): Int {
        return users.size
    }
}

// 四、总结
// 阶段	涉及文件	作用
// UI 定义	activity_main.xml	定义主界面布局，包含 RecyclerView
// 数据绑定	UserAdapter.kt	将用户数据绑定到 RecyclerView 每一项
// 数据展示	item_user.xml	定义每项用户的 UI 结构
// 数据加载	MainActivity.kt	加载数据并更新 RecyclerView
// 数据结构	UserInfo.kt	数据模型类，表示用户信息

// 整个流程是典型的 Android 数据驱动 UI 的实现方式，结合了布局文件、适配器、数据模型和主 Activity 的协调工作。



// 一、Adapter 生命周期概述
// RecyclerView.Adapter 的生命周期主要围绕以下几个核心方法展开：

// 方法	调用时机	作用
// 构造函数	创建 Adapter 时	初始化数据源
// onCreateViewHolder	需要新 ViewHolder 时	创建 ViewHolder（视图）
// onBindViewHolder	需要更新 ViewHolder 内容时	绑定数据到 ViewHolder
// getItemCount	初始化和数据更新时	返回数据项总数
// notifyDataSetChanged() 等	数据更新时	通知 RecyclerView 刷新
// onViewAttachedToWindow	ViewHolder 被添加到窗口时	可选：处理视图附加事件
// onViewDetachedFromWindow	ViewHolder 从窗口移除时	可选：处理视图移除事件
// onViewRecycled	ViewHolder 被回收时	可选：释放资源