package com.deefrent.rnd.fieldapp.ui.onboardedAccounts

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
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardedAccountsBinding
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Agent
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Customer
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Merchant
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.utils.formatDate
import com.deefrent.rnd.fieldapp.viewModels.OnboardedAccountsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

private const val NUM_TABS = 3
val tabArray = arrayOf("Individuals", "Agents", "Merchants")

class OnboardedAccountsFragment : Fragment() {
    private var _binding: FragmentOnboardedAccountsBinding? = null
    private val binding get() = _binding!!
    val onboardedAccountsViewModel: OnboardedAccountsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardedAccountsBinding.inflate(inflater, container, false)
        val view = binding.root

        //set up tabs
        val adapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
        observeSharedViewModel()
        return view
    }

    private fun observeSharedViewModel() {
        onboardedAccountsViewModel.apply {
            startDate.observe(viewLifecycleOwner) { startDate ->
                endDate.observe(viewLifecycleOwner) { endDate ->
                    binding.tvTitle.text =
                        "From ${formatDate(startDate).capitalizeWords} To ${formatDate(endDate).capitalizeWords}"
                }
            }
            onboardedAccountsResponse.observe(
                viewLifecycleOwner
            ) { onboardedAccountsResponse ->
                if (onboardedAccountsResponse.data.Merchants.Agent != null) {
                    agents = onboardedAccountsResponse.data.Merchants.Agent
                    Log.d("TAG", "observeSharedViewModel: ${agents.size}")
                    /* merchants = merchantsObj.Merchant
                     Log.d("TAG", "observeSharedViewModel: ${merchants.size}")*/
                } else {
                    agents = emptyList()
                }
                if (onboardedAccountsResponse.data.Merchants.Merchant != null) {
                    merchants = onboardedAccountsResponse.data.Merchants.Merchant
                    Log.d("TAG", "observeSharedViewModel: ${merchants.size}")
                } else {
                    merchants = emptyList()
                }
                if (onboardedAccountsResponse.data.Customers.isNotEmpty()) {
                    customers = onboardedAccountsResponse.data.Customers
                    Log.d("TAG", "observeSharedViewModel: ${customers.size}")
                } else {
                    customers = emptyList()
                }
            }
        }
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> IndividualAccountsFragment()
                1 -> AgentAccountsFragment()
                else -> MerchantAccountsFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        lateinit var agents: List<Agent>
        lateinit var merchants: List<Merchant>
        lateinit var customers: List<Customer>

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnboardedAccountsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}