package com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.CURRENCY_CODE
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.SUCCESS_DESCRIPTION
import com.deefrent.rnd.common.utils.SUCCESS_DIALOGDETAILCOMMON
import com.deefrent.rnd.common.utils.SUCCESS_TITLE
import com.deefrent.rnd.common.utils.TRANSACTION_CHARGES
import com.deefrent.rnd.common.utils.TRANSACTION_EXERCISE_DUTY
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentDisburseLoanBinding
import com.deefrent.rnd.fieldapp.databinding.LoanDialogLayoutBinding
import com.deefrent.rnd.fieldapp.dtos.DisburseLoanDTO
import com.deefrent.rnd.fieldapp.dtos.DisburseLoanPreviewDTO
import com.deefrent.rnd.fieldapp.network.models.DisbursableLoan
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.network.models.LoginData
import com.deefrent.rnd.fieldapp.network.models.WalletAccount
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import com.deefrent.rnd.fieldapp.view.printreceipt.MoneyMartPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_disburse_loan.etDisbursementDate
import kotlinx.android.synthetic.main.fragment_disburse_loan.etDisbursementDatePartial
import kotlinx.android.synthetic.main.fragment_disburse_loan.etprocessingFees
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class DisburseLoanFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentDisburseLoanBinding
    private lateinit var cardBinding: LoanDialogLayoutBinding
    private val pinViewmodel: PinViewModel by activityViewModels()
    var formatedAccount = ""
    private var national_identity = ""
    private var depositNumber = ""
    private var loanAccountNumber = ""
    private lateinit var calendar: Calendar
    private val lookupViewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }
    private var isCashDisbursement = true
    private var isCashDisbursementPartial = true
    private var dialogDetailCommonHashSet = ArrayList<DialogDetailCommon>()
    private lateinit var loanLookupData: LoanLookupData
    private lateinit var disburseLoan: DisbursableLoan
    private lateinit var loginData: LoginData

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisburseLoanBinding.inflate(layoutInflater)
        val args = DisburseLoanFragmentArgs.fromBundle(requireArguments()).disburseLoan
        disburseLoan = args
        AUTH_IMAGE_FILE_PATH = ""
        loanLookupData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.LOANLOOKUPDATA),
            LoanLookupData::class.java
        )

        loginData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CURRENY_USER_DATA),
            LoginData::class.java
        )
        commonSharedPreferences.saveStringData(
            CommonSharedPreferences.CU_FINGER_PRINT_ID,
            loanLookupData.fingerPrintRegId
        )

        Log.e("LOANLOOKUPDATA", "$loanLookupData")

        /**
         *
         */

        binding.rgDisburseMode.visibilityView(false)
        binding.rgDisburseModePartial.visibilityView(false)
        /**
         *
         */
        binding.rbMyself.isChecked = true
        binding.rbDisburseCash.isChecked = true
        binding.rbDisburseCashPartial.isChecked = true
        binding.apply {
            tvAmountValue.text = args.loanAccountNo
            loanAccountNumber = args.loanAccountNo
            val finalAmount = FormatDigit.formatDigits(args.remainingAmount)
            tvFrequencyValue.text = "${args.currency} $finalAmount"
            etAmount.setText(FormatDigit.formatDigits(args.remainingAmount))
            head.text = "Disburse ${args.name.capitalizeWords}"
            lookupViewmodel.repaymentPeriod.postValue(args.loanAccountNo)
            lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
                national_identity = it.idNumber
                if (it.walletAccounts.isNotEmpty()) {
                    populateWalletAcc(it.walletAccounts)
                    populateWalletAccPartial(it.walletAccounts)
                }
                val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
                val customerName = "${it?.firstName} ${it?.lastName}"
                binding.tvAccName.text = String.format(
                    getString(R.string.acc), "$customerName -" +
                            "\n$idNumber"
                )
            }
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.Full.makeVisible()
                    binding.Partial.makeGone()
                    binding.etAmount.isFocusable = false
                    binding.etAmount.setText(FormatDigit.formatDigits(args.remainingAmount))
                    rbOthers.isChecked = false
                    rbMyself.isChecked = true
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.Full.makeGone()
                    binding.Partial.makeVisible()
                    rbMyself.isChecked = false
                    rbOthers.isChecked = true
                }
            }
            rbDisburseCash.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isCashDisbursement = true
                    binding.apply {
                        tlDeposit.makeGone()
                    }
                }
            }
            rbDisburseWallet.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isCashDisbursement = false
                    binding.apply {
                        tlDeposit.makeVisible()
                    }
                }
            }
            rbDisburseCashPartial.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isCashDisbursementPartial = true
                    binding.apply {
                        tlPPeriod.makeGone()
                    }
                }
            }
            rbDisburseWalletPartial.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isCashDisbursementPartial = false
                    binding.apply {
                        tlPPeriod.makeVisible()
                    }
                }
            }
            etDisbursementDate.keyListener = null
            etDisbursementDate.setOnClickListener {
                showDatePicker()
            }
            etDisbursementDatePartial.keyListener = null
            etDisbursementDatePartial.setOnClickListener {
                showDatePicker()
            }
        }

        checkTextChangedListener()
        return binding.root
    }

    private fun checkTextChangedListener() {
        //
        binding.etDisbursementDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.tiDisbursementDatePartial.error = ""
                binding.tiDisbursementDate.error = ""
                binding.tlEnterAmount.error = ""
            }
        })
        //
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.tiDisbursementDatePartial.error = ""
                binding.tiDisbursementDate.error = ""
                binding.tlEnterAmount.error = ""
            }
        })

    }

    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(requireContext(), { _, year, month, day_of_month ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day_of_month
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            if (binding.rbMyself.isChecked) {
                binding.etDisbursementDate.setText(sdf.format(calendar.time))
            } else if (binding.rbOthers.isChecked) {
                binding.etDisbursementDatePartial.setText(sdf.format(calendar.time))
            }
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        //dialog.datePicker.minDate = calendar.timeInMillis
        //calendar.add(Calendar.YEAR, -18)
        dialog.datePicker.maxDate = calendar.timeInMillis
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val isAuthorized: String = bundle.get("isAuthorized") as String
            performCommitDisbursement(isAuthorized)
        }
        binding.apply {
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val disburseLoanPreviewDTO = DisburseLoanPreviewDTO()
                    disburseLoanPreviewDTO.idNumber = national_identity

                    if (rbMyself.isChecked) {
                        if (validateYesFields() && binding.etDisbursementDate.text.toString()
                                .isNotEmpty()
                        ) {
                            disburseLoanPreviewDTO.amount = etAmount.text.toString().trim()
                            showConfirmDialog()
                            //lookupViewmodel.disburseLoan(disburseLoanPreviewDTO)
                        }
                    } else {
                        if (validateNoFields()) {
                            showConfirmDialog()
                            disburseLoanPreviewDTO.amount = etPamount.text.toString().trim()
                            //lookupViewmodel.disburseLoan(disburseLoanPreviewDTO)
                        }
                    }
                } else {
                    toastyErrors("Check your internet connection and try again")

                }
            }
            lookupViewmodel.disburseResponseStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.tvWait.text = "Submitting Request..."
                            btnContinue.isEnabled = false
                            binding.progressbar.mainPBar.makeVisible()
                        }

                        GeneralResponseStatus.DONE -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }

                        GeneralResponseStatus.ERROR -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            lookupViewmodel.responseGStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.tvWait.text =
                                getString(R.string.we_are_processing_requesrt)
                            btnContinue.isEnabled = false
                            binding.progressbar.mainPBar.makeVisible()
                        }

                        GeneralResponseStatus.DONE -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }

                        GeneralResponseStatus.ERROR -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }

            lookupViewmodel.disbStatusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            lookupViewmodel.stopObserving()
                            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
                            cardBinding =
                                LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
                            cardBinding.apply {
                                tvAmount.text = "Amount:"
                                tvHeading.text = "Confirm Loan Disbursement"
                                tvFrequency.text = "Disburse Method:"
                                tvPeriod.makeGone()
                                tvPeriodValue.makeGone()
                                tvPeriodCycle.makeGone()
                                tvPeriodCycleValue.makeGone()
                                tvAssetValue.makeGone()
                                tvAsset.makeGone()
                                lookupViewmodel.previewData.observe(viewLifecycleOwner) { charge ->
                                    if (rbMyself.isChecked) {
                                        if (isCashDisbursement) {
                                            tvFrequencyValue.text = "CASH"
                                        } else {
                                            tvFrequencyValue.text = spDeposit.text.toString()
                                        }
                                        TRANSACTION_CHARGES = charge.fee
                                        TRANSACTION_EXERCISE_DUTY = charge.exerciseDuty
                                        tvAmountValue.text = "${charge.currency} ${
                                            FormatDigit.formatDigits(
                                                etAmount.text.toString().trim()
                                            )
                                        }"
                                        /*tvAmountValue.text = FormatDigit.formatDigits(
                                            "${charge.currency} ${
                                                etAmount.text.toString().trim()
                                            }"
                                        )*/
                                    } else {
                                        if (isCashDisbursementPartial) {
                                            tvFrequencyValue.text = "CASH"
                                        } else {
                                            tvFrequencyValue.text = etPPeriod.text.toString()
                                        }
                                        tvAmountValue.text = "${charge.currency} ${
                                            FormatDigit.formatDigits(
                                                etPamount.text.toString().trim()
                                            )
                                        }"
                                    }
                                    tvChargeValue.text =
                                        "${charge.currency} ${FormatDigit.formatDigits(charge.fee)}"
                                }
                                tvSupplierValue.makeGone()
                                tvSupplier.makeGone()
                                tvPhone.makeGone()
                                tvAssetCost.makeGone()
                                tvAssetCostValue.makeGone()
                                tvPhoneValue.makeGone()
                                tvAName.makeGone()
                                tvANameValue.makeGone()
                            }

                            cardBinding.btnCancel.setOnClickListener {
                                btnContinue.isEnabled = true
                                dialog.dismiss()
                            }
                            cardBinding.btnConfirm.setOnClickListener {
                                btnContinue.isEnabled = true
                                dialog.dismiss()
                                dialog.hide()
                                commonSharedPreferences.setIsFingerPrintDone(false)
                                findNavController().navigate(R.id.action_disburseLoanFragment_to_authPinFragment)
                                lookupViewmodel.stopObserving()
                            }
                            dialog.setContentView(cardBinding.root)
                            dialog.show()
                            dialog.setCancelable(false)

                        }

                        0 -> {
                            lookupViewmodel.stopObserving()
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                        }

                        else -> {
                            lookupViewmodel.stopObserving()
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }

            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    findNavController().navigate(R.id.authFingerPrintFragment)
                    //performCommitDisbursement("")
                    pinViewmodel.stopObserving()
                }
            }
            //
            lookupViewmodel.statusDCommit.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            binding.apply {
                            }
                            binding.progressbar.mainPBar.makeGone()

                            val direction =
                                /*DisburseLoanFragmentDirections.actionDisburseLoanFragmentToLoanSuccessFragment(
                                    1
                                )
                                  findNavController().navigate(direction)
                                */
                                showSuccessScreen()
                            commonSharedPreferences.setIsFingerPrintDone(false)
                            commonSharedPreferences.setIsPrintReceipt(true)
                            lookupViewmodel.stopObserving()
                            // dashboardModel.setRefresh(true)
                        }

                        0 -> {
                            commonSharedPreferences.setIsFingerPrintDone(false)
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                            lookupViewmodel.stopObserving()
                        }

                        else -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(getString(R.string.error_occurred))
                            lookupViewmodel.stopObserving()

                        }
                    }
                }
            }
        }
    }

    private fun performCommitDisbursement(authorized: String) {
        if (isNetworkAvailable(requireContext())) {
            pinViewmodel.unsetAuthSuccess()
            val disburseLoanDTO = DisburseLoanDTO()
            disburseLoanDTO.loanAccountNo = loanAccountNumber
            disburseLoanDTO.depositAccountId = depositNumber

            if (binding.rbDisburseCash.isChecked) {
                disburseLoanDTO.disburseToWallet = "0"
            }
            if (binding.rbDisburseWallet.isChecked) {
                disburseLoanDTO.disburseToWallet = "1"
            }
            if (binding.rbMyself.isChecked) {
                disburseLoanDTO.amount = binding.etAmount.text.toString().trim()
                disburseLoanDTO.notes = binding.etReason.text.toString().trim()
                disburseLoanDTO.disbursementDate =
                    etDisbursementDate.text.toString().trim()
                disburseLoanDTO.processingFees =
                    etprocessingFees.text.toString().trim()
            } else {
                disburseLoanDTO.amount = binding.etPamount.text.toString().trim()
                disburseLoanDTO.notes = binding.etPreason.text.toString().trim()
                disburseLoanDTO.disbursementDate =
                    etDisbursementDatePartial.text.toString().trim()
                disburseLoanDTO.processingFees =
                    binding.etPProcessingFees.text.toString().trim()
            }
            lookupViewmodel.loanDisburseCommit(disburseLoanDTO)
            pinViewmodel.stopObserving()
            lookupViewmodel.stopObserving()
        } else {
            toastyErrors("Check your internet connection and try again")
        }
    }

    private fun showConfirmDialog() {
        binding.apply {
            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
            cardBinding =
                LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
            cardBinding.apply {
                tvAmount.text = "Amount:"
                tvHeading.text = "Confirm Loan Disbursement"
                tvFrequency.text = "Disburse To:"
                tvPeriod.makeGone()
                tvPeriodValue.makeGone()
                tvPeriodCycle.makeGone()
                tvPeriodCycleValue.makeGone()
                tvAssetValue.makeGone()
                tvAsset.makeGone()
                //lookupViewmodel.previewData.observe(viewLifecycleOwner) { charge ->
                if (rbMyself.isChecked) {
                    if (isCashDisbursement) {
                        tvFrequencyValue.text = "CASH"
                    } else {
                        tvFrequencyValue.text = spDeposit.text.toString()
                    }
                    tvAmountValue.text = "USD ${
                        FormatDigit.formatDigits(
                            etAmount.text.toString().trim()
                        )
                    }"
                    /*tvAmountValue.text = FormatDigit.formatDigits(
                    "${charge.currency} ${
                        etAmount.text.toString().trim()
                    }"
                )*/
                } else {
                    if (isCashDisbursementPartial) {
                        tvFrequencyValue.text = "CASH"
                    } else {
                        tvFrequencyValue.text = etPPeriod.text.toString()
                    }
                    tvAmountValue.text = "USD ${
                        FormatDigit.formatDigits(
                            etPamount.text.toString().trim()
                        )
                    }"
                }
                tvChargeValue.visibility = View.GONE
                tvCharge.visibility = View.GONE

                //}
                tvSupplierValue.makeGone()
                tvSupplier.makeGone()
                tvPhone.makeGone()
                tvAssetCost.makeGone()
                tvAssetCostValue.makeGone()
                tvPhoneValue.makeGone()
                tvAName.makeGone()
                tvANameValue.makeGone()
            }

            cardBinding.btnCancel.setOnClickListener {
                btnContinue.isEnabled = true
                dialog.dismiss()
            }
            cardBinding.btnConfirm.setOnClickListener {
                btnContinue.isEnabled = true
                dialog.dismiss()
                dialog.hide()
                //lookupViewmodel.stopObserving()
                commonSharedPreferences.setIsFingerPrintDone(false)
                findNavController().navigate(R.id.action_disburseLoanFragment_to_authPinFragment)
            }

            dialog.setContentView(cardBinding.root)
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    private fun validateYesFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            val amount = etAmount.text.toString().trim()
            val reason = etReason.text.toString().trim()
            if (isCashDisbursement) {
                if (amount.isEmpty()) {
                    isValid = false
                    tlEnterAmount.error = ("Fill out the amount")
                }
                if (etDisbursementDate.text.toString()
                        .trim().isEmpty()
                ) {
                    isValid = false
                    tiDisbursementDate.error = ("Fill out disbursement date")

                } else {
                    isValid = true
                    tiDisbursementDate.error = ""
                    lookupViewmodel.amount.postValue(etAmount.text.toString().trim())
                }
            } else {
                if (amount.isEmpty() || spDeposit.text.isEmpty()) {
                    isValid = false
                    toastyErrors("Fill out amount")
                }
                if (etDisbursementDate.text.toString()
                        .trim().isEmpty()
                ) {
                    isValid = false
                    tiDisbursementDate.error = ("*Required")
                } else {
                    isValid = true
                    lookupViewmodel.amount.postValue(etAmount.text.toString().trim())
                }
            }

        }
        return isValid
    }

    private fun validateNoFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            etAmount.isFocusable = true
            val amount = etPamount.text.toString().trim()
            val loanAcc = etPPeriod.text.toString().trim()
            val purpose = etPreason.text.toString().trim()
            if (isCashDisbursementPartial) {
                when {
                    amount.isEmpty() -> {
                        isValid = false
                        etPamount.error = ("Fill out the amount")
                    }

                    etDisbursementDatePartial.text.toString()
                        .trim().isEmpty() -> {
                        isValid = false
                        tiDisbursementDatePartial.error = ("Fill out disbursement date")
                    }

                    else -> {
                        isValid = true
                        tlEnterAmount.error = ""
                        tiDisbursementDatePartial.error = ""
                        lookupViewmodel.amount.postValue(etPamount.text.toString().trim())
                    }
                }
            } else {
                when {
                    amount.isEmpty() -> {
                        isValid = false
                        etPamount.error = ("Fill out the amount")
                    }

                    loanAcc.isEmpty() -> {
                        isValid = false
                        etPPeriod.error = ("Fill out the mandatory fields")
                    }

                    etDisbursementDatePartial.text.toString()
                        .trim().isEmpty() -> {
                        isValid = false
                        tiDisbursementDatePartial.error = ("Fill out disbursement date")
                    }

                    else -> {
                        isValid = true
                        tlEnterAmount.error = ""
                        tiDisbursementDatePartial.error = null;
                        lookupViewmodel.amount.postValue(etPamount.text.toString().trim())
                    }
                }
            }
        }
        return isValid
    }

    private fun populateWalletAcc(genderList: List<WalletAccount>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.spDeposit.setAdapter(typeAdapter)
        binding.spDeposit.keyListener = null
        binding.spDeposit.setOnItemClickListener { parent, _, position, _ ->
            val selected: WalletAccount = parent.adapter.getItem(position) as WalletAccount
            depositNumber = selected.accountNumber.toString()
            val accNo = selected.accountNumber.replace("(?<=.{3}).(?=.{3})".toRegex(), "*")
            formatedAccount = "${selected.accountName} - $accNo"
            lookupViewmodel.repaymentPeriodMeasure.postValue("${selected.accountName} - $accNo")
            binding.spDeposit.setText(formatedAccount, false)
        }
    }

    private fun populateWalletAccPartial(genderList: List<WalletAccount>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.etPPeriod.setAdapter(typeAdapter)
        binding.etPPeriod.keyListener = null
        binding.etPPeriod.setOnItemClickListener { parent, _, position, _ ->
            val selected: WalletAccount = parent.adapter.getItem(position) as WalletAccount
            depositNumber = selected.accountNumber.toString()
            val accNo = selected.accountNumber.replace("(?<=.{3}).(?=.{3})".toRegex(), "*")
            formatedAccount = "${selected.accountName} - $accNo"
            lookupViewmodel.repaymentPeriodMeasure.postValue("${selected.accountName} - $accNo")
            binding.etPPeriod.setText(formatedAccount, false)
        }
    }


    private fun showSuccessScreen() {
        val bundle = Bundle()
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "Amount:",
                content = "${CURRENCY_CODE} ${binding.etAmount.text.toString().trim()}"
            )
        )
        if (binding.rbOthers.isChecked) {
            dialogDetailCommonHashSet.add(
                DialogDetailCommon(
                    label = "Deposit To:",
                    content = formatedAccount
                )
            )
        }
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "Loan Account Number",
                content = "${disburseLoan.loanAccountNo} "
            )
        )
        lookupViewmodel.apply {
            loanDisburseCommitData.observe(viewLifecycleOwner) {
                dialogDetailCommonHashSet.add(
                    DialogDetailCommon(
                        label = "Loan Balance:",
                        content = "${it.loanBalance} "
                    )
                )

                dialogDetailCommonHashSet.add(
                    DialogDetailCommon(
                        label = "REF ID:",
                        content = "${it.transactionCode} "
                    )
                )
            }
        }

        bundle.putString(SUCCESS_TITLE, "Loan Disbursement Successful")
        bundle.putString(
            SUCCESS_DESCRIPTION,
            "Your loan has been disbursed successfully to your Loan wallet account."
        )

        bundle.putParcelableArrayList(SUCCESS_DIALOGDETAILCOMMON, dialogDetailCommonHashSet)
        findNavController().navigate(
            R.id.generalSuccessfulFragment,
            bundle
        )
        dataToPrint()
        commonSharedPreferences.setIsPrintReceipt(true)

    }


    private fun dataToPrint() {
        lookupViewmodel.loanDisburseCommitData.observe(viewLifecycleOwner) {
            PrinterConfigs.RECEIPT_TEXT_ARRAY = arrayOf<String>(
                MoneyMartPrintServiceActivity.centeredText("LOAN DISBURSEMENT", 48),
                MoneyMartPrintServiceActivity.centeredText("Loan Disbursement Successful", 44),
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "Amount:         ${CURRENCY_CODE} ${binding.etAmount.text.toString()}",
                "Disburse Loan:  ${disburseLoan.name}",
                "Loan Balance:   ${CURRENCY_CODE} ${it.loanBalance}",
                "Loan Tenure:    ${disburseLoan.loanTenure}",
                "Loan Interest:  ${disburseLoan.interestRate}",
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "\n",
                "CUSTOMER NAME:  ${loanLookupData.firstName} ${loanLookupData.lastName}",
                "ID NUMBER :     ${loanLookupData.idNumber}"
            )
        }

        PrinterConfigs.TYPE_OF_RECEIPT = "LOAN DISBURSEMENT"
        PrinterConfigs.AGENT_CODE = loginData.user.name
        PrinterConfigs.AGENT_NAME = loginData.user.name
        PrinterConfigs.AGENT_BRANCH_STREET = ""
        PrinterConfigs.SERVER_BY = loginData.user.name
        PrinterConfigs.TERMINAL_NO = ""
        PrinterConfigs.TIME_OF_TRANSACTION_REQUEST = getCurrentDateTimeString()
        PrinterConfigs.FINISH_ACTIVITY_ON_PRINT = true
        PrinterConfigs.HAS_SIGNATURE_BITMAP = false
        PrinterConfigs.QR_AUTH_CODE_TO_PRINT = ""


    }

}