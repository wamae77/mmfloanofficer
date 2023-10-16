package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.LocalAsessmentAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLocalAssessmetBinding
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntityWithList
import com.deefrent.rnd.fieldapp.room.repos.AssessCustomerRepository
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessmentEntityCallBack
import com.deefrent.rnd.fieldapp.utils.deleteImageFromInternalStorage
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class LocalAssessmetFragment : Fragment(), AssessmentEntityCallBack {
    private lateinit var binding: FragmentLocalAssessmetBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private lateinit var incompleteAdapter: LocalAsessmentAdapter
    private var arrayList:ArrayList<AssessCustomerEntityWithList> = arrayListOf()
    private var displayList:ArrayList<AssessCustomerEntityWithList> = arrayListOf()
    private lateinit var assrep: AssessCustomerRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLocalAssessmetBinding.inflate(layoutInflater)
        incompleteAdapter = LocalAsessmentAdapter(displayList,this)
        binding.rvAssesIncomplete.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvAssesIncomplete.adapter = incompleteAdapter
        searchCustomer()
        // SetOnRefreshListener on SwipeRefreshLayout
        binding.svLocalAssessment.setOnRefreshListener(OnRefreshListener {
            getLocalCustomerAssessmentDetails()
        })
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val assessCustomerDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).assessCustomerDao()
        assrep= AssessCustomerRepository(assessCustomerDao)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel._customerAssessList.observe(viewLifecycleOwner){
            displayList.clear()
            arrayList.clear()
            displayList.addAll(it)
            incompleteAdapter.notifyDataSetChanged()
            arrayList.addAll(it)
            if (it.isEmpty()){
                binding.svLocalAssessment.isRefreshing=false
                binding.noNewRequest.makeVisible()
                binding.search.makeGone()
                binding.rvAssesIncomplete.makeGone()
            }else{
                binding.svLocalAssessment.isRefreshing=false
                binding.noNewRequest.makeGone()
                binding.search.makeVisible()
                binding.rvAssesIncomplete.makeVisible()
            }
        }


    }

    private fun getLocalCustomerAssessmentDetails(){
        GlobalScope.launch(Dispatchers.IO) {
            val assessList= assrep.getIncompleteAssessed(false)
            withContext(Dispatchers.Main){
                viewmodel._customerAssessList.postValue(assessList)
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
                        if (it.assessCustomerEntity.firstName.lowercase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()){
                        binding.noRequest.visibility=View.VISIBLE
                    }else{
                        binding.noRequest.visibility=View.GONE
                    }
                    binding.rvAssesIncomplete.adapter?.notifyDataSetChanged()
                } else {
                    binding.noRequest.visibility=View.GONE
                    displayList.clear()
                    displayList.addAll(arrayList)
                    binding.rvAssesIncomplete.adapter?.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    override fun onItemSelected(pos: Int, listItems: AssessCustomerEntityWithList) {
        Log.e("TAG", "onItemSelected444444444: ${Gson().toJson(listItems.assessCustomerEntity)}")
        Constants.isFromLocal=true
        viewmodel.assessCustomerEntity.postValue(listItems.assessCustomerEntity)
        viewmodel.parentId.postValue(listItems.assessCustomerEntity.idNumber)
        if (!Constants.isDelete){
            Constants.isDelete=false
            when(listItems.assessCustomerEntity.lastStep){
                "AssessCustomerDetailsFragment"->{
                    findNavController().navigate(R.id.action_incompleteAssesmentFragment_to_assessCustomerDetailsFragment)
                }
                "AssessAdditionalDetailsFragment"->{
                    findNavController().navigate(R.id.assessAdditionalDetailsFragment)
                }
                "AssessBusinesDetailsFragment"->{
                    findNavController().navigate(R.id.assessBusinesDetailsFragment)
                }
                "AssessBusinessAddressFragment"->{
                    findNavController().navigate(R.id.assessBusinessAddressFragment)
                }
                "AssessCollateralsFragment"->{
                    viewmodel.assessCollateral.postValue(listItems.assessCollateral)
                    findNavController().navigate(R.id.assessCollateralsFragment)
                }
                "AssessResidentialDetailsFragment"->{
                    viewmodel.assessCollateral.postValue(listItems.assessCollateral)
                    findNavController().navigate(R.id.assessResidentialDetailsFragment)
                }
                "AssessGuarantorsFragment"->{
                    findNavController().navigate(R.id.assessGuarantorsFragment)
                }
                "AssessBorrowingsFragment"->{
                    findNavController().navigate(R.id.assessBorrowingsFragment)
                }
                "AssessNextOfKinFragment"->{
                    findNavController().navigate(R.id.assessNextOfKinFragment)
                }
                "AssessHouseholdMembersFragment"->{
                    findNavController().navigate(R.id.assessHouseholdMembersFragment)
                }
                "AssessIncomeFragment"->{
                    findNavController().navigate(R.id.assessIncomeFragment)
                }
                "AssessExpensesFragment"->{
                    findNavController().navigate(R.id.assessExpensesFragment)
                }
            }
        }else{
            Constants.isDelete=true
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete ${listItems.assessCustomerEntity.firstName} Record?")
            builder.setMessage("You are about to delete the Assessment Process. Please note this action cannot be undone.Do you wish to continue?")

            builder.setPositiveButton("YES") { _, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    listItems.customerDocs.forEach { uploadedDoc ->
                        Log.d("TAG", "uploadedDoc: $uploadedDoc")
                        deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                        viewmodel.deleteAssessedCustomer(listItems.assessCustomerEntity)
                    }
                }
                viewmodel.removeAssessesDataAtPos(pos)
               // displayList.removeAt(pos)
               // incompleteAdapter.notifyItemRemoved(pos)
                /**notify adapter more than one items has been changed*/
               // incompleteAdapter.notifyItemRangeChanged(pos,incompleteAdapter.itemCount)
            }

            builder.setNegativeButton("NO") { _, _ ->

            }

            builder.show()
        }
    }


}
