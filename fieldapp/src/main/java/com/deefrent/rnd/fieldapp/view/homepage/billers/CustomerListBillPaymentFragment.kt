package com.deefrent.rnd.fieldapp.view.homepage.billers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.CustomerListBillListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerListBillPaymentBinding
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerInfoCallback
import java.util.ArrayList

class CustomerListBillPaymentFragment : Fragment(), CustomerInfoCallback {
    private lateinit var binding: FragmentCustomerListBillPaymentBinding
    private lateinit var customerListBillAdapter: CustomerListBillListAdapter
    private var arrayList: ArrayList<CustomerInfo> = arrayListOf()
    private var displayList: ArrayList<CustomerInfo> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[BillersViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onCreate: ---kevo")
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerListBillPaymentBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v).navigateUp()
        }
        binding.rvAssessment.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        customerListBillAdapter = CustomerListBillListAdapter(displayList, this)
        binding.rvAssessment.adapter = customerListBillAdapter
        viewmodel.nameLookUpData.observe(viewLifecycleOwner) { nameLookUpData ->
            arrayList.clear()
            arrayList = nameLookUpData.data as ArrayList<CustomerInfo>
            displayList.addAll(arrayList)
            customerListBillAdapter.notifyDataSetChanged()
            Log.d("TAG", "onCreateView: list ${arrayList.size}")
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerListBillPaymentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int, customerInfo: CustomerInfo) {
        viewmodel.selectedCustomer.value = customerInfo
        findNavController().navigate(R.id.billersFragment)
    }
}