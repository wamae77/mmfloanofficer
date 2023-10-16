package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.AddBorrowingAdapter
import com.deefrent.rnd.fieldapp.databinding.AddBorrowingDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentOtherBorrowingsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.BorrowEntityCallBack
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import javax.inject.Inject

class Step7OtherBorrowingsFragment : BaseDaggerFragment(),BorrowEntityCallBack {
    private lateinit var binding: FragmentOtherBorrowingsBinding
    private lateinit var cardBinding: AddBorrowingDialogBinding
    private lateinit var gAdapter:AddBorrowingAdapter
    private var statusId=0
    private var isDeleteItems: Boolean = false
    private var nationalId = ""
    private val items:ArrayList<OtherBorrowing> = ArrayList()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs = arrayListOf<CustomerDocsEntity>()
    private lateinit var collateral:List<Collateral>
    private lateinit var guarantor:List<Guarantor>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBorrowingsBinding.inflate(layoutInflater)
        viewmodel.cIdNumber.observe(viewLifecycleOwner){
            nationalId=it
            getSavedItemsFromRoom(it)
        }
        initializeUI()
        handleBackButton()
        binding.apply {
            rbMyself.isChecked=true
            rbMyself.setOnCheckedChangeListener{ buttonView, isChecked ->
                if (isChecked){
                    clOption.makeVisible()
                    rbOthers.isChecked=false
                }
            }
            rbOthers.setOnCheckedChangeListener{ buttonView, isChecked ->
                if (isChecked){
                    rbMyself.isChecked=false
                    clOption.makeGone()
                }

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnAddBorrowing.setOnClickListener {
                addBorrowing()
            }
            btnContinue.setOnClickListener { v ->
                if (rbMyself.isChecked && items.isEmpty()) {
                    toastyErrors("Add other borrowings to continue")
                } else {
                    isDeleteItems=true
                    viewmodel.customerEntityData.observe(viewLifecycleOwner){customerDetailsEntity->
                        customerDetailsEntity.apply {
                            lastStep = "OtherBorrowingsFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            Log.d("TAG", "onOtherBorrowing: $items")
                            saveCustomerFullDatLocally(customerDetailsEntity,items)
                            viewmodel.borrowingData.postValue(items)

                        }
                    }

                    Navigation.findNavController(v)
                        .navigate(R.id.action_otherBorrowings_to_nextOfKinFragment)
                }
            }
            rvAddBorrowing.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
             gAdapter= AddBorrowingAdapter(items,this@Step7OtherBorrowingsFragment)
            rvAddBorrowing.adapter=gAdapter


        }
    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_otherBorrowingsFragment_to_guarantorsFragment)
        }

    }
    private fun populateStatus() {
        val status = resources.getStringArray(R.array.status)
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, status)
        cardBinding.etStatus.setAdapter(typeAdapter)
        cardBinding.etStatus.keyListener = null

    }
    private fun addBorrowing() {
        cardBinding = AddBorrowingDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        populateStatus()
        cardBinding.apply {
            btnContinue.setOnClickListener {
                val aName=etBAddress.text.toString()
                val amount=etAmount.text.toString()
                val status=etStatus.text.toString()
                val totalPaid=etTotalPaid.text.toString()
                val installment=etMonthlyInstallments.text.toString()
                if (aName.isEmpty()){
                    tlBAddress.error=getString(R.string.required)
                    toastyErrors("Select Institution Name")
                }else if (amount.isEmpty()){
                    tiAmount.error=getString(R.string.required)
                }else if (totalPaid.isEmpty()) {
                    tiTotalPaid.error = getString(R.string.required)
                }else if (status.isEmpty()){
                    toastyErrors("Select status")
                }else if (installment.isEmpty()){
                    tiMonthlyInstalments.error=getString(R.string.required)
                }else{
                    if (status=="Active"){
                        etStatus.setText("Active",false)
                        statusId=1
                    }else{
                        etStatus.setText("Inactive",false)
                        statusId=0
                    }
                    tiAmount.error=""
                    tlBAddress.error=""
                    tiTotalPaid.error=""
                    tiMonthlyInstalments.error=""
                    val borr=OtherBorrowing(0,aName,nationalId,amount,totalPaid,statusId,installment)
                    items.add(borr)
                    isDeleteItems=false

                    gAdapter.notifyDataSetChanged()
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
    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity, borrowing:List<OtherBorrowing>){
        viewmodel.insertCustomerFullDetails(customerDetailsEntity,guarantor, collateral, borrowing,household,customerDocs)
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_otherBorrowingsFragment_to_guarantorsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onItemSelected(pos: Int, lists: OtherBorrowing) {
        cardBinding = AddBorrowingDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        populateStatus()
        cardBinding.apply {
            etBAddress.setText(lists.institutionName)
            etAmount.setText(lists.amount)
            etTotalPaid.setText(lists.totalAmountPaidToDate)
            etMonthlyInstallments.setText(lists.monthlyInstallmentPaid)
            if (lists.status==1){
                etStatus.setText("Active",false)
                statusId=1
            }else{
                statusId=0
                etStatus.setText("Inactive",false)

            }
            btnContinue.makeGone()
            clButtons.makeVisible()
            btnCancel.setOnClickListener {
                if (isDeleteItems){
                    viewmodel.deleteBorrowID(lists.id)
                }else{
                    isDeleteItems=false
                    items.removeAt(pos)
                    gAdapter.notifyItemRemoved(pos)
                }
                if (items.size==0){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }

                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                val aName=etBAddress.text.toString()
                val amount=etAmount.text.toString()
                val status=etStatus.text.toString()
                val totalPaid=etTotalPaid.text.toString()
                val installment=etMonthlyInstallments.text.toString()
                if (aName.isEmpty()){
                    tlBAddress.error=getString(R.string.required)
                    toastyErrors("Select Institution Name")
                }else if (amount.isEmpty()){
                    tiAmount.error=getString(R.string.required)
                }else if (totalPaid.isEmpty()) {
                    tiTotalPaid.error = getString(R.string.required)
                }else if (status.isEmpty()){
                    toastyErrors("Select status")
                }else if (installment.isEmpty()){
                    tiMonthlyInstalments.error=getString(R.string.required)
                }else{
                    if (status=="Active"){
                        etStatus.setText("Active",false)
                        statusId=1
                    }else{
                        etStatus.setText("Inactive",false)
                        statusId=0
                    }
                    tiAmount.error=""
                    tlBAddress.error=""
                    tiTotalPaid.error=""
                    tiMonthlyInstalments.error=""
                    lists.institutionName=aName
                    lists.amount=amount
                    lists.totalAmountPaidToDate=totalPaid
                    lists.monthlyInstallmentPaid=installment
                    lists.status=statusId
                    viewmodel.updateBorrow(lists)
                    gAdapter.notifyItemChanged(pos)
                    dialog.dismiss()
                }
            }
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
    }
    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoom: ${Gson().toJson(it)}")
            binding.apply {
                isDeleteItems=true
                items.clear()
                items.addAll(it.otherBorrowing)
                gAdapter.notifyDataSetChanged()
                collateral=it.collateral
                guarantor = it.guarantors
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                if (items.isEmpty()){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }
            }
        }
    }



}