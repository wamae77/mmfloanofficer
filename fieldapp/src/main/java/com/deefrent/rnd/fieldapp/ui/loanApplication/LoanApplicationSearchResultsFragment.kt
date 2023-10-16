package com.deefrent.rnd.fieldapp.ui.loanApplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanApplicationSearchResultsBinding
import com.google.android.material.tabs.TabLayoutMediator

private const val NUM_TABS = 2
val tabArray = arrayOf("Loan Status", "Calculate Loan")

class LoanApplicationSearchResultsFragment : Fragment() {
    private var _binding: FragmentLoanApplicationSearchResultsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: LoanApplicationSharedViewModel by activityViewModels()
    private var isCalculateLoanTabSelected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoanApplicationSearchResultsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
            sharedViewModel.selectCalculateLoanTab(false)
        }
        //set up tabs
        val adapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
        sharedViewModel.calculateLoanTabSelected.observe(viewLifecycleOwner,
            { calculateLoanTabSelected ->
                isCalculateLoanTabSelected = calculateLoanTabSelected
                Log.d("SharedViewModel", "selected: $isCalculateLoanTabSelected")
                when (isCalculateLoanTabSelected) {
                    true -> {
                        binding.tabLayout.setScrollPosition(1, 0f, true)
                        binding.viewPager.currentItem = 1
                    }
                    else -> {
                        //binding.tabLayout.setScrollPosition(0, 0f, true)
                        //binding.viewPager.currentItem = 0
                    }
                }
            })
        return view
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LoanStatus2Fragment()
                else -> CalculateLoanFragment2()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isCalculateLoanTabSelected = false
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoanApplicationSearchResultsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}