package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentIncomeDetailsBinding
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*


class IncomeDetailsFragment : Fragment() {
    private lateinit var binding: FragmentIncomeDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding

    var idnumber = ""
    var paySlipPath = ""
    var paySlipCode = ""
    var rentalPath = ""
    var rentalCode = ""
    var salesPath = ""
    var salesCode = ""
    private lateinit var imagePicker: ImagePicker
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[CustomerAssessmentHomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomeDetailsBinding.inflate(layoutInflater)

        binding.apply {
            pb.makeGone()
            tvPbText.makeGone()
        }
        binding.apply {
            viewmodel.iDLookUpData.observe(viewLifecycleOwner) { info ->
                idnumber = info!!.idNumber
                initEditTextListeners()
                if (info?.incomes != null) {
                    note.makeGone()
                    btnContinue.text = "Update"
                    if (info.incomes.incomeNetSalary.isNotEmpty()) {
                        etNetSalary.setText(FormatDigit.roundTo(info.incomes.incomeNetSalary.toDouble()))
                    }
                    if (info.incomes.ownSalary.isNotEmpty()) {
                        etRent.setText(FormatDigit.roundTo(info.incomes.ownSalary.toDouble()))
                    }
                    if (info.incomes.otherBusinesses.isNotEmpty()) {
                        etfood.setText(FormatDigit.roundTo(info.incomes.otherBusinesses.toDouble()))
                    }
                    if (info.incomes.incomeProfit.isNotEmpty()) {
                        etProfit.setText(FormatDigit.roundTo(info.incomes.incomeProfit.toDouble()))
                    }
                    if (info.incomes.rental.isNotEmpty()) {
                        etRIncome.setText(FormatDigit.roundTo(info.incomes.rental.toDouble()))
                    }
                    if (info.incomes.remittanceOrDonation.isNotEmpty()) {
                        etfees.setText(FormatDigit.roundTo(info.incomes.remittanceOrDonation.toDouble()))
                    }
                    if (info.incomes.other.isNotEmpty()) {
                        etTransport.setText(FormatDigit.roundTo(info.incomes.other.toDouble()))
                    }

                    if (info.incomes.rentalDoc != null) {
                        if (info.incomes.rentalDoc.url.isNotEmpty()) {
                            rentalCode = info.incomes.rentalDoc.documentId.toString()
                            tvAttachRentalIncomeReport.text =
                                getString(R.string.view_rental_income_report)
                        } else {
                            tvAttachRentalIncomeReport.makeGone()
                            tvAttachRentalIncomeReport.text =
                                getString(R.string.attach_rental_income_report)
                        }
                    }
                    if (info.incomes.salesReportDoc != null) {
                        if (info.incomes.salesReportDoc.url.isNotEmpty()) {
                            salesCode = info.incomes.salesReportDoc.documentId.toString()
                            tvAttachSalesReport.text = getString(R.string.view_sales_report)
                        } else {
                            tvAttachSalesReport.makeGone()
                            tvAttachSalesReport.text = getString(R.string.attach_sales_report)
                        }
                    }
                    if (info.incomes.incomeStatementDoc != null) {
                        if (info.incomes.incomeStatementDoc.url.isNotEmpty()) {
                            paySlipCode = info.incomes.incomeStatementDoc.documentId.toString()
                            tvAttachPayslip.text = getString(R.string.view_payslip)
                        } else {
                            tvAttachPayslip.makeGone()
                            tvAttachPayslip.text = getString(R.string.attach_payslip)
                        }
                    }
                    binding.apply {
                        tvAttachRentalIncomeReport.setOnClickListener {
                            if (tvAttachRentalIncomeReport.text.contains(getString(R.string.view_rental_income_report))) {
                                showEditPhotoDialog(
                                    info.incomes.rentalDoc.url,
                                    info.incomes.rentalDoc.code
                                )
                            } else {
                                tvAttachRentalIncomeReport.makeGone()
                              //  showPickerOptionsDialog("rentalIncome")
                            }
                        }
                        tvAttachSalesReport.setOnClickListener {
                            if (tvAttachSalesReport.text.contains(getString(R.string.view_sales_report))) {
                                showEditPhotoDialog(
                                    info.incomes.salesReportDoc.url,
                                    info.incomes.salesReportDoc.code
                                )
                            } else {
                                tvAttachSalesReport.makeGone()
                              //  showPickerOptionsDialog("salesReport")
                            }
                        }
                        tvAttachPayslip.setOnClickListener {
                            if (tvAttachPayslip.text.contains(getString(R.string.view_payslip))) {
                                showEditPhotoDialog(
                                    info.incomes.incomeStatementDoc.url,
                                    info.incomes.incomeStatementDoc.code
                                )
                            } else {
                                tvAttachPayslip.makeGone()
                             //   showPickerOptionsDialog("payslip")
                            }
                        }

                    }

                } else {
                    btnContinue.text = "Continue"
                    note.makeVisible()
                }
            }
        }

        initializeUI()
        return binding.root
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
                            if (tvAttachSalesReport.text.contains(getString(R.string.view_sales_report))) {
                                binding.tvAttachSalesReport.visibility = View.VISIBLE
                            } else {
                                tvAttachSalesReport.makeGone()
                                //  showPickerOptionsDialog("salesReport")
                            }
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
                            if (tvAttachRentalIncomeReport.text.contains(getString(R.string.view_rental_income_report))) {
                                binding.tvAttachRentalIncomeReport.visibility = View.VISIBLE
                            } else {
                                tvAttachRentalIncomeReport.makeGone()
                                //  showPickerOptionsDialog("salesReport")
                            }
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
                            if (tvAttachPayslip.text.contains(getString(R.string.view_payslip))) {
                                binding.tvAttachPayslip.visibility = View.VISIBLE
                            } else {
                                tvAttachPayslip.makeGone()
                                //  showPickerOptionsDialog("salesReport")
                            }
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
        }
    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.apply {
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val netsalary = etNetSalary.text.toString().trim()
                    val salary = etRent.text.toString().trim()
                    val totalSale = etfood.text.toString().trim()
                    val profit = etProfit.text.toString().trim()
                    val donations = etfees.text.toString().trim()
                    val others = etTransport.text.toString().trim()
                    val rentalIncome = etRIncome.text.toString().trim()
                    if (salary.isEmpty()) {
                        tlrentals.error = "Required"
                    } else {
                        tlrentals.error = ""
                        binding.progressbar.tvWait.text = getString(R.string.please_wait)
                        binding.progressbar.mainPBar.makeVisible()
                        val idNo = RequestBody.create(MultipartBody.FORM, idnumber)
                        val nSalary = RequestBody.create(MultipartBody.FORM, netsalary)
                        val grossSalary = RequestBody.create(MultipartBody.FORM, salary)
                        val tSale = RequestBody.create(MultipartBody.FORM, etfood.text.toString().trim())
                        val iProfit = RequestBody.create(MultipartBody.FORM, profit)
                        val rIncome = RequestBody.create(MultipartBody.FORM, rentalIncome)
                        val rDonation = RequestBody.create(MultipartBody.FORM, donations)
                        val iOthers = RequestBody.create(MultipartBody.FORM, others)
                        val payslipId = RequestBody.create(MultipartBody.FORM, paySlipCode)
                        val salesId = RequestBody.create(MultipartBody.FORM, salesCode)
                        val rentalId = RequestBody.create(MultipartBody.FORM, rentalCode)
                        val slipFile = MultipartBody.Part.createFormData(
                            "income_statement_doc", convertPathToFile(paySlipPath).name,
                            RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                convertPathToFile(paySlipPath)
                            )
                        )
                        val salesFile = MultipartBody.Part.createFormData(
                            "sales_report_doc", convertPathToFile(salesPath).name,
                            RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                convertPathToFile(salesPath)
                            )
                        )
                        val rentalFile = MultipartBody.Part.createFormData(
                            "rental_income_doc", convertPathToFile(rentalPath).name,
                            RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                convertPathToFile(rentalPath)
                            )
                        )
                        viewmodel.addIncome(
                            idNo,
                            nSalary,
                            grossSalary,
                            tSale,
                            iProfit,
                            rIncome,
                            rDonation,
                            iOthers
                        )
                    }
                } else {
                    toastyErrors("Check your internet connection and try again")

                }


            }
            viewmodel._expenseLoadingStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.mainLayout.makeGone()
                            binding.btnContinue.isEnabled = false
                            binding.progressbar.tvWait.text =
                                "Updating customer income information..."
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.mainLayout.makeVisible()
                            binding.btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                        else -> {
                            binding.btnContinue.isEnabled = true
                            binding.mainLayout.makeVisible()
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusI.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            onInfoSuccessDialog("Income information updated successfully")
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
        }

    }

    private fun showEditPhotoDialog(url: String, docCode: String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            when (docCode) {
                Constants.paySlipDocCode -> {
                    Glide.with(requireActivity()).load(url)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Income Statement"
                    tvEdit.makeGone()
                    /*tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("payslip")
                    }*/
                }
                Constants.salesReportDocCode -> {
                    Glide.with(requireActivity()).load(url)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Sales Report"
                    tvEdit.makeGone()
                    /*tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("salesReport")
                    }*/
                }
                Constants.rentalIncomeDocCode -> {
                    Glide.with(requireActivity()).load(url)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(userLogo)
                    tvTitle.text = "Rental Income Report"
                    tvEdit.makeGone()
                   /* tvEdit.setOnClickListener {
                        dialog.dismiss()
                        showPickerOptionsDialog("rentalIncome")
                    }*/
                }
            }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =
                    layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(url)
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

    private fun selectFromGallery(type: String) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, type, "Gallery"
            )
        }
    }

    private fun imageCallBack(imageResult: ImageResult<Uri>, type: String, from: String) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                val path: String = if (from == "Camera") {
                    getCameraPath(uri, requireActivity())
                } else {
                    FileUtil.getPath(uri, requireContext())
                }
                when (type) {
                    "rentalIncome" -> {
                        rentalPath = path
                        binding.tvAttachRentalIncomeReport.text =
                            "Rental Income Report - ${getFileName(uri, requireActivity())}"

                    }
                    "salesReport" -> {
                        salesPath = path
                        Log.d("TAG", "imageCallBacksalesPath: $salesPath")
                        Log.d("TAG", "salesPath: $path")
                        binding.tvAttachSalesReport.text =
                            "Sales Report - ${getFileName(uri, requireActivity())}"
                    }
                    "payslip" -> {
                        paySlipPath = path
                        Log.d("TAG", "imageCallBack: $paySlipPath")
                        Log.d("TAG", "imageCallBack: $path")
                        binding.tvAttachPayslip.text =
                            "Income Statement - ${getFileName(uri, requireActivity())}"
                    }
                }

            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun selectFromCamera(type: String) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, type, "Camera")
        }
    }


}