package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.AssessHouseHoldAdapter
import com.deefrent.rnd.fieldapp.databinding.AddHouseholdMemberDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentHouseholdInfoBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessMemberCallBack
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson


class AssessHouseholdMembersFragment : Fragment(), AssessMemberCallBack {
    private lateinit var binding: FragmentHouseholdInfoBinding
    private lateinit var cardBinding: AddHouseholdMemberDialogBinding
    private lateinit var assessMember:AssessHouseHoldAdapter
    private lateinit var guarantor:List<AssessGuarantor>
    private lateinit var borrowings:List<AssessBorrowing>
    private lateinit var collateral:List<AssessCollateral>
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private  var customerDocs : ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private var rshipId = ""
    private var occupationId = ""
    private var nationalId = ""
    private var isDeleteItems: Boolean = false
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseholdInfoBinding.inflate(layoutInflater)
        binding.note.makeVisible()
        binding.note.text ="Note: You must have a minimum of 1 household member to continue"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.apply {
            header.text = "Add Family Members"
            assessMember = AssessHouseHoldAdapter(household, this@AssessHouseholdMembersFragment)
            rvMember.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvMember.adapter = assessMember
            viewmodel.parentId.observe(viewLifecycleOwner) { Id ->
                nationalId = Id
                fetchSavedItemFromRoom(Id)
            }


            btnAddMember.setOnClickListener {
                addMember()
            }
            btnContinue.setOnClickListener {
                if (household.isEmpty()) {
                    toastyErrors("Add household Members to continue")
                } else {
                    isDeleteItems=true
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner){detailsEntity->
                        detailsEntity.apply {
                           lastStep = "AssessHouseholdMembersFragment"
                          isComplete = false
                            hasFinished = false
                            isProcessed = false
                            Log.d("TAG", "onOtherBorrowing: $household")
                           saveAssessmentDataLocally(detailsEntity,household)
                           viewmodel.assessCustomerEntity.postValue(detailsEntity)
                          //  Log.d("TAG", "assessCustomerEntity: ${Gson().toJson(customerDetailsEntity)}")
                        }
                    }
                    findNavController().navigate(R.id.action_assessHouseholdMembersFragment_to_assessIncomeFragment)
                }
            }
         ivBack.setOnClickListener {
             findNavController() .navigate(R.id.assessNextOfKinFragment)
            }

        }
    }


    private fun addMember() {
        cardBinding = AddHouseholdMemberDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { aList ->
                Log.d("TAG", "addCollateral: $aList")
                populateRship(aList)
            }
            dropdownItemsViewModel.getOccupation().observe(viewLifecycleOwner) { occ ->
                Log.d("TAG", "addMember: $occ")
                populateOccupation(occ)
            }
            btnContinue.setOnClickListener {
                val fullName = etname.text.toString()
                val natureofactivity = etDSale.text.toString()
                val income = etIncome.text.toString()
                val rship = spinnerIsource.text.toString()
                val occ = spinneroccupation.text.toString()
                if (rship.isEmpty()) {
                    toastyErrors("Select relationship")
                } else
                    if (fullName.isEmpty()) {
                        tlname.error = getString(R.string.required)
                    } else if (occ.isEmpty()) {
                        toastyErrors("Select occupation")
                    } else if (natureofactivity.isEmpty()) {
                        tlDSale.error = getString(R.string.required)
                    } else if (income.isEmpty()) {
                        tlDSale.error = ""
                        tlIcome.error = getString(R.string.required)
                    } else {
                        tlDSale.error = ""
                        tlname.error = ""
                        tlIcome.error = ""
                        val hoseholdMembers = AssessHouseholdMemberEntity(0,nationalId,etname.text.toString(),etIncome.text.toString(),
                            etDSale.text.toString(),spinneroccupation.text.toString(),occupationId,spinnerIsource.text.toString(),rshipId)
                        household.add(0, hoseholdMembers)
                      assessMember.notifyItemInserted(0)
                        dialog.dismiss()
                    }
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }

    private fun populateRship(rship: List<RshipTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        cardBinding.spinnerIsource.setAdapter(typeAdapter)
        cardBinding.spinnerIsource.keyListener = null
        cardBinding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: RshipTypeEntity = parent.adapter.getItem(position) as RshipTypeEntity
            cardBinding.spinnerIsource.setText(selected.name,false)
            rshipId = selected.id.toString()
        }
    }

    private fun populateOccupation(occupation: List<OccupationEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, occupation)
        cardBinding.spinneroccupation.setAdapter(typeAdapter)
        cardBinding.spinneroccupation.keyListener = null
        cardBinding.spinneroccupation.setOnItemClickListener { parent, _, position, _ ->
            val selected: OccupationEntity =
                parent.adapter.getItem(position) as OccupationEntity
            cardBinding.spinneroccupation.setText(selected.name,false)
            occupationId = selected.id.toString()
        }
    }
    override fun onItemSelected(pos: Int, lists: AssessHouseholdMemberEntity) {
        cardBinding = AddHouseholdMemberDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { aList ->
                Log.d("TAG", "addCollateral: $aList")
                populateRship(aList)
            }
            dropdownItemsViewModel.getOccupation().observe(viewLifecycleOwner) { occ ->
                Log.d("TAG", "addMember: $occ")
                populateOccupation(occ)
            }
            spinnerIsource.setText(lists.relationship,false)
            rshipId=lists.relationshipId
            spinneroccupation.setText(lists.occupation,false)
            occupationId=lists.occupationId
            etname.setText(lists.fullName)
            etDSale.setText(lists.natureOfActivity)
            etIncome.setText(lists.incomeOrFeesPaid)
            btnContinue.makeGone()
            clButtons.makeVisible()
            btnCancel.setOnClickListener {
                if (isDeleteItems){
                    viewmodel.deleteHMemberID(lists.id)
                }else{
                    isDeleteItems=false
                    household.removeAt(pos)
                    assessMember.notifyItemRemoved(pos)
                    Log.e("TAG", "isDeletitems: ${household.size}", )

                }
                if (household.size==0){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }

                dialog.dismiss()
            }
            btnContinue.setOnClickListener {
                val fullName = etname.text.toString()
                val natureofactivity = etDSale.text.toString()
                val income = etIncome.text.toString()
                val rship = spinnerIsource.text.toString()
                val occ = spinneroccupation.text.toString()
                if (rship.isEmpty()) {
                    toastyErrors("Select relationship")
                } else
                    if (fullName.isEmpty()) {
                        tlname.error = getString(R.string.required)
                    } else if (occ.isEmpty()) {
                        toastyErrors("Select occupation")
                    } else if (natureofactivity.isEmpty()) {
                        tlDSale.error = getString(R.string.required)
                    } else if (income.isEmpty()) {
                        tlDSale.error = ""
                        tlIcome.error = getString(R.string.required)
                    } else {
                        tlDSale.error = ""
                        tlname.error = ""
                        tlIcome.error = ""
                        lists.relationshipId=rshipId
                        lists.relationship=spinnerIsource.text.toString()
                        lists.fullName=fullName
                        lists.occupationId=occupationId
                        lists.occupation=spinneroccupation.text.toString()
                        lists.natureOfActivity=natureofactivity
                        lists.incomeOrFeesPaid=income
                        viewmodel.updateHMember(lists)
                        assessMember.notifyItemChanged(pos)
                        dialog.dismiss()
                    }
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }
    private fun saveAssessmentDataLocally(
        assessCustomerEntity: AssessCustomerEntity,
        hohold: List<AssessHouseholdMemberEntity>
    ) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity,customerDocs,
            collateral,
            guarantor,
            borrowings, hohold
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.assessNextOfKinFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun fetchSavedItemFromRoom(parentId: String) {
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}",)
                household.clear()
                household.addAll(it.householdMember)
                assessMember.notifyDataSetChanged()
                collateral = it.assessCollateral
                Log.e("TAG", "assessCollateral: $collateral",)
                guarantor = it.assessGua
                borrowings = it.assessBorrow
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)

            }

        }
    }



}