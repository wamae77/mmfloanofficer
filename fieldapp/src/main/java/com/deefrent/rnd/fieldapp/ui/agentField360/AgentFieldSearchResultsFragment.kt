package com.deefrent.rnd.fieldapp.ui.agentField360

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.DashboardListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentFieldSearchResultsBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.utils.callbacks.DashboardCallBack
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class AgentFieldSearchResultsFragment : Fragment(),DashboardCallBack {
    private var _binding: FragmentAgentFieldSearchResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val dashBoardItemsList = ArrayList<DashboardItem>();
    private lateinit var dashboardListAdapter: DashboardListAdapter
    private val sharedViewModel: AgentFieldSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    private var userType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userType = arguments?.getString("userType").toString()
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgentFieldSearchResultsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
        }
        dashboardListAdapter = DashboardListAdapter(dashBoardItemsList, requireContext(),this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvAgentSearchResults.layoutManager = linearLayoutManager
        binding.rvAgentSearchResults.adapter = dashboardListAdapter
        prepareDashboardItems()
        observeExistingAccountViewModel()
        return view
    }

    private fun prepareDashboardItems() {
        lateinit var SelectedUserType: String
        existingAccountViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    SelectedUserType = userType
                    val dashboardItemNamesMerchant = listOf(
                        requireContext().getString(R.string.view_transaction_analysis),
                        requireContext().getString(R.string.view_commissions_analysis),
                        requireContext().getString(R.string.loan_status),
                        requireContext().getString(R.string.online_questionnaire),
                        "$SelectedUserType Location Details",
                        "$SelectedUserType Complains"
                    )
                    val dashboardItemNamesIndividual = listOf(
                        requireContext().getString(R.string.loan_status),
                        requireContext().getString(R.string.online_questionnaire),
                        requireContext().getString(R.string.customers_complains)
                    )
                    if (userType == "Individual") {
                        for (dashBoardItemName in dashboardItemNamesIndividual.indices) {
                            val dashboardItem =
                                DashboardItem(dashboardItemNamesIndividual[dashBoardItemName])
                            dashBoardItemsList.add(dashboardItem)
                        }
                    } else {
                        for (dashBoardItemName in dashboardItemNamesMerchant.indices) {
                            val dashboardItem =
                                DashboardItem(dashboardItemNamesMerchant[dashBoardItemName])
                            dashBoardItemsList.add(dashboardItem)
                        }
                    }
                    //Log.d("dashboard", "items are: $dashBoardItemsList")
                    dashboardListAdapter.notifyDataSetChanged()
                })
        }

    }

    private fun observeExistingAccountViewModel() {
        lateinit var AccountNumber: String
        existingAccountViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                })
            userType.observe(viewLifecycleOwner,
                { userType ->
                    when (userType) {
                        "Agent" -> {
                            binding.tvFragmentTitle.text = "$userType Field 360"
                            binding.tvAgentNumber.text = "Agent No: 3782"
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                { merchantAgentDetailsResponse ->
                                    binding.tvAgentTelephone.text =
                                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                    binding.tvAccountNumber.text =
                                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                                })
                        }
                        "Merchant" -> {
                            binding.tvFragmentTitle.text = "$userType Field 360"
                            binding.tvAgentNumber.text = "Merchant No: 3782"
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                { merchantAgentDetailsResponse ->
                                    binding.tvAgentTelephone.text =
                                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                    binding.tvAccountNumber.text =
                                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                                })
                        }
                        "Individual" -> {
                            binding.tvFragmentTitle.text = "Customer Field 360"
                            customerDetailsResponse.observe(viewLifecycleOwner,
                                { customerDetailsResponse ->
                                    binding.tvAccountNumber.text =
                                        "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $AccountNumber"
                                    binding.tvAgentNumber.text =
                                        "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}  ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                                    binding.tvAgentTelephone.text =
                                        "Tel: ${customerDetailsResponse.customerDetailsData.customerDetails.phoneNo}"
                                })

                        }
                    }
                })
        }
    }

    /*private fun observeSharedViewModel() {
        var selectedUserType = ""
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    selectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    when (selectedUserType) {
                        "Individual" -> {
                            binding.tvAccountNumber.text =
                                "ACC: CURRENT ACCOUNT - \n $accountNumber"
                            binding.tvAgentNumber.text = "MARTIN NDUNGU"
                            binding.tvAgentTelephone.text = "ID: 30441304"
                        }
                    }

                })
        }

    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dashBoardItemsList.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentFieldSearchResultsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int) {

    }
}