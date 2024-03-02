package com.drdisagree.rushly.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.drdisagree.rushly.databinding.ViewpagerImageItemBinding
import com.drdisagree.rushly.utils.toPx

class ViewPagerImagesAdapter :
    RecyclerView.Adapter<ViewPagerImagesAdapter.ViewPagerImagesViewHolder>() {

    inner class ViewPagerImagesViewHolder(val binding: ViewpagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            val context = itemView.context
            val drawable = CircularProgressDrawable(context);
            drawable.strokeWidth = context.toPx(5)
            drawable.centerRadius = context.toPx(40)
            drawable.start()

            Glide.with(itemView)
                .load(imagePath)
                .placeholder(drawable)
                .fitCenter()
                .into(binding.imageProductDetails)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerImagesViewHolder {
        return ViewPagerImagesViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}