package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinessAddressBinding
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Step3BusinessAddressFragment : BaseDaggerFragment() {
    private lateinit var binding:FragmentBusinessAddressBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var cityId =""
    private var districtId =""
    private var phoneNumber=""
    private var guarantor= arrayListOf<Guarantor>()
    private var collateral= arrayListOf<Collateral>()
    private var otherBorrowing= arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private  var customerDocs :ArrayList<CustomerDocsEntity>  = arrayListOf()
    private var nationaid = ""
    private lateinit var repo:DropdownItemRepository

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
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
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }


        binding.apply {
            binding.ivBack.setOnClickListener {
                if (fromSummary == 2){
                    findNavController().navigate(R.id.summaryFragment)
                }else{
                    fromSummary=-1
                    findNavController().navigate(R.id.action_businessAddressFragment_to_businesDetailsFragment)
                }

            }
            dropdownItemsViewModel.getAllDistrict().observe(viewLifecycleOwner){dList->
                Log.d("TAG", "onViewCreated:dList${dList} ")
                populateDistrict(dList)
            }
            GlobalScope.launch(Dispatchers.IO) {
                val villageList= repo.getVillages()
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "onViewCreated:vgetVillages${villageList} ")
                }
            }
            btnContinue.setOnClickListener {
                val pAddress = etPAddress.text.toString()
                val employee = etnoEmp.text.toString()
                val townN = spinnerCity.text.toString()
                val province = etAccommodationStatus.text.toString()
                val validMsg = FieldValidators.VALIDINPUT
                phoneNumber =
                    FieldValidators().formatPhoneNumber(binding.etBPhone.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                if (pAddress.isEmpty()) {
                    tlPAdress.error = "Required"
                } else if (province.isEmpty()) {
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
                    viewmodel.customerEntityData.observe(viewLifecycleOwner){customerDetailsEntity->
                        customerDetailsEntity.apply {
                            lastStep = "BusinessAddressFragment"
                            isComplete = false
                            isProcessed = false
                            hasFinished = false
                            bsPhysicalAddress = etPAddress.text.toString()
                            bsDistrictId = districtId
                            bsDistrict = etAccommodationStatus.text.toString()
                            bsVillage = spinnerCity.text.toString()
                            bsVillageId = cityId
                            bsPhoneNumber = phoneNumber
                            bsNumberOfEmployees = etnoEmp.text.toString()
                            viewmodel.customerEntityData.postValue(customerDetailsEntity)
                                saveCustomerFullDatLocally(customerDetailsEntity)
                            }

                    }
                    if (fromSummary == 2) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary =-1
                        findNavController().navigate(R.id.action_businessAddressFragment_to_collateralsFragment)
                    }
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
    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity){
        viewmodel.insertCustomerFullDetails(customerDetailsEntity,guarantor, collateral, otherBorrowing,household,customerDocs)
    }
    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoom: ${Gson().toJson(it)}")
            binding.apply {
                if (it.customerDetails.bsDistrict.isNotEmpty()) {
                    etAccommodationStatus.setText(it.customerDetails.bsDistrict)
                }
                if (it.customerDetails.bsVillage.isNotEmpty()) {
                    spinnerCity.setText(it.customerDetails.bsVillage)
                }
                if (it.customerDetails.bsPhysicalAddress.isNotEmpty()) {
                    etPAddress.setText(it.customerDetails.bsPhysicalAddress)
                }
                if (it.customerDetails.bsPhoneNumber.isNotEmpty()) {
                    etBPhone.setText(it.customerDetails.bsPhoneNumber)
                    phoneNumber=it.customerDetails.bsPhoneNumber
                }
                if (it.customerDetails.bsNumberOfEmployees.isNotEmpty()) {
                    etnoEmp.setText(it.customerDetails.bsNumberOfEmployees)
                }
                if (it.customerDetails.bsDistrictId.isNotEmpty()) {
                    districtId=(it.customerDetails.bsDistrictId)
                    getVillages(it.customerDetails.bsDistrictId)
                }
                if (it.customerDetails.bsVillageId.isNotEmpty()) {
                    cityId=(it.customerDetails.bsVillageId)
                }
                guarantor.clear()
                guarantor.addAll(it.guarantors)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.otherBorrowing)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral.clear()
                collateral.addAll(it.collateral)
                Log.e("TAG", "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}")
            }
        }

    }
    private fun getVillages(provinceId:String){
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("TAG", "regionId:1 $provinceId")
            val villageList= repo.getVillagesWithID(provinceId)
            withContext(Dispatchers.Main) {
                Log.d("TAG", "onViewCreated:vgetVillages2${villageList} ")
                if (villageList.isEmpty()){
                    binding.tlCity.makeGone()
                }else{
                    binding.tlCity.makeVisible()
                    populateCity(villageList)
                }

            }
        }
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    if (fromSummary == 2){
                        findNavController().navigate(R.id.summaryFragment)
                    }else{
                        fromSummary=-1
                        findNavController().navigate(R.id.action_businessAddressFragment_to_businesDetailsFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val dropdownItemDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo= DropdownItemRepository(dropdownItemDao)
    }




}