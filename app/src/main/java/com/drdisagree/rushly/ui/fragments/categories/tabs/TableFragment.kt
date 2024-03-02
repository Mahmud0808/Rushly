package com.drdisagree.rushly.ui.fragments.categories.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drdisagree.rushly.data.Category
import com.drdisagree.rushly.ui.viewmodels.CategoryViewModel
import com.drdisagree.rushly.ui.viewmodels.factory.BaseCategoryViewModelFactory
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TableFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Table)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showOfferProductsLoading(viewModel)
                        }

                        is Resource.Success -> {
                            hideOfferProductsLoading()
                            offerAdapter.differ.submitList(it.data)
                        }

                        is Resource.Error -> {
                            hideOfferProductsLoading()
                            Toast.makeText(
                                requireContext(),
                                it.message,
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showBestProductsLoading(viewModel)
                        }

                        is Resource.Success -> {
                            hideBestProductsLoading()
                            bestProductsAdapter.differ.submitList(it.data)
                        }

                        is Resource.Error -> {
                            hideBestProductsLoading()
                            Toast.makeText(
                                requireContext(),
                                it.message,
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

    override fun onBestProductsPagingRequest() {
        viewModel.fetchBestProducts()
    }

    override fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()
    }
}