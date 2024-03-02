package com.drdisagree.rushly.ui.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.drdisagree.rushly.R
import com.drdisagree.rushly.data.User
import com.drdisagree.rushly.databinding.FragmentUserAccountBinding
import com.drdisagree.rushly.ui.viewmodels.UserAccountViewModel
import com.drdisagree.rushly.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private val args by navArgs<UserAccountFragmentArgs>()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = args.user

        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                imageUri?.let { uri ->
                    Glide.with(this).load(uri).into(binding.imageUser)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (user != null) {
                showUserInformation(user!!)
            }

            buttonSave.setOnClickListener {
                binding.apply {
                    viewModel.updateUser(
                        User(
                            firstName = edFirstName.text.toString().trim(),
                            lastName = edLastName.text.toString().trim(),
                            email = edEmail.text.toString().trim()
                        ),
                        imageUri
                    )
                }
            }

            imageEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                imageActivityResultLauncher.launch(intent)
            }

            imageUserAccountClose.setOnClickListener {
                findNavController().navigateUp()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.updateInfo.collect {
                        when (it) {
                            is Resource.Loading -> {
                                buttonSave.startAnimation()
                            }

                            is Resource.Success -> {
                                buttonSave.revertAnimation()
                                findNavController().navigateUp()
                            }

                            is Resource.Error -> {
                                buttonSave.revertAnimation()
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
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment)
                .load(data.imagePath)
                .error(R.drawable.no_user_image)
                .into(imageUser)
            edFirstName.hint = data.firstName
            edLastName.hint = data.lastName
            edEmail.hint = data.email
        }
    }
}