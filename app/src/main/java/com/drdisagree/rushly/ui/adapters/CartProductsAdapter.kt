package com.drdisagree.rushly.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drdisagree.rushly.data.CartProduct
import com.drdisagree.rushly.databinding.RvItemCartProductBinding
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency

class CartProductsAdapter :
    RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder(
        val binding: RvItemCartProductBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()
                tvProductCartPrice.text = getPriceCalculatingOfferAsCurrency(
                    cartProduct.product.price,
                    cartProduct.product.offerPercentage
                )
                if (cartProduct.selectedColor != null) {
                    imageCartProductColor.setImageDrawable(
                        ColorDrawable(cartProduct.selectedColor)
                    )
                    imageCartProductColor.visibility = ViewGroup.VISIBLE
                } else {
                    imageCartProductColor.visibility = ViewGroup.GONE
                }
                if (cartProduct.selectedSize != null) {
                    tvCartProductSize.text = cartProduct.selectedSize
                    tvCartProductSize.visibility = ViewGroup.VISIBLE
                    imageCartProductSize.visibility = ViewGroup.VISIBLE
                } else {
                    tvCartProductSize.visibility = ViewGroup.GONE
                    imageCartProductSize.visibility = ViewGroup.GONE
                }
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            RvItemCartProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null
}