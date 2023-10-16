package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.MerchantInfoListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentMerchantInfoBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class MerchantInfoFragment : Fragment() {
    private var UserType = ""
    private var _binding: FragmentMerchantInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val dashBoardItemsList = ArrayList<DashboardItem>();
    private lateinit var dashboardListAdapter: MerchantInfoListAdapter
    private val sharedViewModel: ExistingAccountSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMerchantInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        dashboardListAdapter = MerchantInfoListAdapter(dashBoardItemsList, requireContext(), this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvMerchantInfo.layoutManager = linearLayoutManager
        binding.rvMerchantInfo.adapter = dashboardListAdapter
        prepareDashboardItems()
        observeExistingAccountViewModel()
        observeSharedViewModel()
        return view
    }

    private fun observeExistingAccountViewModel() {
        lateinit var AccountNumber: String
        existingAccountViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                })
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    binding.tvPhoneNo.text =
                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                    binding.tvAccountNumber.text =
                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                })
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType Info"
                })
        }
    }

    private fun observeSharedViewModel() {
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    UserType = userType
                    when (UserType) {
                        "Merchant" -> binding.tvMerchantAgentNo.text = "Merchant no. 537288"
                        "Agent" -> binding.tvMerchantAgentNo.text = "Agent no. 537288"
                    }
                })
        }
    }

    private fun prepareDashboardItems() {
        lateinit var SelectedUserType: String
        existingAccountViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    SelectedUserType = userType
                    val dashboardItemNames = listOf(
                        "$SelectedUserType Details",
                        requireContext().getString(R.string.liquidation_details),
                        "$SelectedUserType Loan Status",
                        requireContext().getString(R.string.active_channels),
                        requireContext().getString(R.string.physical_address),
                        requireContext().getString(R.string.business_images),
                        requireContext().getString(R.string.personal_images),
                        requireContext().getString(R.string.new_account)
                    )
                    for (dashBoardItemName in dashboardItemNames.indices) {
                        val dashboardItem = DashboardItem(dashboardItemNames[dashBoardItemName])
                        dashBoardItemsList.add(dashboardItem)
                    }
                    //Log.d("dashboard", "items are: $dashBoardItemsList")
                    dashboardListAdapter.notifyDataSetChanged()
                })
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dashBoardItemsList.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MerchantInfoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}