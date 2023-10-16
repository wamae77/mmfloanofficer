package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.AssessBorrowingAdapter
import com.deefrent.rnd.fieldapp.databinding.AddBorrowingDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentOtherBorrowingsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessBorrowingCallBack
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class AssessBorrowingsFragment : Fragment(),AssessBorrowingCallBack {
    private lateinit var binding: FragmentOtherBorrowingsBinding
    private lateinit var cardBinding: AddBorrowingDialogBinding
    private lateinit var assessBorrowingAdapter: AssessBorrowingAdapter
    private var statusId=0
    private var isDeleteItems: Boolean = false
    private var nationalId = ""
    private  var otherBorrowing :ArrayList<AssessBorrowing> = arrayListOf()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private  var customerDocs :ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private lateinit var collateral:List<AssessCollateral>
    private lateinit var guarantor:List<AssessGuarantor>

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBorrowingsBinding.inflate(layoutInflater)

        initializeUI()
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
                if (rbMyself.isChecked && otherBorrowing.isEmpty()) {
                    toastyErrors("Add other borrowings to continue")
                } else {
                    // isDeleteItems=true
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                        detailsEntity.apply {
                             lastStep = "AssessBorrowingsFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            Log.d("TAG", "onOtherBorrowing: $otherBorrowing")
                            saveAssessmentDataLocally(detailsEntity, otherBorrowing)
                            viewmodel.assessCustomerEntity.postValue(detailsEntity)

                        }
                    }

                    Navigation.findNavController(v)
                        .navigate(R.id.action_assessBorrowingFragment_to_assessnokFragment)
                }
            }
            rvAddBorrowing.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            assessBorrowingAdapter = AssessBorrowingAdapter(
                otherBorrowing,
                requireContext(),
                this@AssessBorrowingsFragment
            )
            rvAddBorrowing.adapter = assessBorrowingAdapter
            viewmodel.parentId.observe(viewLifecycleOwner) { Id ->
                nationalId = Id
                getSavedItemsFromRoom(Id)
            }
        }}

    private fun initializeUI() {
        handleBackButton()
        binding.ivBack.setOnClickListener {
            findNavController() .navigate(R.id.assessGuarantorsFragment)
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
                        etStatus.setText("Active")
                        statusId=1
                    }else{
                        etStatus.setText("Inactive")
                        statusId=0
                    }
                    tiAmount.error=""
                    tlBAddress.error=""
                    tiTotalPaid.error=""
                    tiMonthlyInstalments.error=""
                    val borr=AssessBorrowing(0,etBAddress.text.toString(),nationalId,etAmount.text.toString(),etTotalPaid.text.toString(),
                        statusId,etStatus.text.toString(),
                        etMonthlyInstallments.text.toString())
                    otherBorrowing.add(0,borr)
                    assessBorrowingAdapter.notifyItemInserted(0)
                    dialog.dismiss()
                }
            }
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
    }
    override fun onItemSelected(pos: Int, lists: AssessBorrowing) {
        cardBinding = AddBorrowingDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        populateStatus()
        cardBinding.apply {
            etBAddress.setText(lists.institutionName)
            etAmount.setText(lists.amount)
            etTotalPaid.setText(lists.totalAmountPaidToDate)
            etMonthlyInstallments.setText(lists.monthlyInstallmentPaid)
            if (lists.status=="1"){
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
                    otherBorrowing.removeAt(pos)
                    assessBorrowingAdapter.notifyItemRemoved(pos)
                }
                if (otherBorrowing.size==0){
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
                    lists.status= statusId.toString()
                    viewmodel.updateBorrow(lists)
                    assessBorrowingAdapter.notifyItemChanged(pos)
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
                otherBorrowing.clear()
                otherBorrowing.addAll(it.assessBorrow)
                assessBorrowingAdapter.notifyDataSetChanged()
                collateral=it.assessCollateral
                guarantor = it.assessGua
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                if (otherBorrowing.isEmpty()){
                    binding.note.makeVisible()
                }else{
                    binding.note.makeGone()
                }
            }
        }
    }
    private fun saveAssessmentDataLocally(
        assessCustomerEntity: AssessCustomerEntity,
        borr: List<AssessBorrowing>
    ) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity, customerDocs,
            collateral,
            guarantor,
            borr, household
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.assessGuarantorsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}