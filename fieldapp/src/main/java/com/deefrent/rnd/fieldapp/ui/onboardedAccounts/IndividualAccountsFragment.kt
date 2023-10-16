package com.deefrent.rnd.fieldapp.ui.onboardedAccounts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.adapters.OnboardedCustomersListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentIndividualAccountsBinding
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Customer
import com.deefrent.rnd.fieldapp.viewModels.OnboardedAccountsViewModel

class IndividualAccountsFragment : Fragment() {
    private var _binding: FragmentIndividualAccountsBinding? = null
    private val binding get() = _binding!!
    private val onboardedAccountsViewModel: OnboardedAccountsViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val customersList = ArrayList<Customer>()
    private lateinit var customersListAdapter: OnboardedCustomersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndividualAccountsBinding.inflate(inflater, container, false)
        val view = binding.root
        val customers = OnboardedAccountsFragment.customers
        customersList.clear()
        if (customers.isNotEmpty()) {
            binding.tvNoData.visibility = View.GONE
            binding.rvAccounts.visibility = View.VISIBLE
            customersList.addAll(customers)
            customersListAdapter = OnboardedCustomersListAdapter(customersList)
            linearLayoutManager = LinearLayoutManager(requireContext())
            binding.rvAccounts.layoutManager = linearLayoutManager
            binding.rvAccounts.adapter = customersListAdapter
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvAccounts.visibility = View.GONE
        }
        return view
    }

    private fun observeSharedViewModel() {
        onboardedAccountsViewModel.apply {
            onboardedAccountsResponse.observe(viewLifecycleOwner,
                { onboardedAccountsResponse1 ->
                    val customers = onboardedAccountsResponse1.data.Customers
                    Log.d(TAG, "observeSharedViewModel: ${customers.size}")
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //dashBoardItemsList.clear()
    }

    companion object {
        private const val TAG = "Individual Accounts"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IndividualAccountsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}