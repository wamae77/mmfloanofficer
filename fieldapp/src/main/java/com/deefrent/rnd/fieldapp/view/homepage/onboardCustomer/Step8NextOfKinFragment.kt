package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentNextOfKinBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.utils.FieldValidators
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.gson.Gson
import javax.inject.Inject

class Step8NextOfKinFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentNextOfKinBinding
    private var rshipId = ""
    private var phoneNumber = ""
    private var idTypeId = ""
    private lateinit var guarantor: List<Guarantor>
    private lateinit var borrowings: List<OtherBorrowing>
    private lateinit var collateral: List<Collateral>
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs = arrayListOf<CustomerDocsEntity>()
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNextOfKinBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.cIdNumber.observe(viewLifecycleOwner) {
            getSavedItemsFromRoom(it)
        }
        binding.apply {
            binding.ivBack.setOnClickListener {
                if (fromSummary == 4) {
                    findNavController().navigate(R.id.summaryFragment)
                } else {
                    findNavController().navigate(R.id.action_nextOfKinFragment_to_otherBorrowingsFragment)
                }

            }
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { rship ->
                populateRship(rship)
            }
            dropdownItemsViewModel.getAllIDTypes().observe(viewLifecycleOwner) { idType ->
                populateIdentityType(idType)
            }
            handleBackButton()
            /* if (Constants.fromSummary==5){
                 etBAddress.setText(Constants.kinFname)
                 etDSale.setText(Constants.kinsname)
                 etGuarantorAddress.setText(Constants.kinID)
                 etYear.setText(Constants.kinPhone)
             }else{
                 etBAddress.setText("")
                 etDSale.setText("")
                 etGuarantorAddress.setText("")
                 etYear.setText("")
             }*/
            binding.etGuarantorAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tiGuarantorAddress.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tiGuarantorAddress.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    binding.tiGuarantorAddress.error = null
                }
            })

            btnContinue.setOnClickListener {
                val validMsg = FieldValidators.VALIDINPUT
                phoneNumber =
                    FieldValidators().formatPhoneNumber(binding.etYear.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)

                val names = etBAddress.text.toString()
                val rship = spinnerIsource.text.toString()
                val spID = spinnerID.text.toString()
                val surname = etDSale.text.toString()
                val kinId = etGuarantorAddress.text.toString()
                if (rship.isEmpty()) {
                    toastyErrors("Select relationship")
                } else if (names.isEmpty()) {
                    tlBAddress.error = "Required"
                } else if (surname.isEmpty()) {
                    tlDSale.error = "Required"
                } else if (!validPhone.contentEquals(validMsg)) {
                    etYear.requestFocus()
                    tlYear.error = validPhone
                } else if (spID.isEmpty()) {
                    toastyErrors("Select Identity type")
                } else if (kinId.length < 11) {
                    tiGuarantorAddress.error = "Please provide a valid ID Number"
                } else {
                    tlBAddress.error = ""
                    tlDSale.error = ""
                    tlYear.error = ""
                    tiGuarantorAddress.error = ""
                    viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                        customerDetailsEntity.apply {
                            lastStep = "NextOfKinFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            kinRelationshipId = rshipId
                            kinRelationship = spinnerIsource.text.toString()
                            kinIdentityType = spinnerID.text.toString()
                            kinFirstName = etBAddress.text.toString()
                            kinLastName = etDSale.text.toString()
                            kinPhoneNumber = phoneNumber
                            kinIdentityTypeId = idTypeId
                            kinIdNumber = etGuarantorAddress.text.toString()
                            viewmodel.customerEntityData.postValue(customerDetailsEntity)
                            saveCustomerFullDatLocally(customerDetailsEntity)
                        }

                    }

                    if (fromSummary == 4) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        findNavController().navigate(R.id.action_nextOfKinFragment_to_addHouseholdMembersFragment)
                    }


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
            binding.spinnerIsource.setText(selected.name, false)
            rshipId = selected.id.toString()
        }
    }

    private fun populateIdentityType(idType: List<IdentityTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idType)
        binding.spinnerID.setAdapter(typeAdapter)
        binding.spinnerID.keyListener = null
        binding.spinnerID.setOnItemClickListener { parent, _, position, _ ->
            val selected: IdentityTypeEntity =
                parent.adapter.getItem(position) as IdentityTypeEntity
            binding.spinnerID.setText(selected.name, false)
            idTypeId = selected.id.toString()
        }
    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            borrowings,
            household,
            customerDocs
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (fromSummary == 4) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_nextOfKinFragment_to_otherBorrowingsFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoom: ${Gson().toJson(it)}")
            binding.apply {
                guarantor = (it.guarantors)
                borrowings = (it.otherBorrowing)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral = (it.collateral)
                binding.apply {
                    if (it.customerDetails.kinRelationship.isNotEmpty()) {
                        spinnerIsource.setText(it.customerDetails.kinRelationship, false)
                    }
                    if (it.customerDetails.kinFirstName.isNotEmpty()) {
                        etBAddress.setText(it.customerDetails.kinFirstName)
                    }
                    if (it.customerDetails.kinLastName.isNotEmpty()) {
                        etDSale.setText(it.customerDetails.kinLastName)
                    }
                    if (it.customerDetails.kinPhoneNumber.isNotEmpty()) {
                        etYear.setText(it.customerDetails.kinPhoneNumber)
                        phoneNumber = (it.customerDetails.kinPhoneNumber)
                    }
                    if (it.customerDetails.kinIdentityType.isNotEmpty()) {
                        spinnerID.setText(it.customerDetails.kinIdentityType, false)
                    }
                    if (it.customerDetails.kinIdNumber.isNotEmpty()) {
                        etGuarantorAddress.setText(it.customerDetails.kinIdNumber)
                    }
                    if (it.customerDetails.kinRelationshipId.isNotEmpty()) {
                        rshipId = (it.customerDetails.kinRelationshipId)
                    }
                    if (it.customerDetails.kinIdentityTypeId.isNotEmpty()) {
                        rshipId = (it.customerDetails.kinIdentityTypeId)
                    }
                }
            }
        }
    }
}