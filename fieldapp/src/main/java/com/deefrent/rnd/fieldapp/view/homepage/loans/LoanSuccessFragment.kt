package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanSuccessBinding
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.makeGone

class LoanSuccessFragment : Fragment() {
    private lateinit var binding: FragmentLoanSuccessBinding
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private val args: LoanSuccessFragmentArgs by navArgs()
    var totalAmount = ""
    var defCurrency = ""
    var charge = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoanSuccessBinding.inflate(layoutInflater)
        binding.apply {
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            btnContinue.setOnClickListener {
                requireActivity().onBackPressed()
            }
            lookupViewmodel.apply {
                /*codition.observe(viewLifecycleOwner) {
                    tvAssetValue.text = it
                    if (it == "NO") {
                        tvSupplierValue.makeGone()
                        tvSupplier.makeGone()
                        tvPhone.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        tvPhoneValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()

                    }
                }*/
                amount.observe(viewLifecycleOwner) {
                    tvAmountValue.text = FormatDigit.formatDigits(it)
                }
                repaymentPeriodMeasure.observe(viewLifecycleOwner) { tvFrequencyValue.text = it }
                repaymentPeriod.observe(viewLifecycleOwner) { tvPeriodValue.text = it }
                repaymentPeriodCycleMeasure.observe(viewLifecycleOwner) { tvAssetValue.text = it }
                repaymentPeriodCycle.observe(viewLifecycleOwner) { tvSupplierValue.text = it }
                loanOfficerAmount.observe(viewLifecycleOwner) { tvPhoneValue.text = it }
                tvAName.makeGone()
                tvANameValue.makeGone()
                tvAssetCost.makeGone()
                tvAssetCostValue.makeGone()
                /*supplierName.observe(viewLifecycleOwner) { tvSupplierValue.text = it }
                cost.observe(viewLifecycleOwner) {
                    tvAssetCostValue.text = FormatDigit.formatDigits(it)
                }
                supplierPhone.observe(viewLifecycleOwner) { tvPhoneValue.text = it }
                descri.observe(viewLifecycleOwner) { tvANameValue.text = it }*/
                refCode.observe(viewLifecycleOwner) { tvRefValue.text = it }
                charges.observe(viewLifecycleOwner) {
                    tvChargeValue.text = FormatDigit.formatDigits(it)
                }
            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            /* val time = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy  |  HH:mm a"))
             tvDateValue.setText(time)*/
            tvDateValue.setText(getCurrentDateTimeString())

            tvDate.makeGone()
            tvDateValue.makeGone()
            lookupViewmodel.apply {
                when (args.fragmentType) {
                    0 -> {
                        codition.observe(viewLifecycleOwner) {
                            tvAssetValue.text = it
                            if (it == "NO") {
                                tvSupplierValue.makeGone()
                                tvSupplier.makeGone()
                                tvPhone.makeGone()
                                tvAssetCost.makeGone()
                                tvAssetCostValue.makeGone()
                                tvPhoneValue.makeGone()
                                //tvAName.makeGone()
                                //tvANameValue.makeGone()
                            }
                        }
                        tvPhone.text = "LOAN OFFICER AMOUNT:"
                        loanOfficerAmount.observe(viewLifecycleOwner) { tvPhoneValue.text = it }
                        amount.observe(viewLifecycleOwner) {
                            tvAmount.text = "CUSTOMER AMOUNT:"
                            tvAmountValue.text = FormatDigit.formatDigits(it)
                        }
                        repaymentPeriodMeasure.observe(viewLifecycleOwner) { repaymentPeriodMeasure ->
                            repaymentPeriod.observe(viewLifecycleOwner) { repaymentPeriod ->
                                tvFrequencyValue.text = "$repaymentPeriod $repaymentPeriodMeasure"
                            }
                        }
                        repaymentPeriodCycleMeasure.observe(viewLifecycleOwner) { repaymentPeriodCycleMeasure ->
                            repaymentPeriodCycle.observe(viewLifecycleOwner) { repaymentPeriodCycle ->
                                tvPeriodValue.text =
                                    "$repaymentPeriodCycle $repaymentPeriodCycleMeasure"
                            }
                        }
                        tvAsset.makeGone()
                        tvAssetValue.makeGone()
                        tvSupplier.makeGone()
                        tvSupplierValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        applicationDate.observe(viewLifecycleOwner) { applicationDate ->
                            tvDate.text = "APPLICATION DATE:"
                            tvDateValue.text = "$applicationDate"
                        }
                        refCode.observe(viewLifecycleOwner) { tvRefValue.text = it }
                        charges.observe(viewLifecycleOwner) {
                            tvChargeValue.text = FormatDigit.formatDigits(it)
                        }
                    }

                    1 -> {
                        checkTitle.text = "Loan Disbursement Successful"
                        tv1.text =
                            "Your loan has been disbursed successfully to your Loan wallet account."
                        tvSupplierValue.makeGone()
                        tvSupplier.makeGone()
                        tvPhone.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        tvPhoneValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        amount.observe(viewLifecycleOwner) {
                            totalAmount = FormatDigit.formatDigits(it)
                        }
                        lookupViewmodel.previewData.observe(viewLifecycleOwner) { charge ->
                            tvAmountValue.text =
                                FormatDigit.formatDigits("${charge.currency} $totalAmount")
                            tvChargeValue.text =
                                FormatDigit.formatDigits("${charge.currency} ${charge.fee.toString()}")
                        }
                        tvAmount.text = "Amount:"
                        tvFrequency.text = "Deposit To:"
                        repaymentPeriodMeasure.observe(viewLifecycleOwner) {
                            tvFrequencyValue.text = it
                        }
                        tvPeriod.text = "Loan Account Number"
                        repaymentPeriod.observe(viewLifecycleOwner) { tvPeriodValue.text = it }
                        tvAssetValue.makeGone()
                        tvAsset.makeGone()
                    }

                    2 -> {
                        checkTitle.text = "Loan Repayment Successful"
                        tv1.text =
                            "Loan repayment has been processed successfully"
                        tvSupplierValue.makeGone()
                        tvSupplier.makeGone()
                        tvPhone.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        tvPhoneValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        currency.observe(viewLifecycleOwner) {
                            defCurrency = it
                        }
                        amount.observe(viewLifecycleOwner) {
                            totalAmount = FormatDigit.formatDigits(it)
                            tvAmountValue.text =
                                FormatDigit.formatDigits("$defCurrency $totalAmount")
                        }
                        lookupViewmodel.charges.observe(viewLifecycleOwner) { charge ->
                            tvChargeValue.text =
                                FormatDigit.formatDigits("$defCurrency ${charge.toString()}")
                        }
                        tvAmount.text = "Amount:"
                        tvPeriod.text = "Repayment Date:"
                        repaymentDate.observe(viewLifecycleOwner) { tvPeriodValue.text = it }
                        tvFrequency.text = "Loan Account Number"
                        repaymentPeriod.observe(viewLifecycleOwner) { tvFrequencyValue.text = it }
                        tvAssetValue.makeGone()
                        tvAsset.makeGone()
                        /*repaymentPeriodMeasure.observe(viewLifecycleOwner) {
                            tvFrequencyValue.text = it
                        }*/
                    }

                    3 -> {
                        checkTitle.text = "Loan Cash-Out Successful"
                        tv1.text =
                            "You have cash-out money from customer Loan account  successfully."
                        tvSupplierValue.makeGone()
                        tvSupplier.makeGone()
                        tvPhone.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        tvPhoneValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        amount.observe(viewLifecycleOwner) {
                            totalAmount = FormatDigit.formatDigits(it)
                        }
                        lookupViewmodel.previewData.observe(viewLifecycleOwner) { charge ->
                            tvAmountValue.text =
                                FormatDigit.formatDigits("${charge.currency} $totalAmount")
                            tvChargeValue.text =
                                FormatDigit.formatDigits("${charge.currency} ${charge.fee.toString()}")
                        }
                        tvAmount.text = "Amount:"
                        tvFrequency.text = "Cash-Out From:"
                        repaymentPeriodMeasure.observe(viewLifecycleOwner) {
                            tvFrequencyValue.text = it
                        }
                        tvPeriod.makeGone()
                        tvPeriodValue.makeGone()
                        tvAssetValue.makeGone()
                        tvAsset.makeGone()
                    }

                    4 -> {
                        checkTitle.text = "Update Loan Successful"
                        tvPhone.text = "LOAN OFFICER AMOUNT:"
                        loanOfficerAmount.observe(viewLifecycleOwner) { tvPhoneValue.text = it }
                        amount.observe(viewLifecycleOwner) {
                            tvAmount.text = "CUSTOMER AMOUNT:"
                            tvAmountValue.text = FormatDigit.formatDigits(it)
                        }
                        repaymentPeriodMeasure.observe(viewLifecycleOwner) { repaymentPeriodMeasure ->
                            repaymentPeriod.observe(viewLifecycleOwner) { repaymentPeriod ->
                                tvFrequencyValue.text = "$repaymentPeriod $repaymentPeriodMeasure"
                            }
                        }
                        repaymentPeriodCycleMeasure.observe(viewLifecycleOwner) { repaymentPeriodCycleMeasure ->
                            repaymentPeriodCycle.observe(viewLifecycleOwner) { repaymentPeriodCycle ->
                                tvPeriodValue.text =
                                    "$repaymentPeriodCycle $repaymentPeriodCycleMeasure"
                            }
                        }
                        tvAsset.makeGone()
                        tvAssetValue.makeGone()
                        tvSupplier.makeGone()
                        tvSupplierValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        applicationDate.observe(viewLifecycleOwner) { applicationDate ->
                            tvDate.text = "APPLICATION DATE:"
                            tvDateValue.text = "$applicationDate"
                        }
                        refCode.observe(viewLifecycleOwner) { tvRefValue.text = it }
                        charges.observe(viewLifecycleOwner) {
                            tvChargeValue.text = FormatDigit.formatDigits(it)
                        }
                        tvCharge.makeGone()
                        tvChargeValue.makeGone()
                        tvRef.makeGone()
                        tvRefValue.makeGone()
                        /*tvPhone.makeGone()
                        tvAssetCost.makeGone()
                        tvAssetCostValue.makeGone()
                        tvPhoneValue.makeGone()
                        tvAName.makeGone()
                        tvANameValue.makeGone()
                        tvChargeValue.makeGone()
                        tvCharge.makeGone()
                        charges.observe(viewLifecycleOwner) { charge = it }
                        descri.observe(viewLifecycleOwner) {
                            tv1.text = "You have updated ${it.capitalizeWords} successfully."
                        }
                        tvAmount.text = "Amount:"
                        amount.observe(viewLifecycleOwner) {
                            totalAmount = FormatDigit.formatDigits(it)
                            tvAmountValue.text = FormatDigit.formatDigits("${charge} $totalAmount")
                        }
                        tvFrequency.text = "Payment Every: "
                        repaymentPeriodMeasure.observe(viewLifecycleOwner) { repaymentCycleMeasure ->
                            repaymentPeriod.observe(viewLifecycleOwner) {
                                tvFrequencyValue.text = "$it $repaymentCycleMeasure"
                            }
                        }
                        tvAsset.text = "Repayment Period (Tenure):"
                        supplierName.observe(viewLifecycleOwner) { repaymentPeriodMeasure ->
                            codition.observe(viewLifecycleOwner) {
                                tvAssetValue.text = "$it $repaymentPeriodMeasure"
                            }
                        }
                        tvPeriod.makeGone()
                        tvPeriodValue.makeGone()
                        tvSupplier.makeGone()
                        tvAssetValue.makeGone()*/
                    }
                }
            }

        }
    }


}