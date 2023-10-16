package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.UpdateBorrowingAdapter
import com.deefrent.rnd.fieldapp.databinding.AddBorrowingDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentOtherBorrowingsBinding
import com.deefrent.rnd.fieldapp.dtos.AddBorrowingDTO
import com.deefrent.rnd.fieldapp.dtos.DeleteBorrowingDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateBorrowingsDTO
import com.deefrent.rnd.fieldapp.network.models.OtherBorrowing
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.BorrowingCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdateBorrowingsFragment : Fragment(),BorrowingCallBack {
    private lateinit var binding: FragmentOtherBorrowingsBinding
    private lateinit var borroweringAdapter:UpdateBorrowingAdapter
    private lateinit var cardBinding: AddBorrowingDialogBinding
    private var statusName=""
    private var national_identity=""
    private var statusId=0
    private val arrayList:ArrayList<OtherBorrowing> = ArrayList()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBorrowingsBinding.inflate(layoutInflater)
        initializeUI()
        binding.apply {
            tvPbText.makeGone()
            note.makeGone()
            pb.makeGone()
            btnAddBorrowing.makeGone()
            btnContinue.makeGone()
            rbMyself.isChecked=true
            viewmodel.detailsData.observe(viewLifecycleOwner){
                national_identity=it.basicInfo.idNumber
            }
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
                if (rbMyself.isChecked && arrayList.isEmpty()) {
                    toastyErrors("Add other borrowings to continue")
                } else {
                    /*Navigation.findNavController(v)
                        .navigate(R.id.action_otherBorrowings_to_nextOfKinFragment)*/
                }
            }
            rvAddBorrowing.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            borroweringAdapter= UpdateBorrowingAdapter(arrayList,requireContext(),this@UpdateBorrowingsFragment)
            rvAddBorrowing.adapter=borroweringAdapter
            viewmodel.borrowingData.observe(viewLifecycleOwner){
                if (it.isNotEmpty()){
                    arrayList.clear()
                    arrayList.addAll(it)
                    borroweringAdapter.notifyDataSetChanged()
                    if (it.size<2){
                        btnAddBorrowing.makeVisible()
                    }else{
                        btnAddBorrowing.makeGone()
                    }
                }else{
                    note.makeVisible()
                    tvRadio.text=getString(R.string.do_you_have_any_existing_loans_with_other_financial_institutions)
                    note.text=getString(R.string.note_you_have_not_added_any_other_existing_borrowings_kindly_add_your_existing_borrowings)
                    btnAddBorrowing.makeVisible()
                }
            }

        }
    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener {
           findNavController() .navigateUp()
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
                        statusName="Active"
                        statusId=1
                    }else{
                        statusName="Inactive"
                        statusId=0
                    }
                    tiAmount.error=""
                    tlBAddress.error=""
                    tiTotalPaid.error=""
                    tiMonthlyInstalments.error=""
                   val addBorrowingsDTO=AddBorrowingDTO()
                    addBorrowingsDTO.customerIdNumber=national_identity
                    addBorrowingsDTO.amount=etAmount.text.toString()
                    addBorrowingsDTO.status= statusId.toString()
                    addBorrowingsDTO.institutionName=etBAddress.text.toString()
                    addBorrowingsDTO.amountPaidToDate=etTotalPaid.text.toString()
                    addBorrowingsDTO.monthlyInstallmentPaid=etMonthlyInstallments.text.toString()
                    viewmodel.addBorrowingDetails(addBorrowingsDTO)
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            toastySuccess("Borrowers details added successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseAddGStatus.observe(viewLifecycleOwner){
                Log.d("TAG", "responseAddGStatus: $it")
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnContinue.isEnabled=false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE->{
                            dialog.dismiss()
                            btnContinue.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR->{
                            btnContinue.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
    }

    override fun onItemSelected(pos: Int, items: OtherBorrowing) {
        cardBinding = AddBorrowingDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        populateStatus()
        cardBinding.apply {
            etBAddress.setText(items.institutionName)
            val value= FormatDigit.formatDigits(items.amount)
            val tPaid= FormatDigit.formatDigits(items.amountPaidToDate)
            val instPaid= FormatDigit.formatDigits(items.monthlyInstallmentPaid)
            etAmount.setText(value)
            etTotalPaid.setText(tPaid)
            etMonthlyInstallments.setText(instPaid)
            etStatus.setText(items.status)
            btnContinue.makeGone()
            clButtons.makeVisible()
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
                        statusName="Active"
                        statusId=1
                    }else{
                        statusName="Inactive"
                        statusId=0
                    }
                    tiAmount.error=""
                    tlBAddress.error=""
                    tiTotalPaid.error=""
                    tiMonthlyInstalments.error=""
                    val updateBorrowingDTO=UpdateBorrowingsDTO()
                    updateBorrowingDTO.amount=etAmount.text.toString()
                    updateBorrowingDTO.institutionName=etBAddress.text.toString()
                    updateBorrowingDTO.amountPaidToDate=etTotalPaid.text.toString()
                    updateBorrowingDTO.monthlyInstallmentPaid=etMonthlyInstallments.text.toString()
                    updateBorrowingDTO.status= statusId.toString()
                    updateBorrowingDTO.otherBorrowingId= items.otherBorrowingId.toString()
                    viewmodel.updateBorrowingDetails(updateBorrowingDTO)
                }
            }
            btnCancel.setOnClickListener {
                if (isNetworkAvailable(requireContext())){
                    val deleteBorrowingDTO=DeleteBorrowingDTO()
                    deleteBorrowingDTO.otherBorrowingId= items.otherBorrowingId.toString()
                    btnUpdate.isEnabled=false
                    btnCancel.isEnabled=false
                    viewmodel.deleteBorrowingDetails(deleteBorrowingDTO)
                }else{
                    onNoNetworkDialog(requireContext())
                }
            }
            /**update*/
            viewmodel.statuBsCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            toastySuccess("Borrowers details updated successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseUpGStatus.observe(viewLifecycleOwner){
                Log.d("TAG", "responseRemGStatus: $it")
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnUpdate.isEnabled=false
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE->{
                            dialog.dismiss()
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR->{
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            /**delete*/
            viewmodel.statusRGCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            arrayList.removeAt(pos)
                            borroweringAdapter.notifyItemRemoved(pos)
                            dialog.dismiss()
                            toastySuccess("Borrowers details deleted successfully")
                            btnUpdate.isEnabled=false
                            btnCancel.isEnabled=true
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseRemGStatus.observe(viewLifecycleOwner){
                Log.d("TAG", "responseRemGStatus: $it")
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnUpdate.isEnabled=false
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE->{
                            dialog.dismiss()
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR->{
                            btnUpdate.isEnabled=true
                            btnCancel.isEnabled=true
                            progressbar.mainPBar.makeGone()
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