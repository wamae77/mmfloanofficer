package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinessAddressBinding
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AssessBusinessAddressFragment :Fragment() {
    private lateinit var binding:FragmentBusinessAddressBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var cityId =""
    private var districtId =""
    private var phoneNumber=""
    private var guarantor = arrayListOf<AssessGuarantor>()
    private var collateral = arrayListOf<AssessCollateral>()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var customerDocs = arrayListOf<AssessCustomerDocsEntity>()
    private lateinit var repo:DropdownItemRepository
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBusinessAddressBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.apply {
            viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
                getAssessmentFromRoom(nationalId)
            }
            binding.ivBack.setOnClickListener {
                findNavController().navigate(R.id.assessBusinesDetailsFragment)
            }

            dropdownItemsViewModel.getAllDistrict().observe(viewLifecycleOwner){dList->
                Log.d("TAG", "onViewCreated:dList${dList} ")
                populateDistrict(dList)
            }
            btnContinue.setOnClickListener {
                val pAddress = etPAddress.text.toString()
                val employee = etnoEmp.text.toString()
                val townN = spinnerCity.text.toString()
                val dName = etAccommodationStatus.text.toString()
                val validMsg = FieldValidators.VALIDINPUT
                phoneNumber =
                    FieldValidators().formatPhoneNumber(binding.etBPhone.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                if (pAddress.isEmpty()) {
                    tlPAdress.error = "Required"
                } else if (dName.isEmpty()) {
                    toastyErrors("Select Province")
                } else if (binding.tlCity.isVisible && binding.spinnerCity.text.isEmpty()){
                    toastyErrors("Select Town/City")
                } else if (!validPhone.contentEquals(validMsg)) {
                    etBPhone.requestFocus()
                    tlbPhone.error = validPhone
                } else if (employee.isEmpty()) {
                    tlNoEmp.error = "Required"
                }else {
                    tlbPhone.error=""
                    tlNoEmp.error=""
                    tlCity.error=""
                    tlPAdress.error=""
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner){detailsEntity->
                        detailsEntity.apply {
                           lastStep = "AssessBusinessAddressFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            businessPhysicalAddress = etPAddress.text.toString()
                            businessDistrictId = districtId
                            businessDistrictName =etAccommodationStatus.text.toString()
                            businessVillageName = spinnerCity.text.toString()
                            businessVillageId = cityId
                            businessPhone = phoneNumber
                            numberOfEmployees = etnoEmp.text.toString()
                            viewmodel.assessCustomerEntity.postValue(detailsEntity)
                             saveAssessmentDataLocally(detailsEntity)
                            }
                    }
                    findNavController().navigate(R.id.assessCollateralsFragment)

                }
            }
        }
    }
    private fun populateDistrict(district: List<DistrictEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, district)
        binding.etAccommodationStatus.setAdapter(typeAdapter)
        binding.etAccommodationStatus.keyListener = null
        binding.etAccommodationStatus.setOnItemClickListener { parent, _, position, _ ->
            hideKeyboard()
            val selected: DistrictEntity = parent.adapter.getItem(position) as DistrictEntity
            binding.etAccommodationStatus.setText(selected.name,false)
            districtId= selected.id.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val villageList= repo.getVillagesWithID(selected.id.toString())
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "onViewCreated:villageList${villageList} ")
                    if (villageList.isEmpty()){
                        binding.tlCity.makeGone()
                    }else{
                        binding.tlCity.makeVisible()
                        populateCity(villageList)
                      //  binding.spinnerCity.setText(villageList[0].name)
                      //  cityId=villageList[0].id
                    }


                }
            }
        }
    }
    private fun populateCity(district: List<VillageEntity>) {
        binding.spinnerCity.setText("")
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, district)
        binding.spinnerCity.setAdapter(typeAdapter)
        binding.spinnerCity.keyListener = null
        binding.spinnerCity.setOnItemClickListener { parent, _, position, _ ->
            val selected: VillageEntity = parent.adapter.getItem(position) as VillageEntity
            binding.spinnerCity.setText(selected.name,false)
            cityId= selected.id.toString()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val dropdownItemDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo= DropdownItemRepository(dropdownItemDao)
    }
    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity,customerDocs,
            collateral,
            guarantor,
            otherBorrowing,household
        )
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.assessBusinesDetailsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getAssessmentFromRoom(parentId:String){
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}",)
                guarantor.clear()
                guarantor.addAll(it.assessGua)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.assessBorrow)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral.clear()
                collateral.addAll(it.assessCollateral)
                districtId= it.assessCustomerEntity.businessDistrictId.toString()
                cityId= it.assessCustomerEntity.businessVillageId.toString()
                etPAddress.setText(it.assessCustomerEntity.businessPhysicalAddress)
                etAccommodationStatus.setText(it.assessCustomerEntity.businessDistrictName,false)
                spinnerCity.setText(it.assessCustomerEntity.businessVillageName,false)
                etBPhone.setText(it.assessCustomerEntity.businessPhone)
                etnoEmp.setText(it.assessCustomerEntity.numberOfEmployees)

            }

        }
    }




}