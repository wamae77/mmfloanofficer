package com.deefrent.rnd.fieldapp.view.homepage.incompleteRegistration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.ViewPagerAdapter
import com.deefrent.rnd.fieldapp.databinding.IncompleteRegDashboardFragmentBinding
import com.deefrent.rnd.fieldapp.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class IncompleteRegDashboardFragment : Fragment() {
    private lateinit var binding:IncompleteRegDashboardFragmentBinding
    private lateinit var viewModel: IncompleteRegDashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= IncompleteRegDashboardFragmentBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            if (Constants.isFromCustomerDetails) {
                findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_dashboardFragment)
            } else {
                Constants.isFromCustomerDetails=false
                findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_dashboardFragment)
            }
        }
        viewModel = ViewModelProvider(requireActivity())[IncompleteRegDashboardViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
        handleBackButton()
    }
    private fun setViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        //Tab titles
        val titles = arrayOf("Local Registration", "Other Registration")
        adapter.addFragment(LocalRegistrationFragment())
        adapter.addFragment(OtherRegistartionFragment())
        //Attach tab mediator
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position -> tab.text = (titles[position]) }.attach()

        // Line disables swiping to allow viewpager in recyclerview to be swipeable
      //  binding.viewPager.isUserInputEnabled = false
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    if (Constants.isFromCustomerDetails) {
                        findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_dashboardFragment)
                    } else {
                        Constants.isFromCustomerDetails=false
                        findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_dashboardFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }




}