package com.drdisagree.rushly.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drdisagree.rushly.data.CartProduct
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.databinding.RvItemBillingProductBinding
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency

class BillingProductAdapter :
    RecyclerView.Adapter<BillingProductAdapter.BillingProductViewHolder>() {

    inner class BillingProductViewHolder(val binding: RvItemBillingProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                val quantity = "x${billingProduct.quantity}"
                tvBillingProductQuantity.text = quantity
                tvProductCartPrice.text = getPriceCalculatingOfferAsCurrency(
                    billingProduct.product.price,
                    billingProduct.product.offerPercentage
                )
                if (billingProduct.selectedColor != null) {
                    imageCartProductColor.setImageDrawable(
                        ColorDrawable(billingProduct.selectedColor)
                    )
                    imageCartProductColor.visibility = ViewGroup.VISIBLE
                } else {
                    imageCartProductColor.visibility = ViewGroup.GONE
                }
                if (billingProduct.selectedSize != null) {
                    tvCartProductSize.text = billingProduct.selectedSize
                    tvCartProductSize.visibility = ViewGroup.VISIBLE
                    imageCartProductSize.visibility = ViewGroup.VISIBLE
                } else {
                    tvCartProductSize.visibility = ViewGroup.GONE
                    imageCartProductSize.visibility = ViewGroup.GONE
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
        return BillingProductViewHolder(
            RvItemBillingProductBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(billingProduct.product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((Product) -> Unit)? = null
}