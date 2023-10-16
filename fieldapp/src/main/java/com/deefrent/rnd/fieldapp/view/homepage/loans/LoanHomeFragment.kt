package com.deefrent.rnd.fieldapp.view.homepage.loans

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.LoanHomeFragmentBinding
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible

class LoanHomeFragment : Fragment() {
    private lateinit var binding: LoanHomeFragmentBinding
    private val idViewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoanHomeFragmentBinding.inflate(layoutInflater)
        idViewmodel.stopObserving()
        Log.i("TAG", "onCreateView: LoanHomeFragmentBinding")
        idViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            val creditRating = it.creditRating
            binding.tvCreditRating.text =
                if (creditRating.isNotEmpty()) "Credit Rating: $creditRating" else "Credit Rating: Not Known"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
            binding.apply {
                HistoryLayout.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_loanHistoryFragment)
                }
                Disbuse.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_disburseLoanProductFragment)
                }
                repay.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_payableLoansFragment)
                }
                pendingDis.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_loanPendingApprovalFragment)
                }
                ClCashToCustomer.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_cashToCustomerFragment)
                }
                if (it.canApplyLoan) {
                    ClApply.makeVisible()
                    Apply.setOnClickListener {
                        findNavController().navigate(R.id.action_loanHomeFragment_to_loanProductFragment)
                    }
                } else {
                    ClApply.makeGone()
                }
                if (it.canRepay) {
                    clRepay.makeVisible()
                    cvRepaymentSchedule.makeVisible()
                    clLoanStatements.makeVisible()
                } else {
                    clRepay.makeGone()
                    cvRepaymentSchedule.makeGone()
                    clLoanStatements.makeGone()
                }
                clRepaymentSchedule.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_repaymentScheduleFragment)
                }
                clLoanStatements.setOnClickListener {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_loanStatementsFragment)
                }
                if (it.canDisburse) {
                    clDisburse.makeVisible()
                } else {
                    clDisburse.makeGone()
                }
                if (it.canCashOut) {
                    ClCashToCustomer.makeVisible()
                } else {
                    ClCashToCustomer.makeGone()
                }
                if (it.loansPendingApproval.isNotEmpty()) {
                    pendingDis.makeVisible()
                } else {
                    pendingDis.makeGone()
                }
            }


        }
        binding.ivBack.setOnClickListener { v ->
            findNavController().navigate(R.id.action_loanHomeFragment_to_loanLookupFragment)
            //Navigation.findNavController(v).navigateUp()
        }
        handleBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

        }
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_loanHomeFragment_to_loanLookupFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}