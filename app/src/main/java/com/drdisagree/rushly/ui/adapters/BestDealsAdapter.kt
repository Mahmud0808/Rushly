package com.drdisagree.rushly.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.databinding.RvItemBestDealsBinding
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency

class BestDealsAdapter :
    RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(
        private val binding: RvItemBestDealsBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDealRvItem)
                tvBestDealProductName.text = product.name
                val oldPrice = "$${product.price}"
                tvBestDealOldPrice.text = oldPrice
                tvBestDealNewPrice.text = getPriceCalculatingOfferAsCurrency(
                    product.price,
                    product.offerPercentage
                )
                tvBestDealOldPrice.paintFlags =
                    tvBestDealOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            RvItemBestDealsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
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