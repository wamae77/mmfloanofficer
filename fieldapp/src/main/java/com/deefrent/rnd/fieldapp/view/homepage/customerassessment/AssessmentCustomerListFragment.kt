package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.AssessmentCustomerListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentAssessmentCustomerListBinding
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerInfoCallback
import java.util.*
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.utils.Constants
import com.google.gson.Gson

class AssessmentCustomerListFragment : Fragment(), CustomerInfoCallback {
    private lateinit var binding: FragmentAssessmentCustomerListBinding
    private lateinit var assessmentCustomerListAdapter: AssessmentCustomerListAdapter
    private var arrayList: ArrayList<CustomerInfo> = arrayListOf()
    private var displayList: ArrayList<CustomerInfo> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[CustomerAssessmentHomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAssessmentCustomerListBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v).navigateUp()
        }
        handleBackButton()
        binding.rvAssessment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        assessmentCustomerListAdapter = AssessmentCustomerListAdapter(displayList, this)
        binding.rvAssessment.adapter = assessmentCustomerListAdapter
        viewmodel.nameLookUpData.observe(viewLifecycleOwner) { nameLookUpData ->
            arrayList.clear()
            arrayList = nameLookUpData.data as ArrayList<CustomerInfo>
            displayList.addAll(arrayList)
            assessmentCustomerListAdapter.notifyDataSetChanged()
            Log.d("TAG", "onCreateView: list ${arrayList.size}")
        }
        searchCustomer()
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                    //findNavController().navigate(R.id.action_customerListFragment_to_customerIDLookupFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun searchCustomer() {
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    binding.tvError.visibility = View.GONE
                    val search = newText.toLowerCase(Locale.US)
                    arrayList.forEach {
                        if (it.firstName.toLowerCase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "No customer found"
                    } else {
                        binding.tvError.visibility = View.GONE
                    }
                    assessmentCustomerListAdapter.notifyDataSetChanged()
                } else {
                    binding.tvError.visibility = View.GONE
                    displayList.clear()
                    displayList.addAll(arrayList)
                    assessmentCustomerListAdapter.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssessmentCustomerListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int, customerInfo: CustomerInfo) {
        Log.d("TAG", "onItemSelected: ${Gson().toJson(customerInfo)}")
        val customerInfoExpenses = customerInfo.expenses
        val customerInfoIncomes = customerInfo.incomes
        val expenses = Expenses(
            customerInfoExpenses?.domesticWorkersWages ?: "",
            customerInfoExpenses?.food ?: "",
            customerInfoExpenses?.funeralPolicy ?: "",
            customerInfoExpenses?.medicalAidOrContributions ?: "",
            customerInfoExpenses?.other ?: "",
            customerInfoExpenses?.rentals ?: "",
            customerInfoExpenses?.schoolFees ?: "",
            customerInfoExpenses?.tithe ?: "",
            customerInfoExpenses?.transport ?: ""
        )
        val incomes = Incomes(
            customerInfoIncomes?.incomeNetSalary ?: "",
            customerInfoIncomes?.incomeProfit ?: "",
            customerInfoIncomes?.incomeStatementDoc ?: IncomeStatementDoc("", 0, ""),
            customerInfoIncomes?.incomeTotalSales ?: "",
            customerInfoIncomes?.other ?: "",
            customerInfoIncomes?.otherBusinesses ?: "",
            customerInfoIncomes?.ownSalary ?: "",
            customerInfoIncomes?.remittanceOrDonation ?: "",
            customerInfoIncomes?.rental ?: "",
            customerInfoIncomes?.rentalDoc ?: RentalDoc("", 0, ""),
            customerInfoIncomes?.roscals ?: "",
            customerInfoIncomes?.salesReportDoc ?: SalesReportDoc("", 0, "")
        )
        val householdMembers = customerInfo.householdMembers.map {
            HouseholdMember(
                it.fullName, it.incomeOrFeesPaid, it.natureOfActivity, it.occupation,
                it.occupationId.toString(), it.relationShip, it.relationshipId.toString(),
                it.memberId
            )
        }
        val documentsList: List<DocumentType> = arrayListOf()
        val customerIDData = customerInfo.id?.let {
            CustomerIDData(
                it, customerInfo.customerNumber, customerInfo.assessmentPercentage,
                customerInfo.assessmentRemarks, expenses, customerInfo.firstName, householdMembers,
                customerInfo.idNumber, incomes, customerInfo.isFullyRegistered, documentsList,
                customerInfo.lastName
            )
        }
        viewmodel._iDLookUpData.value = customerIDData
        viewmodel._idNumber.value = customerInfo.idNumber
        Constants.lookupId = customerInfo.idNumber
        findNavController().navigate(R.id.action_customerListFragment_to_customerAssessmentHomeFragment)
    }
}