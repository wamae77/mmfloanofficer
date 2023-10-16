package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer
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
import com.deefrent.rnd.fieldapp.data.adapters.AddHouseHoldAdapter
import com.deefrent.rnd.fieldapp.databinding.AddHouseholdMemberDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentHouseholdInfoBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.HouseholdMemberEntityCallBack
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
class Step9AddHouseholdMembersFragment : Fragment(), HouseholdMemberEntityCallBack {
    private lateinit var binding: FragmentHouseholdInfoBinding
    private lateinit var cardBinding: AddHouseholdMemberDialogBinding
    private lateinit var addHouseHoldAdapter: AddHouseHoldAdapter
    private val items: ArrayList<HouseholdMemberEntity> = ArrayList()
    private  var customerDocs :ArrayList<CustomerDocsEntity>  = arrayListOf()
    private lateinit var guarantor:List<Guarantor>
    private lateinit var borrowings:List<OtherBorrowing>
    private lateinit var collateral:List<Collateral>
    private var itemsAdded: ArrayList<HouseholdMemberEntity> = ArrayList()
    private var rshipId = ""
    private var occupationId = ""
    private var nationalId = ""
    private var isDeleteItems: Boolean = false
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseholdInfoBinding.inflate(layoutInflater)
        viewmodel.cIdNumber.observe(viewLifecycleOwner){
            nationalId=it
            getSavedItemsFromRoom(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            header.text = "Add Family Members"
            addHouseHoldAdapter = AddHouseHoldAdapter(items, this@Step9AddHouseholdMembersFragment)
            rvMember.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvMember.adapter = addHouseHoldAdapter

            btnAddMember.setOnClickListener {
                addMember()
            }
            btnContinue.setOnClickListener {
               /* if (items.isEmpty()) {
                    toastyErrors("Add household Members to continue")
                } else {*/
                    isDeleteItems=true
                    viewmodel.customerEntityData.observe(viewLifecycleOwner){customerDetailsEntity->
                        customerDetailsEntity.apply {
                            lastStep = "OtherBorrowingsFragment"
                            isComplete = false
                            isProcessed = false
                            hasFinished = false
                            Log.d("TAG", "onOtherBorrowing: $items")
                            saveCustomerFullDatLocally(customerDetailsEntity,items)
                            viewmodel.householdMemberEntity.postValue(items)
                            Log.d("TAG", "assessCustomerEntity: ${Gson().toJson(customerDetailsEntity)}")
                        }
                    }
                    findNavController().navigate(R.id.action_addHouseholdMembersFragment_to_addIncomeFragment)

            }
         ivBack.setOnClickListener {
             findNavController().navigate(R.id.action_addHouseholdMembersFragment_to_nextOfKinFragment)
         }
         handleBackButton()

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
                        val hoseholdMembers = HouseholdMemberEntity(0,nationalId,fullName,income,"",
                            natureofactivity,occupationId,spinneroccupation.text.toString(),rshipId,spinnerIsource.text.toString())
                        val index = items.size
                        items.add(0, hoseholdMembers)
                        itemsAdded.add(hoseholdMembers)
                        isDeleteItems=false
                       addHouseHoldAdapter.notifyItemInserted(0)
                        dialog.dismiss()
                    }
                if (items.size==0){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
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

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity, household:List<HouseholdMemberEntity>){
        viewmodel.insertCustomerFullDetails(customerDetailsEntity,guarantor, collateral, borrowings,household,customerDocs)
    }
    override fun onItemSelected(pos: Int, lists: HouseholdMemberEntity) {
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
                    items.removeAt(pos)
                    addHouseHoldAdapter.notifyItemRemoved(pos)
                    Log.e("TAG", "isDeletitems: ${items.size}", )

                }
                if (items.size==0){
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
                        addHouseHoldAdapter.notifyItemChanged(pos)
                        dialog.dismiss()
                    }
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_addHouseholdMembersFragment_to_nextOfKinFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoom: $it")
            binding.apply {
                isDeleteItems=true
                items.clear()
                items.addAll(it.householdMember)
                Log.d("TAG", "getHMemberById${(it)}: ")
                addHouseHoldAdapter.notifyDataSetChanged()
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral=it.collateral
                guarantor = it.guarantors
                borrowings = it.otherBorrowing
                if (items.isEmpty()){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }
            }
        }
    }



}