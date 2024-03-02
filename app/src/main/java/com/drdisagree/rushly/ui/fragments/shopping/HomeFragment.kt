package com.drdisagree.rushly.ui.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drdisagree.rushly.databinding.FragmentHomeBinding
import com.drdisagree.rushly.ui.adapters.HomeViewPagerAdapter
import com.drdisagree.rushly.ui.fragments.categories.MainCategoryFragment
import com.drdisagree.rushly.ui.fragments.categories.tabs.AccessoryFragment
import com.drdisagree.rushly.ui.fragments.categories.tabs.ChairFragment
import com.drdisagree.rushly.ui.fragments.categories.tabs.CupboardFragment
import com.drdisagree.rushly.ui.fragments.categories.tabs.FurnitureFragment
import com.drdisagree.rushly.ui.fragments.categories.tabs.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        val viewPagerAdapter = HomeViewPagerAdapter(
            categoriesFragments,
            childFragmentManager,
            lifecycle
        )

        binding.apply {
            viewPagerHome.adapter = viewPagerAdapter
            viewPagerHome.isUserInputEnabled = false

            TabLayoutMediator(tabLayout, viewPagerHome) { tab, position ->
                when (position) {
                    0 -> tab.text = "Main"
                    1 -> tab.text = "Chair"
                    2 -> tab.text = "Cupboard"
                    3 -> tab.text = "Table"
                    4 -> tab.text = "Accessory"
                    5 -> tab.text = "Furniture"
                }
            }.attach()
        }
    }
}