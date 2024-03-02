package com.drdisagree.rushly.ui.fragments.shopping

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.FragmentSearchBinding
import com.drdisagree.rushly.ui.adapters.BestProductsAdapter
import com.drdisagree.rushly.ui.viewmodels.SearchViewModel
import com.drdisagree.rushly.utils.Constants
import com.drdisagree.rushly.utils.GridSpacingItemDecoration
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.toPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private val productsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchRv()

        binding.apply {
            productsAdapter.onClick = {
                val bundle = Bundle().apply {
                    putParcelable(Constants.PRODUCT_COLLECTION, it)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_productDetailsFragment,
                    bundle
                )
            }

            edSearchBox.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.fetchProducts(s.toString().trim())

                    if (s.toString().trim().isNotEmpty()) {
                        showCloseIcon()
                    } else {
                        showSearchIcon()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })

            ivSearchNow.setOnClickListener {
                if (edSearchBox.text.toString().trim().isNotEmpty()) {
                    viewModel.fetchProducts(edSearchBox.text.toString().trim())
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.allProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                shimmerProducts.container.startShimmer()
                                rvProducts.visibility = View.GONE
                                shimmerProducts.container.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                shimmerProducts.container.stopShimmer()
                                shimmerProducts.container.visibility = View.GONE
                                rvProducts.visibility = View.VISIBLE

                                if (it.data!!.isEmpty()) {
                                    showEmptyResult()
                                } else {
                                    hideEmptyResult()
                                    productsAdapter.differ.submitList(it.data)
                                }
                            }

                            is Resource.Error -> {
                                shimmerProducts.container.stopShimmer()
                                shimmerProducts.container.visibility = View.GONE
                                rvProducts.visibility = View.VISIBLE
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
    }

    private fun showEmptyResult() {
        binding.apply {
            tvEmptySearch.visibility = View.VISIBLE
            rvProducts.visibility = View.GONE
        }
    }

    private fun hideEmptyResult() {
        binding.apply {
            tvEmptySearch.visibility = View.GONE
            rvProducts.visibility = View.VISIBLE
        }
    }

    private fun setupSearchRv() {
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = productsAdapter
            addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    requireContext().toPx(20).toInt(),
                    false
                )
            )
        }
    }

    private fun showCloseIcon() {
        binding.apply {
            ivSearchNow.setImageResource(R.drawable.ic_close)
            ivSearchNow.setOnClickListener {
                edSearchBox.setText("")
                edSearchBox.clearFocus()
            }
        }
    }

    private fun showSearchIcon() {
        binding.apply {
            ivSearchNow.setImageResource(R.drawable.ic_search)
            ivSearchNow.setOnClickListener {
                if (edSearchBox.text.toString().trim().isNotEmpty()) {
                    viewModel.fetchProducts(edSearchBox.text.toString().trim())
                }
            }
        }
    }
}