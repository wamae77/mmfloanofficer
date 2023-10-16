package com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentUpdateLoanBinding
import com.deefrent.rnd.fieldapp.databinding.LoanDialogLayoutBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateLoanDTO
import com.deefrent.rnd.fieldapp.network.models.LoansPendingApproval
import com.deefrent.rnd.fieldapp.network.models.PeriodMeasure
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateLoanFragment : Fragment() {
    private lateinit var binding: FragmentUpdateLoanBinding
    private lateinit var cardBinding: LoanDialogLayoutBinding
    private var national_identity = ""
    private var periodCycleID = ""
    private var periodMeasureID = ""
    private var cycleID = ""
    private var currency = ""
    private var loanAccountNumber = ""
    private val pinViewmodel: PinViewModel by activityViewModels()
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private lateinit var args: LoansPendingApproval
    private lateinit var calendar: Calendar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateLoanBinding.inflate(layoutInflater)
        args = UpdateLoanFragmentArgs.fromBundle(requireArguments()).pendingloans
        loanAccountNumber = args.loanAccountNo
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.apply {
            head.text = args.name.capitalizeWords
            etAmount.setText(args.loanOfficerAmount)
            etpaymentCycle.setText(args.paymentCycle)
            etApplicationDate.setText(args.applicationDate)
            etPayPeriod.setText(args.paymentPeriod)
            etOfficerRemarks.setText(args.loanOfficerRemarks)
            val finalAmount = FormatDigit.formatDigits(args.amountApplied)
            tvAmountValue.text = "${args.currency} $finalAmount"
            lookupViewmodel.currency.postValue(args.currency)
            lookupViewmodel.descri.postValue(args.name)
            currency = args.currency
            etCustomerAmount.setText(args.amountApplied)
            // tvFrequencyValue.text = "${args.paymentCycle}"
            binding.etApplicationDate.keyListener = null
            binding.etApplicationDate.setOnClickListener {
                showDatePicker()
            }
        }
        lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            national_identity = it.idNumber
            if (it.periodMeasures.isNotEmpty()) {
                populatePaymentMeasure(it.periodMeasures)
                populatePeriodMeasure(it.periodMeasures)
            }
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
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
            binding.etApplicationDate.setText(sdf.format(calendar.time))
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
                val amount = etAmount.text.toString().trim()
                val paymentCycle = etpaymentCycle.text.toString().trim()
                val cycleMeasure = etCycleMeasure.text.toString().trim()
                val paymentPeriod = etPayPeriod.text.toString().trim()
                val periodMeasure = etPeriodMeasure.text.toString().trim()
                if (amount.isEmpty() || paymentCycle.isEmpty() || cycleMeasure.isEmpty()
                    || paymentPeriod.isEmpty() || periodMeasure.isEmpty() || etApplicationDate.text.toString()
                        .trim().isEmpty()
                    || etCustomerAmount.text.toString().trim().isEmpty()
                ) {
                    toastyErrors("Fill out all the mandatory fields")
                } else {
                    lookupViewmodel.amount.postValue(etCustomerAmount.text.toString().trim())
                    lookupViewmodel.loanOfficerAmount.postValue(etAmount.text.toString().trim())
                    lookupViewmodel.repaymentPeriodCycle.postValue(binding.etpaymentCycle.text.toString())
                    lookupViewmodel.repaymentPeriodCycleMeasure.postValue(
                        etCycleMeasure.text.toString().trim()
                    )
                    lookupViewmodel.repaymentPeriodMeasure.postValue(binding.etPeriodMeasure.text.toString())
                    lookupViewmodel.repaymentPeriod.postValue(etPayPeriod.text.toString().trim())
                    lookupViewmodel.applicationDate.postValue(
                        etApplicationDate.text.toString().trim()
                    )
                    lookupViewmodel.loanOfficerRemarks.postValue(etOfficerRemarks.text.toString())
                    showCardDialog()
                }
            }
            lookupViewmodel.responseUpdateStatus.observe(viewLifecycleOwner) {
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
            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    if (isNetworkAvailable(requireContext())) {
                        pinViewmodel.unsetAuthSuccess()
                        val updateLoanDTO = UpdateLoanDTO()
                        updateLoanDTO.loanAccountNo = loanAccountNumber
                        updateLoanDTO.loanOfficerAmount = etAmount.text.toString().trim()
                        updateLoanDTO.paymentCycle = etpaymentCycle.text.toString().trim()
                        updateLoanDTO.paymentCycleMeasure = cycleID
                        updateLoanDTO.paymentPeriod = etPayPeriod.text.toString().trim()
                        updateLoanDTO.paymentPeriodMeasure = periodCycleID
                        updateLoanDTO.loanOfficerRemarks = etOfficerRemarks.text.toString().trim()
                        updateLoanDTO.applicationDate = etApplicationDate.text.toString().trim()
                        updateLoanDTO.amount = etCustomerAmount.text.toString().trim()
                        lookupViewmodel.updateLoan(updateLoanDTO)
                        pinViewmodel.stopObserving()
                        lookupViewmodel.stopObserving()
                    } else {
                        toastyErrors("Check your internet connection and try again")
                    }

                }
            }
            lookupViewmodel.statusUpdateCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            binding.apply {
                            }
                            val direction =
                                UpdateLoanFragmentDirections.actionUpdateLoanFragmentToLoanSuccessFragment(
                                    4
                                )
                            findNavController().navigate(direction)
                            lookupViewmodel.stopObserving()
                            // dashboardModel.setRefresh(true)
                        }
                        0 -> {
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                            lookupViewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            lookupViewmodel.stopObserving()

                        }
                    }
                }
            }
        }

    }

    private fun showCardDialog() {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        cardBinding = LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
        cardBinding.apply {
            val finalAmnt = FormatDigit.formatDigits(binding.etAmount.text.toString().trim())
            tvAmount.text = "LOAN OFFICER AMOUNT"
            tvAmountValue.text = "$currency $finalAmnt"
            tvFrequency.text = "PAY EVERY:"
            tvFrequencyValue.text = "${binding.etpaymentCycle.text.toString()} ${
                binding.etCycleMeasure.text.toString().trim()
            }"
            tvAsset.text = "REPAYMENT PERIOD (TENURE):"
            tvAssetValue.text = "${
                binding.etPayPeriod.text.toString().trim()
            } ${binding.etPeriodMeasure.text.toString().trim()}"
            tvSupplierValue.text = binding.etApplicationDate.text.toString().trim()
            tvSupplier.text = "APPLICATION DATE:"
            tvPeriodCycle.text = "CUSTOMER AMOUNT:"
            val customerAmount = FormatDigit.formatDigits(binding.etCustomerAmount.text.toString().trim())
            tvPeriodCycleValue.text = "$currency $customerAmount"
            tvCharge.makeGone()
            tvChargeValue.makeGone()
            tvPhone.makeGone()
            tvAssetCost.makeGone()
            tvAssetCostValue.makeGone()
            tvPhoneValue.makeGone()
            tvAName.makeGone()
            tvANameValue.makeGone()
        }

        cardBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            dialog.hide()
            findNavController().navigate(R.id.action_updateLoanFragment_to_authPinFragment)
            lookupViewmodel.stopObserving()
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)

    }

    private fun populatePaymentMeasure(genderList: List<PeriodMeasure>) {
        binding.etCycleMeasure.setText(args.decodedPaymentCycleMeasure)
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.etCycleMeasure.setAdapter(typeAdapter)
        binding.etCycleMeasure.keyListener = null
        binding.etCycleMeasure.setOnItemClickListener { parent, _, position, _ ->
            val selected: PeriodMeasure = parent.adapter.getItem(position) as PeriodMeasure
            cycleID = selected.id.toString()
            lookupViewmodel.repaymentPeriod.postValue(selected.label)
            binding.etCycleMeasure.setText(selected.label, false)
        }
    }

    private fun populatePeriodMeasure(genderList: List<PeriodMeasure>) {
        binding.etPeriodMeasure.setText(args.decodedPaymentPeriodMeasure)
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.etPeriodMeasure.setAdapter(typeAdapter)
        binding.etPeriodMeasure.keyListener = null
        binding.etPeriodMeasure.setOnItemClickListener { parent, _, position, _ ->
            val selected: PeriodMeasure = parent.adapter.getItem(position) as PeriodMeasure
            periodCycleID = selected.id.toString()
            lookupViewmodel.supplierName.postValue(selected.label)
            binding.etPeriodMeasure.setText(selected.label, false)
        }
    }
}


