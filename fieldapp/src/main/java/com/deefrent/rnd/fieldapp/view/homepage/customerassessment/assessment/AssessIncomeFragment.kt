package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddIncomeFragmentBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.rentalIncomeDocCode
import com.deefrent.rnd.fieldapp.utils.FormatDigit.Companion.roundTo
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.io.File
import java.util.*


class AssessIncomeFragment : Fragment() {
    private lateinit var binding: AddIncomeFragmentBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private lateinit var guarantor :List<AssessGuarantor>
    private lateinit var collateral :List<AssessCollateral>
    private lateinit var household :List<AssessHouseholdMemberEntity>
    private lateinit var otherBorrowing :List<AssessBorrowing>
    private lateinit var paySlipCode: List<AssessCustomerDocsEntity>
    private lateinit var salesReportCode: List<AssessCustomerDocsEntity>
    private lateinit var rentalReportCode: List<AssessCustomerDocsEntity>
    private lateinit var paySlipEntity: AssessCustomerDocsEntity
    private lateinit var salesReportEntity: AssessCustomerDocsEntity
    private lateinit var rentalReportEntity: AssessCustomerDocsEntity
    private var customerDocs: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    var idnumber = ""
    lateinit var imagePicker: ImagePicker
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[AssessmentDashboardViewModel::class.java]
    }

    private var incomeDocImageName = ""
    private var incomeDocUri: Uri? = null
    private var salesDocImageName = ""
    private var salesDocUri: Uri? = null
    private var rentalIncomeDocImageName = ""
    private var rentalIncomeDocUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddIncomeFragmentBinding.inflate(layoutInflater)
        initClickListeners()
        initEditTextListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        binding.apply {
            viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
                idnumber=nationalId
                getItemsFromRoom(nationalId)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)
    }

    private fun showPickerOptionsDialog(type: String) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dialog.dismiss()
                    selectFromCamera(type)
                }
                options[item] == "Choose From Gallery" -> {
                    dialog.dismiss()
                    selectFromGallery(type)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun selectFromCamera(type: String) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, type, "Camera")
        }
    }

    private fun selectFromGallery(type: String) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, type, "Gallery"
            )
        }
    }

    //CallBack for result
    private fun imageCallBack(imageResult: ImageResult<Uri>, type: String, from: String) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                val generatedUUID = UUID.randomUUID().toString()
                when (type) {
                    "rentalIncome" -> {
                        rentalIncomeDocImageName = generateUniqueDocName(
                            idnumber,
                            rentalIncomeDocCode
                        )
                        rentalIncomeDocUri = uri
                        if (rentalReportCode.isNotEmpty()) {
                            rentalReportEntity.docPath = rentalIncomeDocImageName
                            Log.i("TAG", "imageCallBack: ${Gson().toJson(rentalReportEntity)}")
                            customerDocs.mapInPlace {
                                /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                                 *  we replace the element /value
                                 * with the new  vale, else if the element has not been modified, we retain it*/
                                    element ->
                                if (element.docCode == rentalIncomeDocCode) rentalReportEntity else element
                            }
                        } else {
                            val customerDocsEntity = AssessCustomerDocsEntity(
                                0,
                                idnumber,
                                rentalIncomeDocCode,
                                generatedUUID,
                                rentalIncomeDocImageName
                            )
                            customerDocs.add(customerDocsEntity)
                        }
                        binding.tvAttachRentalIncomeReport.text =
                            "Rental Income Report - $rentalIncomeDocImageName"

                    }
                    "salesReport" -> {
                        salesDocImageName = generateUniqueDocName(
                            idnumber,
                            Constants.salesReportDocCode
                        )
                        salesDocUri = uri
                        if (salesReportCode.isNotEmpty()) {
                            salesReportEntity.docPath = salesDocImageName
                            Log.i("TAG", "imageCallBack: ${Gson().toJson(salesReportEntity)}")
                            customerDocs.mapInPlace {
                                /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                                 *  we replace the element /value
                                 * with the new  vale, else if the element has not been modified, we retain it*/
                                    element ->
                                if (element.docCode == Constants.salesReportDocCode) salesReportEntity else element
                            }
                        } else {
                            val customerDocsEntity = AssessCustomerDocsEntity(
                                0,
                                idnumber,
                                Constants.salesReportDocCode,
                                generatedUUID,
                                salesDocImageName
                            )
                            customerDocs.add(customerDocsEntity)
                        }

                        binding.tvAttachSalesReport.text =
                            "Sales Report - $salesDocImageName"
                    }
                    "payslip" -> {
                        incomeDocImageName = generateUniqueDocName(
                            idnumber,
                            Constants.paySlipDocCode
                        )
                        incomeDocUri = uri
                        if (paySlipCode.isNotEmpty()) {
                            paySlipEntity.docPath = incomeDocImageName
                            Log.i("TAG", "imageCallBack: ${Gson().toJson(paySlipEntity)}")
                            customerDocs.mapInPlace {
                                /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                                 *  we replace the element /value
                                 * with the new  vale, else if the element has not been modified, we retain it*/
                                    element ->
                                if (element.docCode == Constants.paySlipDocCode) paySlipEntity else element
                            }
                        } else {
                            val customerDocsEntity = AssessCustomerDocsEntity(
                                0,
                                idnumber,
                                Constants.paySlipDocCode,
                                generatedUUID,
                                incomeDocImageName
                            )
                            customerDocs.add(customerDocsEntity)
                        }

                        binding.tvAttachPayslip.text =
                            "Income Statement - $incomeDocImageName"
                    }
                }

            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initEditTextListeners() {
        binding.apply {
            etfood.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when {
                        s.toString().isEmpty() -> {
                            binding.tvAttachSalesReport.visibility = View.GONE
                        }
                        s.toString().toFloat() > 0 -> {
                            binding.tvAttachSalesReport.visibility = View.VISIBLE
                        }
                        else -> {
                            binding.tvAttachSalesReport.visibility = View.GONE
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            etRIncome.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when {
                        s.toString().isEmpty() -> {
                            binding.tvAttachRentalIncomeReport.visibility = View.GONE
                        }
                        s.toString().toFloat() > 0 -> {
                            binding.tvAttachRentalIncomeReport.visibility = View.VISIBLE
                        }
                        else -> {
                            binding.tvAttachRentalIncomeReport.visibility = View.GONE
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            etRent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when {
                        s.toString().isEmpty() -> {
                            binding.tvAttachPayslip.visibility = View.GONE
                        }
                        s.toString().toFloat() > 0 -> {
                            binding.tvAttachPayslip.visibility = View.VISIBLE
                        }
                        else -> {
                            binding.tvAttachPayslip.visibility = View.GONE
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            val textWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
                    if (!TextUtils.isEmpty(etRent.text.toString().trim())
                        || !TextUtils.isEmpty(etNetSalary.getText().toString().trim())
                        || !TextUtils.isEmpty(etfood.getText().toString().trim())
                        || !TextUtils.isEmpty(etProfit.getText().toString().trim())
                        || !TextUtils.isEmpty(etRIncome.getText().toString().trim())
                        || !TextUtils.isEmpty(etfees.getText().toString().trim())
                        || !TextUtils.isEmpty(etTransport.getText().toString().trim())
                    ) {
                        val firtValue = if (TextUtils.isEmpty(etRent.getText().toString().trim())
                        ) 0f else etRent.text.toString().trim().toFloat()
                        val secondValue = if (TextUtils.isEmpty(
                                etNetSalary.text.toString().trim()
                            )
                        ) 0f else etNetSalary.text.toString().trim().toFloat()
                        val thirdValue = if (TextUtils.isEmpty(
                                etfood.text.toString().trim()
                            )
                        ) 0f else etfood.getText().toString().trim().toFloat()
                        val forthValue = if (TextUtils.isEmpty(
                                etProfit.getText().toString().trim()
                            )
                        ) 0f else
                            etProfit.getText().toString().trim().toFloat()
                        val fifthValue = if (TextUtils.isEmpty(
                                etRIncome.getText().toString().trim()
                            )
                        ) 0f else
                            etRIncome.getText().toString().trim().toFloat()
                        val sithValue = if (TextUtils.isEmpty(
                                etfees.getText().toString().trim()
                            )
                        ) 0f else
                            etfees.getText().toString().trim().toFloat()
                        val sievenValue = if (TextUtils.isEmpty(
                                etTransport.getText().toString().trim()
                            )
                        ) 0f else
                            etTransport.getText().toString().trim().toFloat()
                        val answer =
                            firtValue + secondValue + thirdValue + forthValue + fifthValue + sithValue + sievenValue
                        Log.e("RESULT", answer.toString())
                        val finalAns= roundTo(answer.toDouble())
                        tvTotalIncome.setText(finalAns.toString())
                    } else {
                        tvTotalIncome.setText("0")
                    }
                }

                override fun afterTextChanged(editable: Editable?) {}
            }
            etRent.addTextChangedListener(textWatcher)
            etNetSalary.addTextChangedListener(textWatcher)
            etfood.addTextChangedListener(textWatcher)
            etProfit.addTextChangedListener(textWatcher)
            etTransport.addTextChangedListener(textWatcher)
            etRIncome.addTextChangedListener(textWatcher)
            etfees.addTextChangedListener(textWatcher)
        }
    }
    private fun initClickListeners() {
        binding.apply {
            tvAttachRentalIncomeReport.setOnClickListener {
                if (tvAttachRentalIncomeReport.text.contains(getString(R.string.view_rental_income_report))) {
                    showEditPhotoDialog(rentalReportEntity)
                } else {
                    showPickerOptionsDialog("rentalIncome")
                }
            }
            tvAttachSalesReport.setOnClickListener {
                if (tvAttachSalesReport.text.contains(getString(R.string.view_sales_report))) {
                    showEditPhotoDialog(salesReportEntity)
                } else {
                    showPickerOptionsDialog("salesReport")
                }
            }
            tvAttachPayslip.setOnClickListener {
                if (tvAttachPayslip.text.contains(getString(R.string.view_payslip))) {
                    showEditPhotoDialog(paySlipEntity)
                } else {
                    showPickerOptionsDialog("payslip")
                }
            }

        }
    }
    private fun initializeUI() {
        binding.apply {
            btnContinue.setOnClickListener {
                val salary=etRent.text.toString().trim()
                val donations=etfees.text.toString().trim()
                val other=etTransport.text.toString().trim()
                val sales=etfood.text.toString().trim()
                val rentIncome=etRIncome.text.toString().trim()
                if (salary.isEmpty()){
                    tlrentals.error="Required"
                }else{
                    tlrentals.error=""
                   /* val debtRatio=DebtRatio()
                    debtRatio.apply {

                    }*/
                    viewmodel.apply {
                        assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                            detailsEntity.apply {
                               lastStep = "AssessExpensesFragment"
                               isComplete = false
                                hasFinished = false
                                isProcessed = false
                                netSalary=etNetSalary.text.toString().trim()
                                grossSalary = etRent.text.toString().trim()
                                totalSales=etfood.text.toString().trim()
                                profit=etProfit.text.toString().trim()
                                rentalIncome=etRIncome.text.toString().trim()
                                donation=etfees.text.toString().trim()
                                otherIncome=etTransport.text.toString().trim()
                                totalIncome=tvTotalIncome.text.toString()
                                saveAssessmentDataLocally(detailsEntity)
                               viewmodel.assessCustomerEntity.postValue(detailsEntity)
                                if (incomeDocUri != null) {
                                    saveImageToInternalAppStorage(
                                        incomeDocUri!!,
                                        requireContext(),
                                        incomeDocImageName
                                    )
                                }
                                if (salesDocUri != null) {
                                    saveImageToInternalAppStorage(
                                        salesDocUri!!,
                                        requireContext(),
                                        salesDocImageName
                                    )
                                }
                                if (rentalIncomeDocUri != null) {
                                    saveImageToInternalAppStorage(
                                        rentalIncomeDocUri!!,
                                        requireContext(),
                                        rentalIncomeDocImageName
                                    )
                                }
                               // debtRatioLV.postValue(debtRatio)
                            }
                        }
                    }
                    findNavController().navigate(R.id.action_assessIncomeFragment_to_assessExpensesFragment)

                }

            }

        }

        binding.ivBack.setOnClickListener { v ->
            findNavController().navigate(R.id.assessHouseholdMembersFragment)
        }
      handleBackButton()

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
                    findNavController().navigate(R.id.assessHouseholdMembersFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getItemsFromRoom(parentId:String){
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}",)
                binding.apply {
                    if (it.assessCustomerEntity.netSalary.isNotEmpty()){
                        etNetSalary.setText(roundTo(it.assessCustomerEntity.netSalary.toDouble()))
                    }
                    if (it.assessCustomerEntity.grossSalary.isNotEmpty()){
                        etRent.setText(roundTo(it.assessCustomerEntity.grossSalary.toDouble()))
                    }
                    if (it.assessCustomerEntity.totalSales.isNotEmpty()){
                        etfood.setText(roundTo(it.assessCustomerEntity.totalSales.toDouble()))
                    }
                    if (it.assessCustomerEntity.profit.isNotEmpty()){
                        etProfit.setText(roundTo(it.assessCustomerEntity.profit.toDouble()))
                    }
                    if (it.assessCustomerEntity.rentalIncome.isNotEmpty()){
                        etRIncome.setText(roundTo(it.assessCustomerEntity.rentalIncome.toDouble()))
                    }
                    if (it.assessCustomerEntity.donation.isNotEmpty()){
                        etfees.setText(roundTo(it.assessCustomerEntity.donation.toDouble()))
                    }
                    if (it.assessCustomerEntity.otherIncome.isNotEmpty()){
                        etTransport.setText(roundTo(it.assessCustomerEntity.otherIncome.toDouble()))
                    }
                    customerDocs.clear()
                    customerDocs.addAll(it.customerDocs)
                    collateral = it.assessCollateral
                    guarantor = it.assessGua
                    otherBorrowing = it.assessBorrow
                    household = it.householdMember
                    salesReportCode =
                        it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == Constants.salesReportDocCode }
                    paySlipCode =
                        it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == Constants.paySlipDocCode }
                    rentalReportCode =
                        it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == Constants.rentalIncomeDocCode }
                    if (it.customerDocs.isNotEmpty()) {
                        if (paySlipCode.isNotEmpty()) {
                            paySlipEntity = paySlipCode.first()
                            if (paySlipEntity.docPath.isNotEmpty()){
                            val uri = Uri.fromFile(File(paySlipEntity.docPath))
                            val imageName = getFileName(uri, requireActivity())
                            if (Constants.pattern.containsMatchIn(paySlipEntity.docPath)) {
                                binding.tvAttachPayslip.text = getString(R.string.view_payslip)
                            } else {
                                Log.e("TAG", "uri: $imageName")
                                binding.tvAttachPayslip.text =
                                    "Income Statement - $imageName"
                            }
                        } else {
                            binding.tvAttachPayslip.text =
                                resources.getString(R.string.attach_payslip)
                        }
                        } else {
                            binding.tvAttachPayslip.text =
                                resources.getString(R.string.attach_payslip)
                        }

                        if (salesReportCode.isNotEmpty()) {
                            salesReportEntity = salesReportCode.first()
                            Log.e("TAG", "customerFaceID2: ${Gson().toJson(salesReportEntity)}")
                            if (salesReportEntity.docPath.isNotEmpty()){
                            val uriFace = Uri.fromFile(File(salesReportEntity.docPath))
                            val imageFace = getFileName(uriFace, requireActivity())
                            if (Constants.pattern.containsMatchIn(salesReportEntity.docPath)) {
                                binding.tvAttachSalesReport.text = getString(R.string.view_sales_report)
                            } else {
                                Log.d("TAG", "getSavedItemsFromRoom URI: $imageFace")
                                binding.tvAttachSalesReport.text =
                                    "Sales Report - $imageFace"
                            }

                        } else {
                            binding.tvAttachSalesReport.text =
                                resources.getString(R.string.attach_sales_report)
                        }
                        } else {
                            binding.tvAttachSalesReport.text =
                                resources.getString(R.string.attach_sales_report)
                        }

                        /**rental income report*/
                        if (rentalReportCode.isNotEmpty()) {
                            rentalReportEntity = rentalReportCode.first()
                            if (rentalReportEntity.docPath.isNotEmpty()){
                            val uri = Uri.fromFile(File(rentalReportEntity.docPath))
                            val imageName = getFileName(uri, requireActivity())
                            if (Constants.pattern.containsMatchIn(rentalReportEntity.docPath)) {
                                binding.tvAttachRentalIncomeReport.text =
                                    getString(R.string.view_rental_income_report)
                            } else {
                                binding.tvAttachRentalIncomeReport.text =
                                    "Rental Income Report - $imageName"
                            }
                        } else {
                            binding.tvAttachRentalIncomeReport.text =
                                resources.getString(R.string.attach_rental_income_report)
                        }
                        } else {
                            binding.tvAttachRentalIncomeReport.text =
                                resources.getString(R.string.attach_rental_income_report)
                        }
                    }

                }
            }
        }
    }
    private fun showEditPhotoDialog(customerDocsEntity: AssessCustomerDocsEntity) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {

            when (customerDocsEntity.docCode) {
                Constants.paySlipDocCode -> {
                    Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Income Statement"
                    tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("payslip")
                    }
                }
                Constants.salesReportDocCode -> {
                    Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Sales Report"
                    tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("salesReport")
                    }
                }
                Constants.rentalIncomeDocCode -> {
                    Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Rental Income Report"
                    tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("rentalIncome")
                    }
                }
            }

            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =
                    layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(ivImagePreview)
                mBuilder.setView(mView)
                val mDialog: AlertDialog = mBuilder.create()
                mDialog.show()
            }

            ImageCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()

    }



}