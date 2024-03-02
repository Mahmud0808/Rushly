package com.drdisagree.rushly.ui.fragments.onboarding

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
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.User
import com.drdisagree.rushly.databinding.FragmentRegisterBinding
import com.drdisagree.rushly.ui.viewmodels.RegisterViewModel
import com.drdisagree.rushly.utils.validations.LoginRegisterValidation
import com.drdisagree.rushly.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvLogIn.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            buttonRegister.setOnClickListener {
                viewModel.createUserWithEmailAndPassword(
                    User(
                        firstName = edFirstNameRegister.text.toString().trim(),
                        lastName = edLastNameRegister.text.toString().trim(),
                        email = edEmailRegister.text.toString().trim()
                    ),
                    edPasswordRegister.text.toString()
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.register.collect {
                        when (it) {
                            is Resource.Loading -> {
                                buttonRegister.startAnimation()
                            }

                            is Resource.Success -> {
                                buttonRegister.revertAnimation()
                                val bundle = Bundle()
                                bundle.putString("email", it.data?.email)
                                findNavController().navigate(
                                    R.id.action_registerFragment_to_loginFragment,
                                    bundle
                                )
                            }

                            is Resource.Error -> {
                                buttonRegister.revertAnimation()
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
                viewModel.validation.collect {
                    if (it.email is LoginRegisterValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            edEmailRegister.apply {
                                requestFocus()
                                error = it.email.error
                            }
                        }
                    }
                    if (it.password is LoginRegisterValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            edPasswordRegister.apply {
                                requestFocus()
                                error = it.password.error
                            }
                        }
                    }
                }
            }
        }
    }
}