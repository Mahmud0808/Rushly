package com.drdisagree.rushly.ui.fragments.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.CartProduct
import com.drdisagree.rushly.databinding.FragmentProductDetailsBinding
import com.drdisagree.rushly.ui.adapters.ColorsAdapter
import com.drdisagree.rushly.ui.adapters.SizesAdapter
import com.drdisagree.rushly.ui.adapters.ViewPagerImagesAdapter
import com.drdisagree.rushly.ui.viewmodels.DetailsViewModel
import com.drdisagree.rushly.utils.Resource
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private val imageAdapter by lazy { ViewPagerImagesAdapter() }
    private val sizeAdapter by lazy { SizesAdapter() }
    private val colorAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        sizeAdapter.onItemClick = {
            selectedSize = it
        }

        colorAdapter.onItemClick = {
            selectedColor = it
        }

        binding.apply {
            tvProductName.text = product.name

            if (product.offerPercentage == null) {
                val newPrice = "$${product.price}"
                tvProductNewPrice.text = newPrice
                tvProductOldPrice.visibility = View.GONE
            }

            product.offerPercentage?.let {
                val remainingPricePercentage = 100 - it
                val priceAfterOffer = product.price * remainingPricePercentage / 100
                val priceConcatenated = "$${String.format("%.2f", priceAfterOffer)}"
                tvProductNewPrice.text = priceConcatenated
                val price = "$${product.price}"
                tvProductOldPrice.text = price
                tvProductOldPrice.visibility = View.VISIBLE
                tvProductOldPrice.paintFlags =
                    tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            tvProductDescription.text = product.description

            imageClose.setOnClickListener {
                findNavController().navigateUp()
            }

            if (product.colors.isNullOrEmpty()) {
                linearColors.visibility = View.GONE
            }

            if (product.sizes.isNullOrEmpty()) {
                linearSizes.visibility = View.GONE
            }

            if (linearColors.visibility == View.GONE && linearSizes.visibility == View.GONE) {
                linearProductPref.visibility = View.GONE
            }

            buttonAddToCart.setOnClickListener {
                if (selectedColor == null && !product.colors.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select a color",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (selectedSize == null && !product.sizes.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select a size",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.addOrUpdateProductInCart(
                    CartProduct(
                        product,
                        1,
                        selectedColor,
                        selectedSize
                    )
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.addToCart.collect {
                        when (it) {
                            is Resource.Loading -> {
                                buttonAddToCart.startAnimation()
                            }

                            is Resource.Success -> {
                                buttonAddToCart.revertAnimation()
                                buttonAddToCart.setBackgroundDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.black_background
                                    )
                                )
                            }

                            is Resource.Error -> {
                                buttonAddToCart.revertAnimation()
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Unit
                            }
                        }
                    }
                }
            }
        }

        imageAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizeAdapter.differ.submitList(it)
        }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = imageAdapter

            TabLayoutMediator(
                viewPagerIndicator,
                viewPagerProductImages
            ) { _, _ -> }.attach()
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizeAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}