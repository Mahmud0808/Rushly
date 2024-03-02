package com.drdisagree.rushly.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.Order
import com.drdisagree.rushly.data.OrderStatus
import com.drdisagree.rushly.data.getOrderStatus
import com.drdisagree.rushly.databinding.RvItemOrderBinding

class OrderListAdapter : RecyclerView.Adapter<OrderListAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: RvItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                val status =
                    "Status: ${if (order.orderStatus == "") "Unknown" else order.orderStatus}"
                tvOrderStatus.text = status
                tvOrderDate.text = order.date
                imageOrderState.setImageDrawable(
                    when (getOrderStatus(order.orderStatus)) {
                        is OrderStatus.Pending -> {
                            ColorDrawable(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.g_blue_gray200
                                )
                            )
                        }

                        is OrderStatus.Confirmed -> {
                            ColorDrawable(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.g_orange_yellow
                                )
                            )
                        }

                        is OrderStatus.Delivered -> {
                            ColorDrawable(ContextCompat.getColor(itemView.context, R.color.g_green))
                        }

                        is OrderStatus.Shipped -> {
                            ColorDrawable(ContextCompat.getColor(itemView.context, R.color.g_green))
                        }

                        is OrderStatus.Canceled -> {
                            ColorDrawable(ContextCompat.getColor(itemView.context, R.color.g_red))
                        }

                        is OrderStatus.Returned -> {
                            ColorDrawable(ContextCompat.getColor(itemView.context, R.color.g_red))
                        }
                    }
                )
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            RvItemOrderBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Order) -> Unit)? = null
}