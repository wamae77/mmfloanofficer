package com.deefrent.rnd.fieldapp.view.homepage.loans.repaymentSchedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.RepaymentScheduleListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentRepaymentScheduleBinding
import com.deefrent.rnd.fieldapp.dtos.RepaymentScheduleDTO
import com.deefrent.rnd.fieldapp.models.loans.RepaymentSchedule
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import java.util.ArrayList

class RepaymentScheduleFragment : Fragment() {
    private lateinit var binding: FragmentRepaymentScheduleBinding
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private var national_id = ""
    private var loanAccNo = ""
    private var loanBal = ""
    private var loanID = ""
    private lateinit var repaymentScheduleListAdapter: RepaymentScheduleListAdapter
    private var displayList: ArrayList<RepaymentSchedule> = arrayListOf()
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
        binding = FragmentRepaymentScheduleBinding.inflate(layoutInflater)
        val args = RepaymentScheduleFragmentArgs.fromBundle(requireArguments()).repayableLoan
        lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            national_id = it?.idNumber.toString()
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
        }
        binding.head.text = "Repayment Schedule"
        loanID = args.loanId.toString()
        Log.d("TAG", "onCreateView: ${loanID}")
        binding.apply {
            val applied = FormatDigit.formatDigits(args.amountApplied)
            val approved = FormatDigit.formatDigits(args.amountApproved)
            val bal = FormatDigit.formatDigits(args.balance)
            val loanAccountNo = args.loanAccountNo.uppercase()
            val loanProductName = args.name
            tvFrequencyValue.text = "${args.currency} $applied"
            tvPeriodValue.text = "${args.currency} $approved"
            tvLoanBalValue.text = loanProductName
            tvLoanccountValue.text = loanAccountNo
            loanAccNo = args.loanAccountNo
            loanBal = args.balance
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
        }
        initUI()
        handleBackButton()
        getRepaymentSchedule()
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun getRepaymentSchedule() {
        if (isNetworkAvailable(requireContext())) {
            binding.apply {
                progressbar.tvWait.text = "Fetching repayment schedule..."
                rvRepaymentSchedule.makeGone()
                llNoData.makeGone()
            }
            val repaymentScheduleDTO = RepaymentScheduleDTO(loanID)
            lookupViewmodel.getRepaymentSchedule(repaymentScheduleDTO)
        } else {
            onNoNetworkDialog(requireContext())
        }
    }

    private fun initUI() {
        binding.rvRepaymentSchedule.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        lookupViewmodel.responseGStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                    else -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        lookupViewmodel.statusId.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        displayList.clear()
                        displayList = lookupViewmodel.repaymentScheduleList
                        repaymentScheduleListAdapter = RepaymentScheduleListAdapter(displayList)
                        binding.rvRepaymentSchedule.adapter = repaymentScheduleListAdapter
                        Log.d("TAG", "onCreateView: list ${displayList}")
                        if (displayList.isNotEmpty()) {
                            binding.apply {
                                rvRepaymentSchedule.makeVisible()
                                llNoData.makeGone()
                            }
                        } else {
                            binding.apply {
                                rvRepaymentSchedule.makeGone()
                                llNoData.makeVisible()
                            }
                        }
                        lookupViewmodel.stopObserving()
                    }
                    0 -> {
                        onInfoDialog(lookupViewmodel.statusMessage.value)
                        lookupViewmodel.stopObserving()
                    }
                    else -> {
                        lookupViewmodel.stopObserving()
                        onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RepaymentScheduleFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}