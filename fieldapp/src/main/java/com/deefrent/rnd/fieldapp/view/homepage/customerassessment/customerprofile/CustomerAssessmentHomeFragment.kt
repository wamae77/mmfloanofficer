package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.AssesementHomeAdapter
import com.deefrent.rnd.fieldapp.databinding.CustomerAssessmentHomeFragmentBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.utils.callbacks.DashboardCallBack
import com.deefrent.rnd.fieldapp.utils.doubleToStringNoDecimal
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel

class CustomerAssessmentHomeFragment : Fragment(), DashboardCallBack {
    private lateinit var binding: CustomerAssessmentHomeFragmentBinding
    private lateinit var assesementHomeAdapter: AssesementHomeAdapter
    private val itemsList = ArrayList<DashboardItem>()
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerAssessmentHomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CustomerAssessmentHomeFragmentBinding.inflate(layoutInflater)
        // binding.ivBack.findNavController().navigateUp()
        assesementHomeAdapter = AssesementHomeAdapter(itemsList, this)
        binding.rvAssessMent.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAssessMent.adapter = assesementHomeAdapter
        prepareDashboardItems()
        viewModel.iDLookUpData.observe(viewLifecycleOwner) {
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
            val percentage = it.assessmentPercentage.toDouble()
            binding.pb.progress = doubleToStringNoDecimal(percentage)?.toInt() ?: 0
            binding.tvProgressCircle.text = "${doubleToStringNoDecimal(percentage)}%"
        }
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v).navigateUp()
            //findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_customerIDLookupFragment)
        }
        handleBackButton()
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                    //findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_customerIDLookupFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
        }

    }

    private fun prepareDashboardItems(): ArrayList<DashboardItem> {
        val dashboardItemNames = listOf(
            requireContext().getString(R.string.customer_details),
            requireContext().getString(R.string.householsd_info),
            requireContext().getString(R.string.expense_info),
            requireContext().getString(R.string._income_info),
            requireContext().getString(R.string.customer_doc),
        )
        for (dashBoardItemName in dashboardItemNames.indices) {
            val dashboardItem = DashboardItem(dashboardItemNames[dashBoardItemName])
            itemsList.add(dashboardItem)
        }
        assesementHomeAdapter.notifyDataSetChanged()
        return itemsList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemsList.clear()
    }

    override fun onItemSelected(pos: Int) {
        when (pos) {
            0 -> {
                findNavController().navigate(R.id.action_customerAst_to_customeentFragment)
            }
            1 -> {
                findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_householdInfoFragment)
            }
            2 -> {
                findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_expensesInfoFragment)
            }
            3 -> {
                findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_incomeDetailsFragment)
            }
            4->{
               findNavController().navigate(R.id.action_customerAssessmentHomeFragment_to_documentTypeFragment)
            }
        }
    }

}