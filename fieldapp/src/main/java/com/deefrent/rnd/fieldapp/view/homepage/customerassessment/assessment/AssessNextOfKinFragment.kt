package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentNextOfKinBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.FieldValidators
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.gson.Gson

class AssessNextOfKinFragment : Fragment() {
    private lateinit var binding:FragmentNextOfKinBinding
    private var rshipId=""
    private var idTypeId=""
    private lateinit var guarantor:List<AssessGuarantor>
    private lateinit var borrowings:List<AssessBorrowing>
    private lateinit var collateral:List<AssessCollateral>
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private  var customerDocs : ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNextOfKinBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
            getSavedItemFromRoom(nationalId)
        }

        binding.apply {
            binding.ivBack.setOnClickListener {
                findNavController().navigate(R.id.assessBorrowingsFragment)
            }
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner){rship->
                populateRship(rship)
            }
            dropdownItemsViewModel.getAllIDTypes().observe(viewLifecycleOwner){idType->
                populateIdentityType(idType)
            }
            binding.etGuarantorAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tiGuarantorAddress.error=null
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tiGuarantorAddress.error=null
                }
                override fun afterTextChanged(p0: Editable?) {
                    binding.tiGuarantorAddress.error=null
                }
            })

            btnContinue.setOnClickListener {
                val validMsg = FieldValidators.VALIDINPUT
             val   phoneNumber =
                    FieldValidators().formatPhoneNumber(binding.etYear.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)

                val names=etBAddress.text.toString()
                val rship=spinnerIsource.text.toString()
                val sId=spinnerID.text.toString()
                val surname=etDSale.text.toString()
                val kinId=etGuarantorAddress.text.toString()
                if (rship.isEmpty()){
                    toastyErrors("Select relationship")
                }else if (names.isEmpty()){
                    tlBAddress.error="Required"
                }else if (surname.isEmpty()){
                    tlDSale.error="Required"
                }else if (!validPhone.contentEquals(validMsg)) {
                    etYear.requestFocus()
                    tlYear.error = validPhone
                }else if (sId.isEmpty()){
                    toastyErrors("Select Identity type")
                }else if (kinId.length<11){
                    tiGuarantorAddress.error="Required"
                }else{
                    tlBAddress.error=""
                    tlDSale.error=""
                    tlYear.error=""
                    tiGuarantorAddress.error=""
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner){detailsEntity->
                        detailsEntity.apply {
                           lastStep = "AssessNextOfKinFragment"
                           isComplete = false
                            hasFinished = false
                            isProcessed = false
                           kinRelationshipId = rshipId.toString()
                            kinRelationship=spinnerIsource.text.toString()
                            kinIdentityType=etGuarantorAddress.text.toString()
                            kinFirstName=etBAddress.text.toString()
                            kinLastName=etDSale.text.toString()
                            kinPhoneNumber=phoneNumber
                            kinIdentityTypeId= idTypeId.toString()
                            kinIdNumber=etGuarantorAddress.text.toString()
                            viewmodel.assessCustomerEntity.postValue(detailsEntity)
                          saveAssessmentDataLocally(detailsEntity)
                        }

                    }
                    findNavController().navigate(R.id.action_assessNokFragment_to_assesshouseholeMemberragment)
                }
            }
        }
    }
    private fun populateRship(rship: List<RshipTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        binding.spinnerIsource.setAdapter(typeAdapter)
        binding.spinnerIsource.keyListener = null
        binding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: RshipTypeEntity = parent.adapter.getItem(position) as RshipTypeEntity
            binding.spinnerIsource.setText(selected.name,false)
            rshipId= selected.id.toString()
        }
    }
    private fun populateIdentityType(idType: List<IdentityTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idType)
        binding.spinnerID.setAdapter(typeAdapter)
        binding.spinnerID.keyListener = null
        binding.spinnerID.setOnItemClickListener { parent, _, position, _ ->
            val selected: IdentityTypeEntity = parent.adapter.getItem(position) as IdentityTypeEntity
            binding.spinnerID.setText(selected.name,false)
            idTypeId= selected.id.toString()
        }
    }
    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity,customerDocs,
            collateral,
            guarantor,
            borrowings,household
        )
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.assessBorrowingsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getSavedItemFromRoom(parentId:String){
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}",)
                collateral = it.assessCollateral
                Log.e("TAG", "assessCollateral: $collateral",)
                guarantor = it.assessGua
                borrowings = it.assessBorrow
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                household.clear()
                household.addAll(it.householdMember)
                binding.apply {
                    rshipId=it.assessCustomerEntity.kinRelationshipId
                    idTypeId=it.assessCustomerEntity.kinIdentityTypeId.toString()
                    spinnerIsource.setText(it.assessCustomerEntity.kinRelationship,false)
                    etBAddress.setText(it.assessCustomerEntity.kinFirstName)
                    etDSale.setText(it.assessCustomerEntity.kinLastName)
                    etYear.setText(it.assessCustomerEntity.kinPhoneNumber)
                    spinnerID.setText(it.assessCustomerEntity.kinIdentityType,false)
                    etGuarantorAddress.setText(it.assessCustomerEntity.kinIdNumber)
                }

            }

        }
    }




}