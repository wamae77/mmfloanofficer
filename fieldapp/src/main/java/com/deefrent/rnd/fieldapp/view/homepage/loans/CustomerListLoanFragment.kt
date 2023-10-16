package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.CustomerListLoanAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerListLoanBinding
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanLookUpDataCallBack
import com.deefrent.rnd.fieldapp.utils.onInfoDialogWarn
import com.google.gson.Gson
import java.util.ArrayList

class CustomerListLoanFragment : Fragment(), LoanLookUpDataCallBack {
    private lateinit var binding: FragmentCustomerListLoanBinding
    private lateinit var customerListLoanAdapter: CustomerListLoanAdapter
    private var arrayList: ArrayList<LoanLookupData> = arrayListOf()
    private var displayList: ArrayList<LoanLookupData> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[LoanLookUpViewModel::class.java]
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
        binding = FragmentCustomerListLoanBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v).navigateUp()
        }
        handleBackButton()
        binding.rvAssessment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        customerListLoanAdapter = CustomerListLoanAdapter(displayList, this)
        binding.rvAssessment.adapter = customerListLoanAdapter
        viewmodel.nameLookUpData.observe(viewLifecycleOwner) { nameLookUpData ->
            arrayList.clear()
            arrayList = nameLookUpData.data as ArrayList<LoanLookupData>
            displayList.addAll(arrayList)
            customerListLoanAdapter.notifyDataSetChanged()
            Log.d("TAG", "onCreateView: list ${arrayList.size}")
        }
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_listLoanFragment_to_loanLookupFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerListLoanFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int, loanLookupData: LoanLookupData) {
        viewmodel._loanLookUpData.value = loanLookupData
        viewmodel._idNumber.value = loanLookupData.idNumber
        val gson = Gson()
        val jsonString = gson.toJson(loanLookupData)
        CommonSharedPreferences(requireContext()).saveStringData(
            key = CommonSharedPreferences.LOANLOOKUPDATA,
            value = jsonString
        )
        if (loanLookupData.fingerPrintRegId == "") {
            findNavController().navigate(R.id.updateCustomerFingerPrintFragment)
            //findNavController().navigate(R.id.enrollOneFingerPrintFragment)
        } else {
            if (loanLookupData.isAssessed) {
                findNavController().navigate(R.id.action_loanListFragment_to_loanHomeFragment)
                viewmodel.stopObserving()
            } else {
                onInfoDialogWarn(getString(R.string.customer_not_assessed))
            }
        }


    }
}