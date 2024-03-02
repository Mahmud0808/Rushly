package com.drdisagree.rushly.ui.fragments.categories.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.FragmentBaseCategoryBinding
import com.drdisagree.rushly.ui.adapters.BestProductsAdapter
import com.drdisagree.rushly.ui.viewmodels.CategoryViewModel
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {

    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()

        offerAdapter.onClick = {
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
            rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (offerAdapter.differ.currentList.isNotEmpty() &&
                        !recyclerView.canScrollHorizontally(1) &&
                        dx != 0
                    ) {
                        onOfferPagingRequest()
                    }
                }
            })

            nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { recyclerView, _, scrollY, _, _ ->
                if (bestProductsAdapter.differ.currentList.isNotEmpty() &&
                    recyclerView.height + scrollY >= recyclerView.getChildAt(0).height
                ) {
                    onBestProductsPagingRequest()
                }
            })
        }
    }

    fun showOfferProductsLoading(viewModel: CategoryViewModel) {
        binding.apply {
            if (viewModel.offerProductsPagingInfo.pageCount == 1L) {
                rvOfferProducts.visibility = View.GONE
                progressbarOfferProducts.visibility = View.GONE
                shimmerOfferProducts.container.visibility = View.VISIBLE
                shimmerOfferProducts.container.startShimmer()
            } else {
                shimmerOfferProducts.container.stopShimmer()
                shimmerOfferProducts.container.visibility = View.GONE
                rvOfferProducts.visibility = View.VISIBLE
                progressbarOfferProducts.visibility = View.VISIBLE
            }
        }
    }

    fun showBestProductsLoading(viewModel: CategoryViewModel) {
        binding.apply {
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
    }

    fun hideOfferProductsLoading() {
        binding.apply {
            shimmerOfferProducts.container.stopShimmer()
            shimmerOfferProducts.container.visibility = View.GONE
            progressbarOfferProducts.visibility = View.GONE
            rvOfferProducts.visibility = View.VISIBLE
        }
    }

    fun hideBestProductsLoading() {
        binding.apply {
            shimmerBestProducts.container.stopShimmer()
            shimmerBestProducts.container.visibility = View.GONE
            progressbarBestProducts.visibility = View.GONE
            rvBestProducts.visibility = View.VISIBLE
        }
    }

    open fun onOfferPagingRequest() {

    }

    open fun onBestProductsPagingRequest() {

    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = offerAdapter
        }
    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = bestProductsAdapter
        }
    }

//    override fun onResume() {
//        super.onResume()
//
//        showBottomNavView()
//
//        binding.apply {
//            if (shimmerOfferProducts.container.visibility == View.VISIBLE) {
//                shimmerOfferProducts.container.startShimmer()
//            }
//            if (shimmerBestProducts.container.visibility == View.VISIBLE) {
//                shimmerBestProducts.container.startShimmer()
//            }
//        }
//    }
//
//    override fun onPause() {
//        binding.apply {
//            if (shimmerOfferProducts.container.visibility == View.VISIBLE) {
//                shimmerOfferProducts.container.stopShimmer()
//            }
//            if (shimmerBestProducts.container.visibility == View.VISIBLE) {
//                shimmerBestProducts.container.stopShimmer()
//            }
//        }
//
//        super.onPause()
//    }
}