package com.deefrent.rnd.fieldapp.view.homepage.loans.apply

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.digitalpersona.uareu.Reader
import com.digitalpersona.uareu.ReaderCollection
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentApplyLoanBinding
import com.deefrent.rnd.fieldapp.databinding.LoanDialogLayoutBinding
import com.deefrent.rnd.fieldapp.dtos.LoanRequestDTO
import com.deefrent.rnd.fieldapp.network.models.LoanPurposes
import com.deefrent.rnd.fieldapp.network.models.PeriodMeasure
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ApplyLoanFragment : BaseDaggerFragment() {
    private var _binding: FragmentApplyLoanBinding? = null
    private lateinit var cardBinding: LoanDialogLayoutBinding
    private val pinViewmodel: PinViewModel by activityViewModels()
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private val binding get() = _binding!!
    private var purposeId = ""
    private var paymentPeriodMeasure = ""
    private var paymentCycleMeasure = ""
    private var productId = ""
    private var idNO = ""
    private var assetPurchase = "NO"
    private var maxPeriod = ""
    private lateinit var calendar: Calendar

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyLoanBinding.inflate(layoutInflater)

        val args = ApplyLoanFragmentArgs.fromBundle(
            requireArguments()
        ).loanProductItem
        lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            idNO = it?.idNumber.toString()
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
        }
        binding.head.text = "Apply ${(args.name.capitalizeWords)}"
        binding.apply {
            lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
                it.periodMeasures.also { periodMeasuresList ->
                    populatePeriodCycles(periodMeasuresList)
                    populatePeriodMeasures(periodMeasuresList)
                }
            }
            lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
                populateLoanPurpose(it.loanPurposes)
            }
            tvAmountValue.text = args.limit
            tvFrequencyValue.text = args.interestRate
            tvPeriodValue.text = args.maxRepaymentPeriod
            val perio = args.maxRepaymentPeriod.replace("[^0-9]".toRegex(), "")
            maxPeriod = perio
            productId = args.productId.toString()
            etPaymentPeriod.setText(perio)
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
            rbOthers.isChecked = true
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    assetPurchase = "YES"
                    clOption.makeVisible()
                    rbOthers.isChecked = false
                }

            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    assetPurchase = "NO"
                    rbMyself.isChecked = false
                    clOption.makeGone()
                }

            }
            etApplicationDate.setOnClickListener {
                showDatePicker()
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
                val loanRequestDTO = LoanRequestDTO()
                //if (rbMyself.isChecked) {
                /*if (validateYesFields()) {
                    loanRequestDTO.idNumber = idNO
                    loanRequestDTO.productId = productId
                    loanRequestDTO.amount = etAmount.text.toString().trim()
                    loanRequestDTO.paymentFrequencyId = repaymentPeriodMeasure
                    loanRequestDTO.paymentPeriod = etPaymentPeriod.text.toString().trim()
                    loanRequestDTO.purposeId = purposeId
                    loanRequestDTO.assetSupplierName = etAssetSupplier.text.toString().trim()
                    loanRequestDTO.assetCost = etAssetcost.text.toString().trim()
                    loanRequestDTO.assetDescription = etDesc.text.toString().trim()
                    loanRequestDTO.supplierPhone = etSupplierPhone.text.toString().trim()
                    lookupViewmodel.applyLoan(loanRequestDTO)
                }*/
                // } else {
                if (validateNoFields()) {
                    loanRequestDTO.idNumber = idNO
                    loanRequestDTO.productId = productId
                    loanRequestDTO.amount = etAmount.text.toString().trim()
                    loanRequestDTO.repaymentPeriodMeasure = paymentPeriodMeasure
                    loanRequestDTO.repaymentPeriod = etPaymentPeriod.text.toString().trim()
                    loanRequestDTO.paymentCycleMeasure = paymentCycleMeasure
                    loanRequestDTO.paymentCycle = etPaymentCycle.text.toString().trim()
                    loanRequestDTO.purposeId = purposeId
                    loanRequestDTO.loanOfficerAmount = etLoanOfficerAmount.text.toString()
                    loanRequestDTO.loanOfficerRemarks = etLoanOfficerRemarks.text.toString()
                    loanRequestDTO.applicationDate = etApplicationDate.text.toString().trim()
                    lookupViewmodel.applyLoan(loanRequestDTO)
                }
                // }
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

            lookupViewmodel.applyStatusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    binding.progressbar.mainPBar.visibility = View.GONE
                    when (it) {
                        1 -> {
                            lookupViewmodel.stopObserving()
                            btnContinue.isEnabled = true
                            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
                            cardBinding =
                                LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
                            cardBinding.apply {
                                //if (rbOthers.isChecked) {
                                tvAmountValue.text =
                                    FormatDigit.formatDigits(etAmount.text.toString().trim())
                                lookupViewmodel.apply {
                                    repaymentPeriodMeasure.observe(viewLifecycleOwner) { repaymentPeriodMeasure ->
                                        repaymentPeriod.observe(viewLifecycleOwner) { repaymentPeriod ->
                                            tvFrequencyValue.text =
                                                "$repaymentPeriod $repaymentPeriodMeasure"
                                        }
                                    }
                                    repaymentPeriodCycleMeasure.observe(viewLifecycleOwner) { repaymentPeriodCycleMeasure ->
                                        repaymentPeriodCycle.observe(viewLifecycleOwner) { repaymentPeriodCycle ->
                                            tvPeriodCycleValue.text =
                                                "$repaymentPeriodCycle $repaymentPeriodCycleMeasure"
                                        }
                                    }
                                    applicationDate.observe(viewLifecycleOwner) { applicationDate ->
                                        tvAsset.text = "APPLICATION DATE:"
                                        tvAssetValue.text = applicationDate
                                    }
                                }
                                lookupViewmodel.charges.observe(viewLifecycleOwner) { charge ->
                                    tvChargeValue.text = FormatDigit.formatDigits(charge)
                                }
                                tvPhone.text = "LOAN OFFICER AMOUNT:"
                                tvPhoneValue.text = FormatDigit.formatDigits(
                                    etLoanOfficerAmount.text.toString().trim()
                                )
                                tvSupplier.makeGone()
                                tvSupplierValue.makeGone()
                                tvAName.makeGone()
                                tvANameValue.makeGone()
                                tvAssetCost.makeGone()
                                tvAssetCostValue.makeGone()
                                /*} else {
                                    tvAmountValue.text =
                                        FormatDigit.formatDigits(etAmount.text.toString().trim())
                                    tvFrequencyValue.text =
                                        binding.acPaymentPeriodMeasure.text.toString()
                                    tvPeriodValue.text = etPaymentPeriod.text.toString().trim()
                                    tvAssetValue.text = assetPurchase
                                    lookupViewmodel.charges.observe(viewLifecycleOwner) { charge ->
                                        tvChargeValue.text = FormatDigit.formatDigits(charge)
                                    }
                                    tvSupplierValue.text = etAssetSupplier.text.toString().trim()
                                    tvAssetCostValue.text = etAssetcost.text.toString().trim()
                                    tvPhoneValue.text = etSupplierPhone.text.toString().trim()
                                    tvANameValue.text = etDesc.text.toString().trim()
                                }*/
                            }

                            cardBinding.btnCancel.setOnClickListener {
                                dialog.dismiss()
                            }
                            cardBinding.btnConfirm.setOnClickListener {
                                dialog.dismiss()
                                dialog.hide()
                                findNavController().navigate(R.id.action_applyLoanFragment_to_authPinFragment)
                                lookupViewmodel.stopObserving()
                            }

                            dialog.setContentView(cardBinding.root)
                            dialog.show()
                            dialog.setCancelable(false)
                        }

                        0 -> {
                            btnContinue.isEnabled = true
                            lookupViewmodel.stopObserving()
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                        }

                        else -> {
                            btnContinue.isEnabled = true
                            lookupViewmodel.stopObserving()
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    pinViewmodel.unsetAuthSuccess()
                    binding.progressbar.mainPBar.visibility = View.VISIBLE
                    binding.progressbar.tvWait.text = getString(R.string.we_are_processing_requesrt)
                    lookupViewmodel.loanApplyCommit()
                    pinViewmodel.stopObserving()
                    lookupViewmodel.stopObserving()
                }
            }
            lookupViewmodel.statusCommit.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            binding.apply {
                            }
                            pinViewmodel.getWalletAccountBal()
                            binding.progressbar.mainPBar.makeGone()
                            val direction =
                                ApplyLoanFragmentDirections.actionApplyLoanFragmentToLoanSuccessFragment(
                                    0
                                )
                            findNavController().navigate(direction)
                            lookupViewmodel.stopObserving()
                            // dashboardModel.setRefresh(true)
                        }

                        0 -> {
                            lookupViewmodel.stopObserving()
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                        }

                        else -> {
                            lookupViewmodel.stopObserving()
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(getString(R.string.error_occurred))
                        }
                    }
                    lookupViewmodel.stopObserving()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        /* if (commonSharedPreferences.getStringData(LoginFingerPrintCaptureActivity.IS_AUTH_SUCCESSFUL) == "1") {
             pinViewmodel.unsetAuthSuccess()
             binding.progressbar.mainPBar.visibility = View.VISIBLE
             binding.progressbar.tvWait.text = getString(R.string.we_are_processing_requesrt)
             lookupViewmodel.loanApplyCommit()
             pinViewmodel.stopObserving()
             lookupViewmodel.stopObserving()
         }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun populatePeriodMeasures(periodMeasuresList: List<PeriodMeasure>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, periodMeasuresList)
        binding.acPaymentPeriodMeasure.setAdapter(typeAdapter)
        binding.acPaymentPeriodMeasure.keyListener = null
        binding.acPaymentPeriodMeasure.setOnItemClickListener { parent, _, position, _ ->
            val selected: PeriodMeasure = parent.adapter.getItem(position) as PeriodMeasure
            binding.acPaymentPeriodMeasure.setText(selected.label, false)
            paymentPeriodMeasure = selected.id.toString()
        }
    }

    private fun populatePeriodCycles(periodMeasuresList: List<PeriodMeasure>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, periodMeasuresList)
        binding.acPaymentCycleMeasure.setAdapter(typeAdapter)
        binding.acPaymentCycleMeasure.keyListener = null
        binding.acPaymentCycleMeasure.setOnItemClickListener { parent, _, position, _ ->
            val selected: PeriodMeasure = parent.adapter.getItem(position) as PeriodMeasure
            binding.acPaymentCycleMeasure.setText(selected.label, false)
            paymentCycleMeasure = selected.id.toString()
        }
    }

    private fun populateLoanPurpose(loanPurpose: List<LoanPurposes>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, loanPurpose)
        binding.spPurpose.setAdapter(typeAdapter)
        binding.spPurpose.keyListener = null
        binding.spPurpose.setOnItemClickListener { parent, _, position, _ ->
            val selected: LoanPurposes = parent.adapter.getItem(position) as LoanPurposes
            binding.spPurpose.setText(selected.name, false)
            purposeId = selected.id.toString()
        }
    }

    private fun validateYesFields(): Boolean {
        var isValid = false
        binding.apply {
            val amount = etAmount.text.toString().trim()
            val period = etPaymentPeriod.text.toString().trim()
            val supplier = etAssetSupplier.text.toString().trim()
            val cost = etAssetcost.text.toString().trim()
            val phone = etSupplierPhone.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            if (amount.isEmpty()) {
                tlEnterAmount.error = "required"
                isValid = false
            } else if (binding.acPaymentPeriodMeasure.text.isEmpty()) {
                toastyErrors("Select payment period")
                isValid = false
            } else if (period.isEmpty()) {
                tiPaymentPeriod.error = "required"
                isValid = false
            } else if (period > maxPeriod) {
                toastyErrors("Payment period should not be more than the required maximum repayment period")
            } else if (supplier.isEmpty()) {
                tlAssetSupplier.error = "required"
                isValid = false
            } else if (cost.isEmpty()) {
                tlAssetCost.error = "required"
                isValid = false
            } else if (phone.isEmpty()) {
                tlSupplierPhone.error = "required"
                isValid = false
            } else if (desc.isEmpty()) {
                tldesc.error = "required"
                isValid = false
            } else {
                isValid = true
                lookupViewmodel.amount.postValue(etAmount.text.toString().trim())
                lookupViewmodel.repaymentPeriodMeasure.postValue(binding.acPaymentPeriodMeasure.text.toString())
                lookupViewmodel.repaymentPeriod.postValue(etPaymentPeriod.text.toString().trim())
                lookupViewmodel.codition.postValue(assetPurchase)
                lookupViewmodel.supplierName.postValue(etAssetSupplier.text.toString().trim())
                lookupViewmodel.cost.postValue(etAssetcost.text.toString().trim())
                lookupViewmodel.supplierPhone.postValue(etSupplierPhone.text.toString().trim())
                lookupViewmodel.descri.postValue(desc)
                lookupViewmodel.loanOfficerAmount.postValue(etLoanOfficerAmount.text.toString())
                lookupViewmodel.loanOfficerRemarks.postValue(etLoanOfficerRemarks.text.toString())
            }
        }
        return isValid
    }

    private fun validateNoFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            val amount = etAmount.text.toString().trim()
            val period = etPaymentPeriod.text.toString().trim()
            when {
                /*
                    loanRequestDTO.paymentCycleMeasure = paymentCycleMeasure
                    loanRequestDTO.paymentCycle = etPaymentCycle.text.toString().trim()*/
                amount.isEmpty() -> {
                    tlEnterAmount.error = "Required"
                    isValid = false
                }

                etLoanOfficerAmount.text.toString().isEmpty() -> {
                    tiLoanOfficerAmount.error = "Required"
                    isValid = false
                }

                binding.acPaymentPeriodMeasure.text.toString().isEmpty() -> {
                    toastyErrors("Select payment period measure")
                    isValid = false
                }

                period.isEmpty() -> {
                    tiPaymentPeriod.error = "Required"
                    isValid = false
                }

                binding.acPaymentCycleMeasure.text.toString().isEmpty() -> {
                    toastyErrors("Select payment cycle measure")
                    isValid = false
                }

                etPaymentCycle.text.toString().isEmpty() -> {
                    tiPaymentCycle.error = "Required"
                    isValid = false
                }

                etLoanOfficerRemarks.text.toString().isEmpty() -> {
                    tiLoanOfficerRemarks.error = "Required"
                    isValid = false
                }

                etApplicationDate.text.toString().isEmpty() -> {
                    tiApplicationDate.error = "Required"
                    isValid = false
                }
                /*period > maxPeriod -> {
                    toastyErrors("Payment period should not be more than the required maximum repayment period")
                }*/
                else -> {
                    tlEnterAmount.error = ""
                    isValid = true
                    lookupViewmodel.amount.postValue(etAmount.text.toString().trim())
                    lookupViewmodel.repaymentPeriodMeasure.postValue(
                        binding.acPaymentPeriodMeasure.text.toString().trim()
                    )
                    lookupViewmodel.repaymentPeriod.postValue(
                        etPaymentPeriod.text.toString().trim()
                    )
                    lookupViewmodel.repaymentPeriodCycleMeasure.postValue(
                        acPaymentCycleMeasure.text.toString().trim()
                    )
                    lookupViewmodel.repaymentPeriodCycle.postValue(
                        etPaymentCycle.text.toString().trim()
                    )
                    lookupViewmodel.applicationDate.postValue(
                        etApplicationDate.text.toString().trim()
                    )
                    lookupViewmodel.loanOfficerAmount.postValue(etLoanOfficerAmount.text.toString())
                    lookupViewmodel.loanOfficerRemarks.postValue(etLoanOfficerRemarks.text.toString())
                }
            }
        }
        return isValid
    }

    /**
     *CONFIGURE FINGERPRINT
     */
    /**
     * FINGER PRINT CONFIGURATION
     */
    private var m_deviceName = ""
    private var MY_PERMISSIONS = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
        "android.permission.WRITE_OWNER_DATA",
        "android.permission.READ_OWNER_DATA",
        "android.hardware.usb.accessory",
        "USB_PERMISSION",
        "android.permission.HARDWARE_TEST",
        "android.hardware.usb.host"
    )
    private val REQUEST_CODE = 1
    private lateinit var m_reader: Reader//? = null
    private lateinit var readers: ReaderCollection
    private val ACTION_USB_PERMISSION = "USB_PERMISSION"
    private var mPermissionIntent: PendingIntent? = null


}