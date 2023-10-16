package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.LoanMiniStatementListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanHistoryDetailsBinding
import com.deefrent.rnd.fieldapp.dtos.RepaymentScheduleDTO
import com.deefrent.rnd.fieldapp.models.loans.Transaction
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus

class LoanHistoryDetailsFragment : Fragment() {
    private lateinit var binding: FragmentLoanHistoryDetailsBinding
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private var loanID = ""
    private lateinit var miniStatementListAdapter: LoanMiniStatementListAdapter
    private var displayList: ArrayList<Transaction> = arrayListOf()
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
        binding = FragmentLoanHistoryDetailsBinding.inflate(layoutInflater)
        binding.tvRecent.text =
            "${lookupViewmodel.loanHistoryItem.name.capitalizeWords} (${lookupViewmodel.loanHistoryItem.loanNumber}) Mini Statement"
        loanID = lookupViewmodel.loanHistoryItem.loanId.toString()
        initUI()
        //handleBackButton()
        getLoanMiniStatement()
        return binding.root
    }

    private fun initUI() {
        binding.rvMiniStatement.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.btnRefresh.setOnClickListener {
            getLoanMiniStatement()
        }
        lookupViewmodel.loanHistoryItem.let {
            binding.tvFrequencyValue.text =
                "${it.currency} ${FormatDigit.formatDigits(it.amountApplied)}"
            binding.tvPeriodValue.text =
                "${it.currency} ${FormatDigit.formatDigits(it.amountDisbursed)}"
            binding.tvLoanBal.text = "Date Applied: "
            binding.tvLoanBalValue.text = it.applicationDate
            binding.tvLoanAccount.text = "Date Disbursed: "
            binding.tvLoanccountValue.text = it.disbursementDate
        }
        lookupViewmodel.loanLookUpData.observe(viewLifecycleOwner) {
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
        }
        lookupViewmodel.responseGStatus.observe(viewLifecycleOwner)
        {
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
        lookupViewmodel.statusId.observe(viewLifecycleOwner)
        {
            if (it != null) {
                when (it) {
                    1 -> {
                        displayList.clear()
                        displayList = lookupViewmodel.loanMiniStatementList
                        miniStatementListAdapter = LoanMiniStatementListAdapter(displayList)
                        binding.rvMiniStatement.adapter = miniStatementListAdapter
                        Log.d("TAG", "onCreateView: list ${displayList.size}")
                        if (displayList.isNotEmpty()) {
                            binding.apply {
                                rvMiniStatement.makeVisible()
                                llNoData.makeGone()
                            }
                        } else {
                            binding.apply {
                                rvMiniStatement.makeGone()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
    }

    private fun getLoanMiniStatement() {
        if (isNetworkAvailable(requireContext())) {
            binding.apply {
                progressbar.tvWait.text = "Fetching loan mini-statement..."
                rvMiniStatement.makeGone()
                llNoData.makeGone()
            }
            val repaymentScheduleDTO = RepaymentScheduleDTO(loanID)
            lookupViewmodel.getLoanMiniStatement(repaymentScheduleDTO)
        } else {
            onNoNetworkDialog(requireContext())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoanHistoryDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}