package com.deefrent.rnd.fieldapp.view.homepage.incompleteRegistration

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.IncompleteLocalRegAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLocalRegistrationBinding
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.CusomerDetailsEntityWithList
import com.deefrent.rnd.fieldapp.room.repos.CustomerDetailsRepository
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerEntityCallBack
import com.deefrent.rnd.fieldapp.utils.deleteImageFromInternalStorage
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.OnboardCustomerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class LocalRegistrationFragment : Fragment(), CustomerEntityCallBack {
    private lateinit var binding: FragmentLocalRegistrationBinding
    private lateinit var viewModel: OnboardCustomerViewModel
    private lateinit var incompleteAdapter: IncompleteLocalRegAdapter
    private var arrayList:ArrayList<CusomerDetailsEntityWithList> = arrayListOf()
    private var displayList:ArrayList<CusomerDetailsEntityWithList> = arrayListOf()
    private lateinit var repository: CustomerDetailsRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLocalRegistrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[OnboardCustomerViewModel::class.java]
        incompleteAdapter = IncompleteLocalRegAdapter(displayList,this)
        binding.rvIncomplete.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvIncomplete.adapter = incompleteAdapter
        searchCustomer()
        viewModel.customerList.observe(viewLifecycleOwner){
            displayList.clear()
            arrayList.clear()
            displayList.addAll(it)
            incompleteAdapter.notifyDataSetChanged()
            arrayList.addAll(it)
            if (it.isEmpty()){
                binding.svLocalAssessment.isRefreshing=false
                binding.search.makeGone()
                binding.noNewRequest.makeVisible()
            }else{
                binding.svLocalAssessment.isRefreshing=false
                binding.search.makeVisible()
                binding.noNewRequest.makeGone()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocalCustomerDetails()
        binding.svLocalAssessment.setOnRefreshListener {
            getLocalCustomerDetails()
        }

    }
    private fun getLocalCustomerDetails(){
        GlobalScope.launch(Dispatchers.IO) {
            val cList= repository.getIncompleteCustomerDetails(false)
            withContext(Dispatchers.Main){
                viewModel._customerList.postValue(cList)
            }
        }
    }

    private fun searchCustomer(){
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    binding.noRequest.visibility=View.GONE
                    val search = newText.toLowerCase(Locale.US)
                    arrayList.forEach {
                        if (it.customerDetails.firstName?.toLowerCase(Locale.US)?.contains(search) == true) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()){
                        binding.noRequest.visibility=View.VISIBLE
                    }else{
                        binding.noRequest.visibility=View.GONE
                    }
                    binding.rvIncomplete.adapter?.notifyDataSetChanged()
                } else {
                    binding.noRequest.visibility=View.GONE
                    displayList.clear()
                    displayList.addAll(arrayList)
                    binding.rvIncomplete.adapter?.notifyDataSetChanged()
                }
                return true
            }
        })

    }
    override fun onItemSelected(pos: Int, items: CusomerDetailsEntityWithList) {
        Log.d("TAG", "onItemSelected:${items.customerDetails.lastStep} ")
        viewModel.customerEntityData.postValue(items.customerDetails)
        viewModel.cIdNumber.postValue(items.customerDetails.nationalIdentity)
        viewModel.getCustomerEntityData.postValue(items.customerDetails)
        if (!Constants.isDelete){
            Constants.isDelete=false
            when(items.customerDetails.lastStep){
                "CustomerDetailsFragment"->{
                    viewModel.cIdNumber.postValue(items.customerDetails.nationalIdentity)
                val directions=IncompleteRegDashboardFragmentDirections.actionIncompleteRegDashboardFragmentToOnboardCustomerDetailsFragment(3)
                findNavController().navigate(directions)
                }
                "CustomerAdditionalDetailsFragment"->{
                    findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_customerAdditionalDetailsFragment)
                }
                "BusinessDetailsFragment"->{
                    findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_businesDetailsFragment)
                }
                "BusinessAddressFragment"->{
                    findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_businessAddressFragment)
                }
                "CollateralsFragment"->{
                    findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_collateralsFragment)
                }
                "ResidentialDetailsFragment"->{
                    findNavController().navigate(R.id.action_incompleteRegDashboardFragment_to_residentialDetailsFragment)
                }
                "GuarantorsFragment"->{
                    findNavController().navigate(R.id.guarantorsFragment)
                }
                "OtherBorrowingsFragment"->{
                    findNavController().navigate(R.id.otherBorrowingsFragment)
                }
                "NextOfKinFragment"->{
                    findNavController().navigate(R.id.nextOfKinFragment)
                }
                /**is there a need to have summary here or this shud be sync on the background?*/
                "AddHouseholdMembersFragment"->{
                    findNavController().navigate(R.id.addHouseholdMembersFragment)
                }
                "AddIncomeFragment"->{
                    findNavController().navigate(R.id.addIncomeFragment)
                }
                "AddExpensesFragment"->{
                    findNavController().navigate(R.id.addExpensesFragment)
                }

            }

        }else{
            Constants.isDelete=true
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete ${items.customerDetails.firstName} Record?")
            builder.setMessage("You are about to delete the OnBoarding Process.Please note this action cannot be undone. Do you wish to continue?")
            builder.setPositiveButton("YES") { _, _ ->
                //GlobalScope.launch(Dispatchers.IO) {
                    items.customerDocs.forEach { uploadedDoc ->
                        Log.d("TAG", "uploadedDoc: $uploadedDoc")
                        deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    }
                //}
                viewModel.deleteCustomerD(items.customerDetails)
                viewModel.removeRegDataAtPos(pos)
                incompleteAdapter.notifyDataSetChanged()
            }

            builder.setNegativeButton("NO") { _, _ ->

            }

            builder.show()
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val customerDetailsDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).customerDetailsDao()
        repository= CustomerDetailsRepository(customerDetailsDao)
    }

}