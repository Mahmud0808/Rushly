package com.drdisagree.rushly.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.ActivityShoppingBinding
import com.drdisagree.rushly.ui.viewmodels.CartViewModel
import com.drdisagree.rushly.utils.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val navController = findNavController(R.id.shoppingHostFragment)
            bottomNavigation.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment -> {
                        bottomNavigation.visibility = BottomNavigationView.VISIBLE
                    }

                    R.id.searchFragment -> {
                        bottomNavigation.visibility = BottomNavigationView.VISIBLE
                    }

                    R.id.cartFragment -> {
                        bottomNavigation.visibility = BottomNavigationView.VISIBLE
                    }

                    R.id.profileFragment -> {
                        bottomNavigation.visibility = BottomNavigationView.VISIBLE
                    }

                    else -> {
                        bottomNavigation.visibility = BottomNavigationView.GONE
                    }
                }
            }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.cartProducts.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                val count = it.data?.size ?: 0
                                if (count > 0) {
                                    bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                        number = count
                                        backgroundColor = ContextCompat.getColor(
                                            this@ShoppingActivity,
                                            R.color.g_blue
                                        )
                                    }
                                } else {
                                    bottomNavigation.removeBadge(R.id.cartFragment)
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

    override fun onStart() {
        super.onStart()

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null || !firebaseAuth.currentUser!!.isEmailVerified) {
            startActivity(
                Intent(
                    this@ShoppingActivity,
                    LoginRegisterActivity::class.java
                )
            )
            finishAffinity()
        }
    }
}