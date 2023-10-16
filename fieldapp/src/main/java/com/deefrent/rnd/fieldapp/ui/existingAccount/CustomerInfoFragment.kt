package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.CustomerInfoListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerInfoBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class CustomerInfoFragment : Fragment() {
    private var _binding: FragmentCustomerInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val dashBoardItemsList = ArrayList<DashboardItem>()
    private lateinit var dashboardListAdapter: CustomerInfoListAdapter
    private val sharedViewModel: ExistingAccountSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //customerKYC = arguments?.getParcelable("customerDetails")
        //Log.d("customerDetails", "onCreate: ${customerKYC?.customerDetails?.firstName}")
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        dashboardListAdapter = CustomerInfoListAdapter(dashBoardItemsList, requireContext(), this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerInfo.layoutManager = linearLayoutManager
        binding.rvCustomerInfo.adapter = dashboardListAdapter
        prepareDashboardItems()
        observeSharedViewModel()
        displayCustomerData()
        return view
    }

    private fun displayCustomerData() {
        lateinit var AccountNumber: String
        existingAccountViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                })
            customerDetailsResponse.observe(viewLifecycleOwner,
                { customerDetailsResponse ->
                    binding.tvAccountNumber.text =
                     "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $AccountNumber"
                    binding.tvCustomerID.text =
                        "Tel: ${customerDetailsResponse.customerDetailsData.customerDetails.phoneNo}"
                    binding.tvAccountName.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}  ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                })
        }
        //Log.d("customer", "displayCustomerData: ${customerKYC?.frontIdCapture}")
    }

    private fun observeSharedViewModel() {
        sharedViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    //binding.tvAccountNumber.text =
                    // "ACC: ${customerKYC?.customerDetails?.accountType?.personalAccountTypeName} - \n $accountNumber"
                })
            /*customerID.observe(viewLifecycleOwner,
                { customerID ->
                    binding.tvCustomerID.text = "Tel: 0715361229"
                })*/
        }
    }

    private fun prepareDashboardItems(): ArrayList<DashboardItem> {
        val dashboardItemNames = listOf(
            requireContext().getString(R.string.customer_details),
            requireContext().getString(R.string.bank_account_details),
            requireContext().getString(R.string.id_passport_photos),
            requireContext().getString(R.string.customer_loan_status),
            requireContext().getString(R.string.active_channels),
            requireContext().getString(R.string.new_account)
        )
        for (dashBoardItemName in dashboardItemNames.indices) {
            val dashboardItem = DashboardItem(dashboardItemNames[dashBoardItemName])
            dashBoardItemsList.add(dashboardItem)
        }
        //Log.d("dashboard", "items are: $dashBoardItemsList")
        dashboardListAdapter.notifyDataSetChanged()
        return dashBoardItemsList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dashBoardItemsList.clear()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerInfoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}