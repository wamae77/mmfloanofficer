package com.deefrent.rnd.fieldapp.ui.onboardMerchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.DashboardListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardMerchantSearchBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.utils.callbacks.DashboardCallBack

class OnboardMerchantSearchFragment : Fragment(),DashboardCallBack {
    private var _binding: FragmentOnboardMerchantSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val dashBoardItemsList = ArrayList<DashboardItem>();
    private lateinit var dashboardListAdapter: DashboardListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardMerchantSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
        }
        dashboardListAdapter = DashboardListAdapter(dashBoardItemsList,requireContext(),this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvAgentSearchResults.layoutManager = linearLayoutManager
        binding.rvAgentSearchResults.adapter = dashboardListAdapter
        prepareDashboardItems()
        return view
    }

    private fun prepareDashboardItems(): ArrayList<DashboardItem> {
        val dashboardItemNames = listOf(
            requireContext().getString(R.string.merchant_agent_details),
            requireContext().getString(R.string.contact_person_details),
            requireContext().getString(R.string.liquidation_details),
            requireContext().getString(R.string.physical_address),
            requireContext().getString(R.string.admin_user_details),
            requireContext().getString(R.string.agent_images)
        )
        for (dashBoardItemName in dashboardItemNames.indices) {
            val dashboardItem = DashboardItem(dashboardItemNames[dashBoardItemName])
            dashBoardItemsList.add(dashboardItem)
        }
        //Log.d("dashboard", "items are: $dashBoardItemsList")
        dashboardListAdapter.notifyDataSetChanged()
        return dashBoardItemsList;
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
            OnboardMerchantSearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int) {

    }
}