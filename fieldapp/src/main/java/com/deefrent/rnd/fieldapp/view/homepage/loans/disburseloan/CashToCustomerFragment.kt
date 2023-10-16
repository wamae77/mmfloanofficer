package com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCashToCustomerBinding
import com.deefrent.rnd.fieldapp.databinding.LoanDialogLayoutBinding
import com.deefrent.rnd.fieldapp.dtos.CashOutDTO
import com.deefrent.rnd.fieldapp.dtos.DisburseLoanPreviewDTO
import com.deefrent.rnd.fieldapp.network.models.WalletAccount
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel


class CashToCustomerFragment : Fragment() {
    private lateinit var binding: FragmentCashToCustomerBinding
    private lateinit var cardBinding: LoanDialogLayoutBinding
    private val pinViewmodel: PinViewModel by activityViewModels()
    var formatedAccount = ""
    private var national_identity = ""
    private var depositNumber = ""
    private var loanAccountNumber = ""
    private val lookupViewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashToCustomerBinding.inflate(layoutInflater)
        //  val args = DisburseLoanFragmentArgs.fromBundle(requireArguments()).disburseLoan
        //binding.rbMyself.isChecked = true
        binding.apply {
            etAmount.isFocusable = false
            ivBack.setOnClickListener {
                findNavController().navigate(R.id.loanHomeFragment)
            }
            lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
                national_identity = it.idNumber
                if (it.walletAccounts.isNotEmpty()) {
                    populateWalletAcc(it.walletAccounts)
                    populatePartialWalletAcc(it.walletAccounts)
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
                    //   binding.etAmount.setText(FormatDigit.formatDigits(args.remainingAmount))
                    /*rbOthers.isChecked = false
                    rbMyself.isChecked = true*/
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.Full.makeGone()
                    binding.Partial.makeVisible()
                    /*rbMyself.isChecked = false
                    rbOthers.isChecked = true*/
                }

            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (binding.rbMyself.isChecked) {
                etAmount.isEnabled = false
            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val disburseLoanPreviewDTO = DisburseLoanPreviewDTO()
                    disburseLoanPreviewDTO.idNumber = national_identity
                    if (binding.rbMyself.isChecked || binding.rbOthers.isChecked) {
                        if (rbMyself.isChecked) {
                            if (validateYesFields()) {
                                disburseLoanPreviewDTO.amount = etAmount.text.toString().trim()
                                lookupViewmodel.cashOutLoan(disburseLoanPreviewDTO)
                            }
                        } else {
                            if (validateNoFields()) {
                                disburseLoanPreviewDTO.amount = etPamount.text.toString().trim()
                                lookupViewmodel.cashOutLoan(disburseLoanPreviewDTO)
                            }
                        }
                    } else {
                        toastyErrors("Please select either Full Amount or Partial Amount")
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
            lookupViewmodel.cashStatusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
                            cardBinding =
                                LoanDialogLayoutBinding.inflate(LayoutInflater.from(context))
                            cardBinding.apply {
                                tvAmount.text = "Amount:"
                                tvHeading.text = "Confirm Cash-Out"
                                tvFrequency.text = "Cash-Out From:"
                                tvPeriodCycle.makeGone()
                                tvPeriodCycleValue.makeGone()
                                tvCharge.makeGone()
                                tvChargeValue.makeGone()
                                tvPeriod.makeGone()
                                tvPeriodValue.makeGone()
                                tvAssetValue.makeGone()
                                tvAsset.makeGone()
                                lookupViewmodel.previewData.observe(viewLifecycleOwner) { charge ->
                                    if (rbMyself.isChecked) {
                                        tvFrequencyValue.text = spDeposit.text.toString()
                                        tvAmountValue.text = "${charge.currency} ${
                                            FormatDigit.formatDigits(
                                                etAmount.text.toString().trim()
                                            )
                                        }"
                                    } else {
                                        tvFrequencyValue.text = etPPeriod.text.toString()
                                        tvAmountValue.text = "${charge.currency} ${
                                            FormatDigit.formatDigits(
                                                etPamount.text.toString().trim()
                                            )
                                        }"
                                    }
                                    tvChargeValue.text =
                                        FormatDigit.formatDigits("${charge.currency} ${charge.fee}")
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
                                findNavController().navigate(R.id.action_cashToCustomerFragment_to_authPinFragment)
                                lookupViewmodel.stopObserving()
                            }

                            dialog.setContentView(cardBinding.root)
                            dialog.show()
                            dialog.setCancelable(false)
                            lookupViewmodel.stopObserving()

                        }
                        0 -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(lookupViewmodel.statusMessage.value)
                            lookupViewmodel.stopObserving()
                        }
                        else -> {
                            btnContinue.isEnabled = true
                            binding.progressbar.mainPBar.visibility = View.GONE
                            onInfoDialog(getString(R.string.error_occurred))
                            lookupViewmodel.stopObserving()

                        }
                    }
                }
            }
            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    if (isNetworkAvailable(requireContext())) {
                        pinViewmodel.unsetAuthSuccess()
                        val cashOutDTO = CashOutDTO()
                        cashOutDTO.walletAccountNo = depositNumber
                        cashOutDTO.idNumber = national_identity
                        if (binding.rbMyself.isChecked) {
                            cashOutDTO.amount = etAmount.text.toString().trim().replace(",", "")
                            cashOutDTO.notes = etReason.text.toString().trim()
                        } else {
                            cashOutDTO.amount = etPamount.text.toString().trim().replace(",", "")
                            cashOutDTO.notes = etPreason.text.toString().trim()
                        }
                        lookupViewmodel.cashOutLoanCommit(cashOutDTO)
                        pinViewmodel.stopObserving()
                        lookupViewmodel.stopObserving()
                    } else {
                        toastyErrors("Check your internet connection and try again")
                    }

                }
            }
            lookupViewmodel.cashCommitStatusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            binding.apply {
                            }
                            binding.progressbar.mainPBar.makeGone()
                            val direction =
                                CashToCustomerFragmentDirections.actionCashToCustomerFragmentToLoanSuccessFragment(
                                    3
                                )
                            findNavController().navigate(direction)
                            lookupViewmodel.stopObserving()
                            // dashboardModel.setRefresh(true)
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

    private fun validateYesFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            val amount = etAmount.text.toString().trim()
            val reason = etReason.text.toString().trim()
            if (amount.isEmpty() || spDeposit.text.isEmpty()) {
                isValid = false
                toastyErrors("Fill out the mandatory fields")
            } else {
                isValid = true
                lookupViewmodel.amount.postValue(etAmount.text.toString().trim())
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
                amount.isEmpty() || loanAcc.isEmpty() -> {
                    isValid = false
                    toastyErrors("Fill out the mandatory fields")
                }
                else -> {
                    isValid = true
                    tlEnterAmount.error = ""
                    lookupViewmodel.amount.postValue(etPamount.text.toString().trim())
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
            depositNumber = selected.accountNumber
            val accNo = selected.accountNumber.replace("(?<=.{3}).(?=.{3})".toRegex(), "*")
            formatedAccount = "${selected.accountName} - $accNo"
            lookupViewmodel.repaymentPeriodMeasure.postValue("${selected.accountName} - $accNo")
            binding.spDeposit.setText(formatedAccount, false)
            binding.tvAmount.makeVisible()
            binding.tvAmountValue.text = "${selected.defaultCurrency} ${selected.availableBalance}"
            binding.etAmount.setText(selected.availableBalance)
            binding.etAmount.isFocusable = false
        }
    }

    private fun populatePartialWalletAcc(genderList: List<WalletAccount>) {
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
            binding.tvAmount.makeVisible()
            binding.tvAmountValue.text = "${selected.defaultCurrency} ${selected.availableBalance}"
            binding.etPamount.setText(selected.availableBalance)
        }
    }

}