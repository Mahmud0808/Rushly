package com.drdisagree.rushly.ui.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.FragmentCartBinding
import com.drdisagree.rushly.firebase.FirebaseCommon
import com.drdisagree.rushly.ui.adapters.CartProductsAdapter
import com.drdisagree.rushly.ui.dialogs.showAlertDialog
import com.drdisagree.rushly.ui.viewmodels.CartViewModel
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.VerticalItemDecoration
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        var totalPrice = 0f

        cartAdapter.onProductClick = {
            val bundle = Bundle().apply {
                putParcelable(PRODUCT_COLLECTION, it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.apply {
            buttonCheckout.setOnClickListener {
                val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                    totalPrice,
                    cartAdapter.differ.currentList.toTypedArray(),
                    true
                )
                findNavController().navigate(action)
            }

            imageCloseCart.setOnClickListener {
                findNavController().navigateUp()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.cartProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                progressbarCart.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                progressbarCart.visibility = View.INVISIBLE

                                if (it.data!!.isEmpty()) {
                                    showEmptyCart()
                                } else {
                                    hideEmptyCart()
                                    cartAdapter.differ.submitList(it.data)
                                }
                            }

                            is Resource.Error -> {
                                progressbarCart.visibility = View.INVISIBLE
                                showEmptyCart()
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

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.productsPrice.collectLatest { price ->
                        price?.let {
                            totalPrice = it
                            tvTotalPrice.text = getPriceCalculatingOfferAsCurrency(it, 0f)
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.deleteDialog.collectLatest {
                        showAlertDialog(
                            requireContext(),
                            "Delete item from cart?",
                            "This item will be deleted from your cart.",
                            "Yes",
                            onPositiveClick = {
                                viewModel.deleteCartProduct(it)
                            },
                            "Cancel",
                            onNegativeClick = {}
                        )
                    }
                }
            }
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}