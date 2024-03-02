package com.drdisagree.rushly.ui.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.User
import com.drdisagree.rushly.databinding.FragmentProfileBinding
import com.drdisagree.rushly.ui.activities.LoginRegisterActivity
import com.drdisagree.rushly.ui.dialogs.showResetPasswordDialog
import com.drdisagree.rushly.ui.viewmodels.LoginViewModel
import com.drdisagree.rushly.ui.viewmodels.UserAccountViewModel
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val userAccountViewModel by viewModels<UserAccountViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            imageProfileClose.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonEditProfile.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable("user", user)
                }
                findNavController().navigate(
                    R.id.action_profileFragment_to_userAccountFragment,
                    bundle
                )
            }

            layoutMyOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_orderListFragment)
            }

            layoutAllOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_allOrderListFragment)
            }

            layoutBillingAddress.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                    0f,
                    emptyArray(),
                    false
                )
                findNavController().navigate(action)
            }

            layoutAddNewProduct.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_newProductFragment)
            }

            layoutChangePassword.setOnClickListener {
                showResetPasswordDialog {
                    loginViewModel.resetPassword(email = it)
                }
            }

            layoutInformation.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://github.com/Mahmud0808/Rushly")
                }
                startActivity(intent)
            }

            layoutLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        requireContext(),
                        LoginRegisterActivity::class.java
                    )
                )
                requireActivity().finishAffinity()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    userAccountViewModel.user.collect {
                        when (it) {
                            is Resource.Loading -> {
                                progressbarProfile.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                user = it.data
                                progressbarProfile.visibility = View.INVISIBLE
                                Glide.with(this@ProfileFragment)
                                    .load(user?.imagePath)
                                    .error(R.drawable.no_user_image)
                                    .into(imageUser)
                                val fullName = "${user?.firstName} ${user?.lastName}"
                                tvUserName.text = fullName
                                tvEmail.text = user?.email
                            }

                            is Resource.Error -> {
                                progressbarProfile.visibility = View.INVISIBLE
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
                    userAccountViewModel.isAdmin.collect {
                        when (it) {
                            is Resource.Loading -> {
                            }

                            is Resource.Success -> {
                                if (it.data == true) {
                                    layoutAllOrders.visibility = View.VISIBLE
                                    layoutAddNewProduct.visibility = View.VISIBLE
                                } else {
                                    layoutAllOrders.visibility = View.GONE
                                    layoutAddNewProduct.visibility = View.GONE
                                }
                            }

                            is Resource.Error -> {
                                layoutAllOrders.visibility = View.GONE
                                layoutAddNewProduct.visibility = View.GONE
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