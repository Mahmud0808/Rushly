package com.drdisagree.rushly.ui.fragments.shopping

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
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.Address
import com.drdisagree.rushly.databinding.FragmentAddressBinding
import com.drdisagree.rushly.ui.viewmodels.AddressViewModel
import com.drdisagree.rushly.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()
    private val args by navArgs<AddressFragmentArgs>()
    private var address: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        address = args.address
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (address == null) {
                buttonDelete.text = getString(R.string.cancel)

                buttonDelete.setOnClickListener {
                    findNavController().navigateUp()
                }
            } else {
                edLabel.setText(address?.label)
                edAddress.setText(address?.address)
                edStreet.setText(address?.street)
                edCity.setText(address?.city)
                edState.setText(address?.state)
                edPhone.setText(address?.phone)

                buttonDelete.setOnClickListener {
                    viewModel.deleteAddress(address!!)
                }
            }

            buttonSave.setOnClickListener {
                viewModel.addAddress(
                    Address(
                        label = edLabel.text.toString(),
                        address = edAddress.text.toString(),
                        street = edStreet.text.toString(),
                        city = edCity.text.toString(),
                        state = edState.text.toString(),
                        phone = edPhone.text.toString()
                    )
                )
            }

            imageAddressClose.setOnClickListener {
                findNavController().navigateUp()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.address.collect {
                        when (it) {
                            is Resource.Loading -> {
                                progressbarAddress.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                progressbarAddress.visibility = View.INVISIBLE
                                findNavController().navigateUp()
                            }

                            is Resource.Error -> {
                                progressbarAddress.visibility = View.INVISIBLE
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
                    viewModel.getAddresses.collect {
                        when (it) {
                            is Resource.Loading -> {
                                progressbarAddress.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                progressbarAddress.visibility = View.INVISIBLE
                            }

                            is Resource.Error -> {
                                progressbarAddress.visibility = View.INVISIBLE
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
                    viewModel.error.collect { error ->
                        Toast.makeText(
                            requireContext(),
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}