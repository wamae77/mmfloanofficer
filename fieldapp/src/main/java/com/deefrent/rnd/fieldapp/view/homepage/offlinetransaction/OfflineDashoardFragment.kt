package com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.adapters.OfflineViewPagerAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOfflineDashoardBinding
import com.google.android.material.tabs.TabLayoutMediator

class OfflineDashoardFragment : Fragment() {

    private lateinit var binding:FragmentOfflineDashoardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOfflineDashoardBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
    }
    private fun setViewPager() {
        val adapter = OfflineViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        //Tab titles
        val titles = arrayOf("Registration", "Assessment")
        adapter.addFragment(OfflineRegFragment())
        adapter.addFragment(OfflineAssessmentFragment())
        //Attach tab mediator
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position -> tab.text = (titles[position]) }.attach()

        // Line disables swiping to allow viewpager in recyclerview to be swipeable
        //  binding.viewPager.isUserInputEnabled = false
    }


}