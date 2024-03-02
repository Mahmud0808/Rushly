package com.drdisagree.rushly.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.databinding.RvItemProductBinding
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency

class BestProductsAdapter :
    RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(
        private val binding: RvItemProductBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvProductName.text = product.name

                if (product.offerPercentage == null) {
                    tvProductOldPrice.visibility = View.GONE
                    tvProductOldPrice.paintFlags = 0
                } else {
                    tvProductOldPrice.visibility = View.VISIBLE
                    val oldPrice = "$${product.price}"
                    tvProductOldPrice.text = oldPrice
                    tvProductOldPrice.paintFlags =
                        tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                tvProductNewPrice.text = getPriceCalculatingOfferAsCurrency(
                    product.price,
                    product.offerPercentage
                )
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            RvItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null
}