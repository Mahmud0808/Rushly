package com.drdisagree.rushly.ui.fragments.settings

import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.Order
import com.drdisagree.rushly.data.OrderStatus
import com.drdisagree.rushly.data.getOrderStatus
import com.drdisagree.rushly.databinding.FragmentOrderDetailsBinding
import com.drdisagree.rushly.ui.adapters.BillingProductAdapter
import com.drdisagree.rushly.ui.viewmodels.OrderViewModel
import com.drdisagree.rushly.ui.viewmodels.UserAccountViewModel
import com.drdisagree.rushly.utils.HorizontalItemDecoration
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.getPriceCalculatingOfferAsCurrency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val userAccountViewModel by viewModels<UserAccountViewModel>()
    private var order = Order()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        order = args.order
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderRv()

        billingProductAdapter.onProductClick = {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(
                R.id.action_orderDetailsFragment_to_productDetailsFragment,
                bundle
            )
        }

        binding.apply {
            val id = "#${order.orderId}"
            tvOrderId.text = id

            val orderStates = mutableListOf(
                OrderStatus.Pending.status,
                OrderStatus.Confirmed.status,
                OrderStatus.Shipped.status,
                OrderStatus.Delivered.status
            )
            stepView.setSteps(orderStates)

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Pending -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderState, false)
            stepView.done(stepView.currentStep == stepView.stepCount - 1)

            tvFullName.text = order.address.label
            val address =
                "${order.address.address}, ${order.address.street}, ${order.address.city}, ${order.address.state}"
            tvAddress.text = address
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = getPriceCalculatingOfferAsCurrency(order.totalPrice, 0f)

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    userAccountViewModel.isAdmin.collect {
                        when (it) {
                            is Resource.Loading -> {
                                stepView.setOnStepClickListener { }
                            }

                            is Resource.Success -> {
                                if (it.data == true) {
                                    stepView.setOnStepClickListener { step ->
                                        orderViewModel.updateOrderStatus(
                                            order.copy(
                                                orderStatus = orderStates[step]
                                            )
                                        )
                                        stepView.go(step, true)
                                        stepView.done(stepView.currentStep == stepView.stepCount - 1)
                                    }
                                } else {
                                    stepView.setOnStepClickListener { }
                                }
                            }

                            is Resource.Error -> {
                                stepView.setOnStepClickListener { }
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
                    orderViewModel.order.collect {
                        when (it) {
                            is Resource.Loading -> {
                                progressbarOrderDetails.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                progressbarOrderDetails.visibility = View.INVISIBLE
                            }

                            is Resource.Error -> {
                                progressbarOrderDetails.visibility = View.INVISIBLE
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

        billingProductAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}