package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.*
import com.deefrent.rnd.fieldapp.databinding.AddHouseholdMemberDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentHouseholdInfoBinding
import com.deefrent.rnd.fieldapp.dtos.AddHouseHoldMember
import com.deefrent.rnd.fieldapp.dtos.DeleteMemberDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateHouseholdMembersDTO
import com.deefrent.rnd.fieldapp.network.models.HouseholdMember
import com.deefrent.rnd.fieldapp.room.entities.OccupationEntity
import com.deefrent.rnd.fieldapp.room.entities.RshipTypeEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.HouseholdMembersCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class HouseholdInfoFragment : Fragment(),HouseholdMembersCallBack {
    private lateinit var binding: FragmentHouseholdInfoBinding
    private lateinit var cardBinding: AddHouseholdMemberDialogBinding
    private lateinit var gAdapter: HouseHoldInfoAdapter
    private val items: ArrayList<HouseholdMember> = ArrayList()
    private var itemsAdded: ArrayList<HouseholdMember> = ArrayList()
    private var rshipId =""
    private var occupationId =""
    private var deleteLocally =false
    private var id = ""
    private var members: List<AddHouseHoldMember.Member> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerAssessmentHomeViewModel::class.java)
    }
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseholdInfoBinding.inflate(layoutInflater)
        binding.apply {
            pb.makeGone()
            tvPbText.makeGone()
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
            gAdapter = HouseHoldInfoAdapter(items,this@HouseholdInfoFragment)
            rvMember.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvMember.adapter = gAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewmodel.iDLookUpData.observe(viewLifecycleOwner) {
                id = it?.idNumber.toString()
                if (it?.householdMembers?.isNotEmpty() == true) {
                    binding.btnContinue.text="Update Members"
                    binding.note.makeGone()
                    binding.rvMember.makeVisible()
                    Log.d("TAG", "oateView: $it")
                    items.clear()
                    items.addAll(it.householdMembers)
                    binding.rvMember.adapter?.notifyDataSetChanged()
                }else{
                    binding.btnContinue.text="Submit Members"
                    binding.note.makeVisible()
                }
            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())){
                    viewmodel.houseHoldList.observe(viewLifecycleOwner) { member ->
                        val listM = member.map {
                            AddHouseHoldMember.Member(
                                it.relationshipId,
                                it.fullName,
                                it.occupationId,
                                it.natureOfActivity,
                                it.incomeOrFeesPaid
                            )
                        }
                        members = listM
                    }
                    val addHouseholdMember = AddHouseHoldMember(id, members)
                    if (members.isEmpty()){
                        toastyErrors("Add family a new family members to update")
                    }else{
                        viewmodel.addMembers(addHouseholdMember)
                    }
                }else{
                    toastyErrors("Check your internet connection and try again")
                }
            }
            viewmodel.responseStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.btnContinue.isEnabled=false
                            binding.progressbar.tvWait.text="Updating family members details..."
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.btnContinue.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            binding.btnContinue.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            deleteLocally=false
                            viewmodel.houseHoldList.value= arrayListOf()
                            onInfoSuccessDialog("Family members added successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            viewmodel.stopObserving()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            btnAddMember.setOnClickListener {
                addMember()
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
                populateOccupation(occ)
            }
            btnContinue.setOnClickListener {
                val fullName = etname.text.toString()
                val natureofactivity = etDSale.text.toString()
                val income = etIncome.text.toString()
                val rship = spinnerIsource.text.toString()
                val occ = spinneroccupation.text.toString()
                 if (rship.isEmpty()){
                     toastyErrors("Select relationship")
                 }else
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
                    val hoseholdMembers=HouseholdMember(fullName,income,natureofactivity,spinneroccupation.text.toString(),occupationId,spinnerIsource.text.toString(),rshipId,0)
                    items.add(0,hoseholdMembers)
                    itemsAdded.add(hoseholdMembers)
                    gAdapter.notifyItemInserted(0)
                    deleteLocally=true
                    if (members.isEmpty()){
                        binding.note.makeVisible()
                    }else{
                        binding.note.makeGone()

                    }
                    viewmodel.houseHoldList.postValue(itemsAdded)
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
    override fun onItemSelected(pos: Int, item: HouseholdMember) {
        cardBinding = AddHouseholdMemberDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            clButtons.makeVisible()
            btnContinue.makeGone()
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { aList ->
                Log.d("TAG", "addCollateral: $aList")
                populateRship(aList)
            }
            dropdownItemsViewModel.getOccupation().observe(viewLifecycleOwner) { occ ->
                Log.d("TAG", "addMember: $occ")
                populateOccupation(occ)
            }
            etname.setText(item.fullName)
            etDSale.setText(item.natureOfActivity)
            etIncome.setText(FormatDigit.formatDigits(item.incomeOrFeesPaid))
            spinnerIsource.setText(item.relationShip,false)
            spinneroccupation.setText(item.occupation,false)
            btnCancel.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    if (!deleteLocally) {
                        deleteLocally = false
                        val deleteMemberDTO = DeleteMemberDTO()
                        deleteMemberDTO.memberId = item.memberId.toString()
                        viewmodel.deleteMember(deleteMemberDTO)
                    } else {
                        items.removeAt(pos)
                        gAdapter.notifyItemRemoved(pos)
                        Log.e("TAG", "isDeletitems: ${items.size}",)
                    }
                    /*if (household.size==0){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }*/

                    dialog.dismiss()
                }else{
                    onNoNetworkDialog(requireContext())
                }
            }

            btnUpdate.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val fullName = etname.text.toString()
                    val natureofactivity = etDSale.text.toString()
                    val income = etIncome.text.toString()
                    when {
                        spinnerIsource.text.isEmpty() -> {
                            toastyErrors("Select relationship")
                        }
                        fullName.isEmpty() -> {
                            tlname.error = getString(R.string.required)
                        }
                        spinneroccupation.text.isEmpty() -> {
                            toastyErrors("Select occupation")
                        }
                        natureofactivity.isEmpty() -> {
                            tlDSale.error = getString(R.string.required)
                        }
                        income.isEmpty() -> {
                            tlDSale.error = ""
                            tlIcome.error = getString(R.string.required)
                        }
                        else -> {
                            tlDSale.error = ""
                            tlname.error = ""
                            tlIcome.error = ""
                            Log.d("TAG", "onItemSelected: ${item.memberId}")
                            val updateHouseholdMembersDTO = UpdateHouseholdMembersDTO()
                            updateHouseholdMembersDTO.full_name = etname.text.toString()
                            updateHouseholdMembersDTO.member_id = item.memberId
                            updateHouseholdMembersDTO.current_occupation_id = item.occupationId
                            updateHouseholdMembersDTO.income_or_fees_paid =
                                etIncome.text.toString().trim()
                            updateHouseholdMembersDTO.nature_of_activity = etDSale.text.toString()
                            updateHouseholdMembersDTO.relationship_id = item.relationshipId
                            binding.progressbar.mainPBar.makeVisible()
                            viewmodel.updateMembers(updateHouseholdMembersDTO)
                            dialog.dismiss()

                        }
                    }
                }else{
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusUpdate.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.houseHoldList.value= arrayListOf()
                            onInfoSuccessDialog("Household members Updated successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            viewmodel.statusDelCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            viewmodel.houseHoldList.value= arrayListOf()
                            onInfoSuccessDialog("Household members Deleted successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            viewmodel.stopObserving()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            viewmodel.responseDelStatus.observe(viewLifecycleOwner){
                Log.d("TAG", "responseRemGStatus: $it")
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnUpdate.isEnabled=false
                            btnCancel.isEnabled=true
                            binding.progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE->{
                            dialog.dismiss()
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR->{
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }

        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }
}