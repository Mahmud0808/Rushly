package com.drdisagree.rushly.ui.fragments.onboarding

import android.content.Intent
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
import com.drdisagree.rushly.databinding.FragmentLoginBinding
import com.drdisagree.rushly.ui.activities.ShoppingActivity
import com.drdisagree.rushly.ui.dialogs.showResetPasswordDialog
import com.drdisagree.rushly.ui.dialogs.showVerifyEmailDialog
import com.drdisagree.rushly.ui.viewmodels.LoginViewModel
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.getBottomNavView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("email")?.let {
            showVerifyEmailDialog {
                viewModel.resendVerificationMail()
            }
        }

        binding.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()

                viewModel.login(email, password)
            }

            tvForgotPasswordLogin.setOnClickListener {
                showResetPasswordDialog {
                    viewModel.resetPassword(email = it)
                }
            }

            facebookLogin.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }

            googleLogin.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.resetPassword.collect {
                        when (it) {
                            is Resource.Loading -> {
                            }

                            is Resource.Success -> {
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    it.message.toString(),
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
                                snackbar.setAnchorView(getBottomNavView())
                                snackbar.show()
                            }

                            is Resource.Error -> {
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Error: ${it.message.toString()}",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
                                snackbar.show()
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
                    viewModel.login.collect {
                        when (it) {
                            is Resource.Loading -> {
                                buttonLogin.startAnimation()
                            }

                            is Resource.Success -> {
                                Intent(
                                    requireActivity(),
                                    ShoppingActivity::class.java
                                ).also { intent ->
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            }

                            is Resource.Error -> {
                                buttonLogin.revertAnimation()
                                val message = it.message.toString()
                                val messageLower = message.lowercase(Locale.ROOT)

                                if (messageLower.contains("email") &&
                                    messageLower.contains("password")
                                ) {
                                    edEmailLogin.requestFocus()
                                    edPasswordLogin.requestFocus()

                                    edEmailLogin.error = message
                                    edPasswordLogin.error = message
                                } else {
                                    var errorShown = false

                                    if (messageLower.contains("email")) {
                                        edEmailLogin.requestFocus()
                                        edEmailLogin.error = message
                                        errorShown = true
                                    }
                                    if (messageLower.contains("password")) {
                                        edPasswordLogin.requestFocus()
                                        edPasswordLogin.error = message
                                        errorShown = true
                                    }

                                    if (!errorShown) {
                                        Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
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
}