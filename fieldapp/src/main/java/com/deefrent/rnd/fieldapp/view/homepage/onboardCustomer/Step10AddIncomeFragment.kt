package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
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
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.paySlipDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.rentalIncomeDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.salesReportDocCode
import com.deefrent.rnd.fieldapp.utils.FormatDigit.Companion.roundTo
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import kotlinx.android.synthetic.main.edit_phots_dialog.ImageCancel
import java.io.File
import java.util.*


class Step10AddIncomeFragment : Fragment() {
    private var nationaid = ""
    private lateinit var binding: AddIncomeFragmentBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private lateinit var householdEntity: List<HouseholdMemberEntity>
    private lateinit var guarantor: List<Guarantor>
    private lateinit var borrowings: List<OtherBorrowing>
    private lateinit var collateral: List<Collateral>
    lateinit var imagePicker: ImagePicker
    private lateinit var paySlipCode: List<CustomerDocsEntity>
    private lateinit var salesReportCode: List<CustomerDocsEntity>
    private lateinit var rentalReportCode: List<CustomerDocsEntity>
    private lateinit var paySlipEntity: CustomerDocsEntity
    private lateinit var salesReportEntity: CustomerDocsEntity
    private lateinit var rentalReportEntity: CustomerDocsEntity
    private  var customerDocs :ArrayList<CustomerDocsEntity>  = arrayListOf()
    var idnumber = ""
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[OnboardCustomerViewModel::class.java]
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
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }
        initClickListeners()
        initEditTextListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
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
                            nationaid,
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
                            val customerDocsEntity = CustomerDocsEntity(
                                0,
                                nationaid,
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
                            nationaid,
                            salesReportDocCode
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
                                if (element.docCode == salesReportDocCode) salesReportEntity else element
                            }
                        } else {
                            val customerDocsEntity = CustomerDocsEntity(
                                0,
                                nationaid,
                                salesReportDocCode,
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
                            nationaid,
                            paySlipDocCode
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
                                if (element.docCode == paySlipDocCode) paySlipEntity else element
                            }
                        } else {
                            val customerDocsEntity = CustomerDocsEntity(
                                0,
                                nationaid,
                                paySlipDocCode,
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
            llTotalIncome.makeGone()
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
                val salary = etRent.text.toString().trim()
                val donations = etfees.text.toString().trim()
                val other = etTransport.text.toString().trim()
                val sales = etfood.text.toString().trim()
                val rentIncome = etRIncome.text.toString().trim()
                if (other.isEmpty()) {
                    tlTransport.error = "Required"
                } else {
                    tlrentals.error = ""
                    viewmodel.apply {
                        customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                            customerDetailsEntity.apply {
                                lastStep = "AddIncomeFragment"
                                isComplete = false
                                isProcessed = false
                                hasFinished = false
                                netSalary = etNetSalary.text.toString().trim()
                                grossSalary = etRent.text.toString().trim()
                                totalSales = etfood.text.toString().trim()
                                profit = etProfit.text.toString().trim()
                                rIncome = etRIncome.text.toString().trim()
                                donation = etfees.text.toString().trim()
                                otherIncome = etTransport.text.toString().trim()
                                totalIncome = tvTotalIncome.text.toString()
                                saveCustomerFullDatLocally(customerDetailsEntity)
                                viewmodel.customerEntityData.postValue(customerDetailsEntity)
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
                                Log.d(
                                    "TAG",
                                    "assessCustomerEntity: ${Gson().toJson(customerDetailsEntity)}"
                                )
                            }
                        }
                    }
                    if (fromSummary == 6) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_addIncomeFragment_to_addExpensesFragment)

                    }

                }

            }

        }

        binding.ivBack.setOnClickListener { v ->
            if (fromSummary == 6) {
                findNavController().navigate(R.id.summaryFragment)
            } else {
                findNavController().navigate(R.id.action_addIncome_to_addHouseholdMembersFragment)

            }
        }
        handleBackButton()

    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            borrowings,
            householdEntity,
            customerDocs
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (fromSummary == 6) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_addIncome_to_addHouseholdMembersFragment)

                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) { debtRatio ->
            binding.apply {
                if (debtRatio.customerDetails.grossSalary.isNotEmpty()) {
                    etRent.setText(roundTo(debtRatio.customerDetails.grossSalary.toDouble()))
                }
                if (debtRatio.customerDetails.netSalary.isNotEmpty()) {
                    etNetSalary.setText(roundTo(debtRatio.customerDetails.netSalary.toDouble()))
                }
                if (debtRatio.customerDetails.totalSales.isNotEmpty()) {
                    etfood.setText(roundTo(debtRatio.customerDetails.totalSales.toDouble()))
                }
                if (debtRatio.customerDetails.profit.isNotEmpty()) {
                    etProfit.setText(roundTo(debtRatio.customerDetails.profit.toDouble()))
                }
                if (debtRatio.customerDetails.rIncome.isNotEmpty()) {
                    etRIncome.setText(roundTo(debtRatio.customerDetails.rIncome.toDouble()))
                }
                if (debtRatio.customerDetails.donation.isNotEmpty()) {
                    etfees.setText(roundTo(debtRatio.customerDetails.donation.toDouble()))
                }
                if (debtRatio.customerDetails.otherIncome.isNotEmpty()) {
                    etTransport.setText(roundTo(debtRatio.customerDetails.otherIncome.toDouble()))
                }
                customerDocs.clear()
                customerDocs.addAll(debtRatio.customerDocs)
                collateral = debtRatio.collateral
                guarantor = debtRatio.guarantors
                borrowings = debtRatio.otherBorrowing
                householdEntity = debtRatio.householdMember
                salesReportCode =
                    debtRatio.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == salesReportDocCode }
                paySlipCode =
                    debtRatio.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == paySlipDocCode }
                rentalReportCode =
                    debtRatio.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == rentalIncomeDocCode }
                if (debtRatio.customerDocs.isNotEmpty()) {
                    if (paySlipCode.isNotEmpty()) {
                        paySlipEntity = paySlipCode.first()
                        if (paySlipEntity.docPath.isNotEmpty()) {
                            val uri = Uri.fromFile(File(paySlipEntity.docPath))
                            val imageName = getFileName(uri, requireActivity())
                            if (pattern.containsMatchIn(paySlipEntity.docPath)) {
                                binding.tvAttachPayslip.text = getString(R.string.view_payslip)
                            } else {
                                Log.e("TAG", "uri: $imageName")
                                binding.tvAttachPayslip.text =
                                    "Income Statement - $imageName"
                            }
                        }else {
                            binding.tvAttachPayslip.text =
                                resources.getString(R.string.attach_payslip)
                        }
                    }else {
                        binding.tvAttachPayslip.text =
                            resources.getString(R.string.attach_payslip)
                    }

                    if (salesReportCode.isNotEmpty()) {
                        salesReportEntity = salesReportCode.first()
                        if (salesReportEntity.docPath.isNotEmpty()){
                        Log.e("TAG", "customerFaceID2: ${Gson().toJson(salesReportEntity)}")
                        val uriFace = Uri.fromFile(File(salesReportEntity.docPath))
                        val imageFace = getFileName(uriFace, requireActivity())
                        if (pattern.containsMatchIn(salesReportEntity.docPath)) {
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
                    }else {
                        binding.tvAttachSalesReport.text =
                            resources.getString(R.string.attach_sales_report)
                    }

                    /**rental income report*/
                    if (rentalReportCode.isNotEmpty()) {
                        rentalReportEntity = rentalReportCode.first()
                        if (rentalReportEntity.docPath.isNotEmpty()){
                        val uri = Uri.fromFile(File(rentalReportEntity.docPath))
                        val imageName = getFileName(uri, requireActivity())
                        if (pattern.containsMatchIn(rentalReportEntity.docPath)) {
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

    private fun showEditPhotoDialog(customerDocsEntity: CustomerDocsEntity) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {

            when (customerDocsEntity.docCode) {
                paySlipDocCode -> {
                    Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Income Statement"
                    tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("payslip")
                    }
                }
                salesReportDocCode -> {
                    Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Sales Report"
                    tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("salesReport")
                    }
                }
                rentalIncomeDocCode -> {
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