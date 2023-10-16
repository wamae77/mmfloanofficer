package com.deefrent.rnd.fieldapp.view.homepage.billers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentBillPaymentSuccessBinding
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.makeGone

class BillPaymentSuccessFragment : Fragment() {
    private lateinit var binding: FragmentBillPaymentSuccessBinding
    private val billsViewModel: BillersViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBillPaymentSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_billPaymentSuccessFragment_to_billPaymentLookUpFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().navigate(R.id.action_billPaymentSuccessFragment_to_billPaymentLookUpFragment)
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_billPaymentSuccessFragment_to_billPaymentLookUpFragment)
            }
        }
        billsViewModel.postBillPaymentDTO.observe(viewLifecycleOwner) { billPaymentDTO ->
            binding.apply {
                tvBiller.text=billsViewModel.billerName
                if (billPaymentDTO.academicSemester.isNotEmpty()) {
                    tvAmountValue.text =
                        "${billsViewModel.paymentCurrency} ${FormatDigit.formatDigits(billPaymentDTO.amount)}"
                    tvFrequency.text = "STUDENT NUMBER:"
                    tvFrequencyValue.text = billPaymentDTO.accountNumber
                    tvPhone.text = "PHONE NUMBER:"
                    tvPhoneValue.text = billPaymentDTO.phoneNumber
                    tvAsset.text = "PAID FROM:"
                    tvAssetValue.text = billsViewModel.walletName
                    tvSupplier.text = "CUSTOMER NAME:"
                    tvSupplierValue.text = billsViewModel.customerName
                    tvAName.text = "ACADEMIC SEMESTER"
                    tvANameValue.text = billPaymentDTO.academicSemester
                    tvAssetCost.text = "STUDENT NAME"
                    billsViewModel.recipientName.observe(viewLifecycleOwner) {
                        tvAssetCostValue.text = it
                    }
                    tvRef.makeGone()
                    tvRefValue.makeGone()
                    tvCharge.makeGone()
                    tvChargeValue.makeGone()
                    tvDate.makeGone()
                    tvDateValue.makeGone()
                } else if (billPaymentDTO.recipientFirstName.isNotEmpty()) {
                    tvAmountValue.text =
                        "${billsViewModel.paymentCurrency} ${FormatDigit.formatDigits(billPaymentDTO.amount)}"
                    tvFrequency.text = "ACCOUNT NUMBER:"
                    tvFrequencyValue.text = billPaymentDTO.accountNumber
                    tvPhone.text = "PHONE NUMBER:"
                    tvPhoneValue.text = billPaymentDTO.phoneNumber
                    tvAsset.text = "PAID FROM:"
                    tvAssetValue.text = billsViewModel.walletName
                    tvSupplier.text = "CUSTOMER NAME:"
                    tvSupplierValue.text = billsViewModel.customerName
                    tvAName.text = "RECIPIENT FIRST NAME:"
                    tvANameValue.text = billPaymentDTO.recipientFirstName
                    tvAssetCost.text = "RECIPIENT LAST NAME:"
                    tvAssetCostValue.text = billPaymentDTO.recipientLastName
                    tvRef.text = "RECIPIENT ID NUMBER"
                    tvRefValue.text = billPaymentDTO.recipientIdNumber
                    tvChargeValue.makeGone()
                    tvDate.makeGone()
                    tvDateValue.makeGone()
                } else {
                    tvAmountValue.text =
                        "${billsViewModel.paymentCurrency} ${FormatDigit.formatDigits(billPaymentDTO.amount)}"
                    tvFrequency.text = "ACCOUNT NUMBER:"
                    tvFrequencyValue.text = billPaymentDTO.accountNumber
                    tvPhone.text = "PHONE NUMBER:"
                    tvPhoneValue.text = billPaymentDTO.phoneNumber
                    tvAsset.text = "PAID FROM:"
                    tvAssetValue.text = billsViewModel.walletName
                    tvSupplier.text = "CUSTOMER NAME:"
                    tvSupplierValue.text = billsViewModel.customerName
                    tvAName.text = "RECIPIENT NAME:"
                    billsViewModel.recipientName.observe(viewLifecycleOwner) {
                        tvANameValue.text = it
                    }
                    tvAssetCost.makeGone()
                    tvAssetCostValue.makeGone()
                    tvRef.makeGone()
                    tvRefValue.makeGone()
                    tvCharge.makeGone()
                    tvChargeValue.makeGone()
                    tvDate.makeGone()
                    tvDateValue.makeGone()
                }
            }
        }
        billsViewModel.postBillPaymentData.observe(viewLifecycleOwner) { postBillPaymentData ->
            binding.apply {
                tvPeriod.text = "TRANSACTION CODE:"
                tvPeriodValue.text = postBillPaymentData.transactionCode
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BillPaymentSuccessFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}