package com.deefrent.rnd.fieldapp.view.homepage.loans.payloan

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.CURRENCY_CODE
import com.deefrent.rnd.common.utils.SUCCESS_DESCRIPTION
import com.deefrent.rnd.common.utils.SUCCESS_DIALOGDETAILCOMMON
import com.deefrent.rnd.common.utils.SUCCESS_TITLE
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanPaymentBinding
import com.deefrent.rnd.fieldapp.databinding.LoanDialogLayoutBinding
import com.deefrent.rnd.fieldapp.dtos.PayLoanDTO
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.network.models.LoginData
import com.deefrent.rnd.fieldapp.network.models.RepayableLoan
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import com.deefrent.rnd.fieldapp.view.printreceipt.MoneyMartPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LoanPaymentFragment : BaseDaggerFragment() {
    private var _binding: FragmentLoanPaymentBinding? = null
    private lateinit var cardBinding: LoanDialogLayoutBinding
    private val pinViewmodel: PinViewModel by activityViewModels()
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private lateinit var loanLookupData: LoanLookupData
    private lateinit var payableLoans: RepayableLoan
    private lateinit var loginData: LoginData
    private var dialogDetailCommonHashSet = ArrayList<DialogDetailCommon>()
    private val binding get() = _binding!!
    private var national_id = ""
    private var loanAccNo = ""
    private var loanBal = ""
    private lateinit var calendar: Calendar

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoanPaymentBinding.inflate(layoutInflater)

        loanLookupData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.LOANLOOKUPDATA),
            LoanLookupData::class.java
        )

        loginData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CURRENY_USER_DATA),
            LoginData::class.java
        )

        binding.rbMyself.isChecked = true
        binding.etPaymentDateFull.keyListener = null
        binding.etPaymentDateFull.setOnClickListener {
            showDatePicker()
        }
        binding.etPaymentDatePartial.keyListener = null
        binding.etPaymentDatePartial.setOnClickListener {
            showDatePicker()
        }
        val args = LoanPaymentFragmentArgs.fromBundle(requireArguments()).payableLoans
        lookupViewmodel.addPayAbleLoans(args)
        payableLoans = args
        lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            national_id = it?.idNumber.toString()
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" + "\n$idNumber"
            )
        }
        binding.head.text = "Repay ${args.name.capitalizeWords}"
        binding.apply {
            val applied = FormatDigit.formatDigits(args.amountApplied)
            val approved = FormatDigit.formatDigits(args.amountApproved)
            val bal = FormatDigit.formatDigits(args.balance)
            tvFrequencyValue.text = "${args.currency} $applied"
            tvPeriodValue.text = "${args.currency} $approved"
            tvLoanBalValue.text = "${args.currency} $bal"
            loanAccNo = args.loanAccountNo
            loanBal = args.balance
            binding.etAmount.setText(FormatDigit.formatDigits(args.balance))
            etPayPeriod.setText(args.loanAccountNo)
            etPPeriod.setText(args.loanAccountNo)
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.Full.makeVisible()
                    binding.Partial.makeGone()
                    binding.etAmount.isFocusable = false
                    binding.etAmount.setText(FormatDigit.formatDigits(args.balance))
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

            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v).navigateUp()
            }
        }
        return binding.root
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
                binding.etPaymentDateFull.setText(sdf.format(calendar.time))
            } else if (binding.rbOthers.isChecked) {
                binding.etPaymentDatePartial.setText(sdf.format(calendar.time))
            }

        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        //dialog.datePicker.minDate = calendar.timeInMillis
        //calendar.add(Calendar.YEAR, -18)
        dialog.datePicker.maxDate = calendar.timeInMillis
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {

                    val payLoanDTO = PayLoanDTO()
                    if (rbMyself.isChecked) {
                        if (validateYesFields()) {
                            payLoanDTO.loanAccountNo = loanAccNo
                            payLoanDTO.idNumber = national_id
                            payLoanDTO.description = etPreason.text.toString().trim()
                            payLoanDTO.payAll = 1
                            payLoanDTO.amount = loanBal
                            payLoanDTO.repaymentDate = etPaymentDateFull.text.toString().trim()
                            if (binding.useMpesaCheckBox.isChecked) {
                                lookupViewmodel.payLoanMpesaPreview(payLoanDTO)
                                    ?.let { id ->
                                        toastyErrors(getString(id))
                                    }
                            } else {
                                lookupViewmodel.payLoanPreview(payLoanDTO)
                            }
                        }
                    } else {
                        if (validateNoFields()) {
                            payLoanDTO.loanAccountNo = loanAccNo
                            payLoanDTO.idNumber = national_id
                            payLoanDTO.description = etPreason.text.toString().trim()
                            payLoanDTO.payAll = 0
                            payLoanDTO.amount = etPamount.text.toString().trim()
                            payLoanDTO.repaymentDate = etPaymentDatePartial.text.toString().trim()
                            if (binding.useMpesaCheckBox.isChecked) {
                                lookupViewmodel.payLoanMpesaPreview(payLoanDTO)
                                    ?.let { id ->
                                        toastyErrors(getString(id))
                                    }
                            } else {
                                lookupViewmodel.payLoanPreview(payLoanDTO)
                            }
                        }

                    }
                } else {
                    toastyErrors("Check your internet connection and try again")
                }
            }
            lookupViewmodel.payResponseStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnContinue.isEnabled = false
                            binding.progressbar.mainPBar.visibility = View.VISIBLE

                        }

                        GeneralResponseStatus.DONE -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                        }

                        else -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                        }
                    }
                }
            }
            lookupViewmodel.payStatusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            lookupViewmodel.stopObserving()
                            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
                            cardBinding =
                                LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
                            cardBinding.apply {
                                if (rbMyself.isChecked) {
                                    tvAmountValue.text = FormatDigit.formatDigits(loanBal)
                                    tvPhoneValue.text = etPaymentDateFull.text.toString()
                                } else {
                                    tvAmountValue.text =
                                        FormatDigit.formatDigits(etPamount.text.toString().trim())
                                    tvPhoneValue.text = etPaymentDatePartial.text.toString()
                                }
                                tvHeading.text = "Confirm Loan Repayment"
                                tvFrequency.text = "Loan account number"
                                tvFrequencyValue.text = loanAccNo
                                tvPeriodValue.makeGone()
                                tvPeriod.makeGone()
                                tvPeriodCycle.makeGone()
                                tvPeriodCycleValue.makeGone()
                                tvAssetValue.makeGone()
                                tvAsset.makeGone()
                                lookupViewmodel.charges.observe(viewLifecycleOwner) { charge ->
                                    tvChargeValue.text = FormatDigit.formatDigits(charge)
                                }
                                tvSupplierValue.makeGone()
                                tvSupplier.makeGone()
                                tvPhone.text = "REPAYMENT DATE:"
                                tvAssetCost.makeGone()
                                tvAssetCostValue.makeGone()
                                tvAName.makeGone()
                                tvANameValue.makeGone()
                            }

                            cardBinding.btnCancel.setOnClickListener {
                                dialog.dismiss()
                            }
                            cardBinding.btnConfirm.setOnClickListener {
                                dialog.dismiss()
                                dialog.hide()
                                findNavController().navigate(R.id.action_loanPaymentFragment_to_authPinFragment)
                                lookupViewmodel.stopObserving()
                            }

                            dialog.setContentView(cardBinding.root)
                            dialog.show()
                            dialog.setCancelable(false)

                        }

                        0 -> {
                            lookupViewmodel.stopObserving()
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                        }

                        else -> {
                            lookupViewmodel.stopObserving()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    btnContinue.isEnabled = false
                    pinViewmodel.unsetAuthSuccess()
                    binding.progressbar.mainPBar.visibility = View.VISIBLE
                    binding.progressbar.tvWait.text = getString(R.string.we_are_processing_requesrt)
                    lookupViewmodel.payLoanCommit()
                    pinViewmodel.stopObserving()
                    lookupViewmodel.stopObserving()
                }
            }

            lookupViewmodel.statusCommit.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            pinViewmodel.getWalletAccountBal()
                            btnContinue.isEnabled = true
                            binding.apply {}
                            binding.progressbar.mainPBar.makeGone()/* val direction =
                                 LoanPaymentFragmentDirections.actionLoanPaymentFragmentToLoanSuccessFragment(
                                     2
                                 )
                             findNavController().navigate(direction)*/
                            showSuccessScreen()
                            commonSharedPreferences.setIsFingerPrintDone(false)
                            commonSharedPreferences.setIsPrintReceipt(true)
                            lookupViewmodel.stopObserving()
                        }

                        0 -> {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        /*if (commonSharedPreferences.getStringData(LoginFingerPrintCaptureActivity.IS_AUTH_SUCCESSFUL) == "1") {
            binding.btnContinue.isEnabled = false
            pinViewmodel.unsetAuthSuccess()
            binding.progressbar.mainPBar.visibility = View.VISIBLE
            binding.progressbar.tvWait.text = getString(R.string.we_are_processing_requesrt)
            lookupViewmodel.payLoanCommit()
            pinViewmodel.stopObserving()
            lookupViewmodel.stopObserving()
        }*/
    }

    private fun validateYesFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            val amount = etAmount.text.toString().trim()
            val loanAcc = etPayPeriod.text.toString().trim()
            val purpose = etReason.text.toString().trim()
            when {
                loanAcc.isEmpty() -> {
                    isValid = false
                    tlPayPeriod.error = "Required"
                }

                etPaymentDateFull.text.toString().isEmpty() -> {
                    isValid = false
                    tiPaymentDateFull.error = "Required"
                }

                else -> {
                    isValid = true
                    tlPayPeriod.error = ""
                    lookupViewmodel.repaymentPeriod.postValue(loanAccNo)
                    lookupViewmodel.amount.postValue(loanBal)
                    lookupViewmodel.repaymentDate.postValue(etPaymentDateFull.text.toString())
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
            when {
                amount.isEmpty() -> {
                    isValid = false
                    tlPAmount.error = "Required"
                }

                loanAcc.isEmpty() -> {
                    isValid = false
                    tlPPeriod.error = "Required"
                }

                etPaymentDatePartial.text.toString().isEmpty() -> {
                    isValid = false
                    tiPaymentDatePartial.error = "Required"
                }

                else -> {
                    isValid = true
                    tlPAmount.error = ""
                    tlPPeriod.error = ""
                    lookupViewmodel.repaymentPeriod.postValue(loanAccNo)
                    lookupViewmodel.amount.postValue(etPamount.text.toString().trim())
                    lookupViewmodel.repaymentDate.postValue(etPaymentDatePartial.text.toString())
                }
            }
        }
        return isValid
    }

    private fun showSuccessScreen() {
        val bundle = Bundle()
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "Amount:",
                content = "$CURRENCY_CODE ${binding.etAmount.text.toString().trim()}"
            )
        )
        lookupViewmodel.apply {
            repaymentPeriod.observe(viewLifecycleOwner) {
                dialogDetailCommonHashSet.add(
                    DialogDetailCommon(
                        label = "Loan Account Number", content = "${it} "
                    )
                )
            }
            repaymentDate.observe(viewLifecycleOwner) {
                dialogDetailCommonHashSet.add(
                    DialogDetailCommon(
                        label = "Repayment Date:", content = it
                    )
                )
            }
            charges.observe(viewLifecycleOwner) { charge ->
                dialogDetailCommonHashSet.add(
                    DialogDetailCommon(
                        label = "Charges :",
                        content = "${FormatDigit.formatDigits("$CURRENCY_CODE ${charge.toString()}")} "
                    )
                )
            }
        }

        bundle.putString(SUCCESS_TITLE, "Loan Repayment Successful")
        bundle.putString(
            SUCCESS_DESCRIPTION, "Loan repayment has been processed successfully"
        )

        bundle.putParcelableArrayList(SUCCESS_DIALOGDETAILCOMMON, dialogDetailCommonHashSet)
        findNavController().navigate(
            R.id.generalSuccessfulFragment, bundle
        )
        dataToPrint()
        commonSharedPreferences.setIsPrintReceipt(true)

    }


    private fun dataToPrint() {
        val amountToPay = if (binding.rbMyself.isChecked) {
            binding.etAmount.text.toString()
        } else {
            binding.etPamount.text.toString()
        }
        lookupViewmodel.payLoanCommitData.observe(viewLifecycleOwner) {
            PrinterConfigs.TRANSACTION_REFERENCE = it.transactionCode
            PrinterConfigs.RECEIPT_TEXT_ARRAY = arrayOf<String>(
                MoneyMartPrintServiceActivity.centeredText("LOAN REPAYMENT", 48),
                MoneyMartPrintServiceActivity.centeredText("Loan Repayment Successful", 44),
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "Amount:         $CURRENCY_CODE ${amountToPay}",
                "Loan:  ${payableLoans.name}",
                "Loan Balance:   $CURRENCY_CODE ${it.loanBalance}",
                "REF ID:   $CURRENCY_CODE ${it.transactionCode}",
                "Loan Tenure:    ${payableLoans.loanTenure}",
                "Loan Interest:  ${payableLoans.interestRate}",
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "\n",
                "CUSTOMER NAME:  ${loanLookupData.firstName} ${loanLookupData.lastName}",
                "ID NUMBER :     ${loanLookupData.idNumber}"
            )
        }

        PrinterConfigs.TYPE_OF_RECEIPT = "LOAN REPAYMENT"
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