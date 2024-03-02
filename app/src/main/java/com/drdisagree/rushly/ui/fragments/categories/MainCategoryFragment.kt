package com.drdisagree.rushly.ui.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.FragmentMainCategoryBinding
import com.drdisagree.rushly.ui.adapters.BestDealsAdapter
import com.drdisagree.rushly.ui.adapters.BestProductsAdapter
import com.drdisagree.rushly.ui.adapters.SpecialProductsAdapter
import com.drdisagree.rushly.ui.viewmodels.MainCategoryViewModel
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.GridSpacingItemDecoration
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.showBottomNavView
import com.drdisagree.rushly.utils.toPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {

    private val tag = this.javaClass.simpleName
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        specialProductsAdapter.onClick = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT_COLLECTION, it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT_COLLECTION, it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT_COLLECTION, it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.specialProducts.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (viewModel.specialProductsPagingInfo.pageCount == 1L) {
                                    rvSpecialProducts.visibility = View.GONE
                                    progressbarSpecialProducts.visibility = View.GONE
                                    shimmerSpecialProducts.container.visibility = View.VISIBLE
                                    shimmerSpecialProducts.container.startShimmer()
                                } else {
                                    shimmerSpecialProducts.container.stopShimmer()
                                    shimmerSpecialProducts.container.visibility = View.GONE
                                    rvSpecialProducts.visibility = View.VISIBLE
                                    progressbarSpecialProducts.visibility = View.VISIBLE
                                }
                            }

                            is Resource.Success -> {
                                shimmerSpecialProducts.container.stopShimmer()
                                shimmerSpecialProducts.container.visibility = View.GONE
                                progressbarSpecialProducts.visibility = View.GONE
                                rvSpecialProducts.visibility = View.VISIBLE

                                specialProductsAdapter.differ.submitList(it.data)
                            }

                            is Resource.Error -> {
                                shimmerSpecialProducts.container.stopShimmer()
                                shimmerSpecialProducts.container.visibility = View.GONE
                                progressbarSpecialProducts.visibility = View.GONE
                                rvSpecialProducts.visibility = View.VISIBLE

                                Log.e(tag, it.message.toString())
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Unit
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.bestDeals.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (viewModel.bestDealsPagingInfo.pageCount == 1L) {
                                    rvBestDealsProducts.visibility = View.GONE
                                    progressbarBestDealsProducts.visibility = View.GONE
                                    shimmerBestDealsProducts.container.visibility = View.VISIBLE
                                    shimmerBestDealsProducts.container.startShimmer()
                                } else {
                                    shimmerBestDealsProducts.container.stopShimmer()
                                    shimmerBestDealsProducts.container.visibility = View.GONE
                                    rvBestDealsProducts.visibility = View.VISIBLE
                                    progressbarBestDealsProducts.visibility = View.VISIBLE
                                }
                            }

                            is Resource.Success -> {
                                shimmerBestDealsProducts.container.stopShimmer()
                                shimmerBestDealsProducts.container.visibility = View.GONE
                                progressbarBestDealsProducts.visibility = View.GONE
                                rvBestDealsProducts.visibility = View.VISIBLE

                                bestDealsAdapter.differ.submitList(it.data)
                            }

                            is Resource.Error -> {
                                shimmerBestDealsProducts.container.stopShimmer()
                                shimmerBestDealsProducts.container.visibility = View.GONE
                                progressbarBestDealsProducts.visibility = View.GONE
                                rvBestDealsProducts.visibility = View.VISIBLE

                                Log.e(tag, it.message.toString())
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Unit
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.bestProducts.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (viewModel.bestProductsPagingInfo.pageCount == 1L) {
                                    rvBestProducts.visibility = View.GONE
                                    progressbarBestProducts.visibility = View.GONE
                                    shimmerBestProducts.container.visibility = View.VISIBLE
                                    shimmerBestProducts.container.startShimmer()
                                } else {
                                    shimmerBestProducts.container.stopShimmer()
                                    shimmerBestProducts.container.visibility = View.GONE
                                    rvBestProducts.visibility = View.VISIBLE
                                    progressbarBestProducts.visibility = View.VISIBLE
                                }
                            }

                            is Resource.Success -> {
                                shimmerBestProducts.container.stopShimmer()
                                shimmerBestProducts.container.visibility = View.GONE
                                progressbarBestProducts.visibility = View.GONE
                                rvBestProducts.visibility = View.VISIBLE

                                bestProductsAdapter.differ.submitList(it.data)
                            }

                            is Resource.Error -> {
                                shimmerBestProducts.container.stopShimmer()
                                shimmerBestProducts.container.visibility = View.GONE
                                progressbarBestProducts.visibility = View.GONE
                                rvBestProducts.visibility = View.VISIBLE

                                Log.e(tag, it.message.toString())
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Unit
                            }
                        }
                    }
                }
            }

            rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                        viewModel.fetchSpecialProducts()
                    }
                }
            })

            rvBestDealsProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                        viewModel.fetchBestDealsProducts()
                    }
                }
            })

            nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { recyclerView, _, scrollY, _, _ ->
                if (recyclerView.height + scrollY >= recyclerView.getChildAt(0).height) {
                    viewModel.fetchBestProducts()
                }
            })
        }
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = specialProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = bestDealsAdapter
        }
    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = bestProductsAdapter
            addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    requireContext().toPx(20).toInt(),
                    false
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavView()

        binding.apply {
            if (shimmerSpecialProducts.container.visibility == View.VISIBLE) {
                shimmerSpecialProducts.container.startShimmer()
            }
            if (shimmerBestDealsProducts.container.visibility == View.VISIBLE) {
                shimmerBestDealsProducts.container.startShimmer()
            }
            if (shimmerBestProducts.container.visibility == View.VISIBLE) {
                shimmerBestProducts.container.startShimmer()
            }
        }
    }

    override fun onPause() {
        binding.apply {
            if (shimmerSpecialProducts.container.visibility == View.VISIBLE) {
                shimmerSpecialProducts.container.stopShimmer()
            }
            if (shimmerBestDealsProducts.container.visibility == View.VISIBLE) {
                shimmerBestDealsProducts.container.stopShimmer()
            }
            if (shimmerBestProducts.container.visibility == View.VISIBLE) {
                shimmerBestProducts.container.stopShimmer()
            }
        }

        super.onPause()
    }
}