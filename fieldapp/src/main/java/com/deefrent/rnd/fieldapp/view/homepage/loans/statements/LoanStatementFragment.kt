package com.deefrent.rnd.fieldapp.view.homepage.loans.statements

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
import com.deefrent.rnd.fieldapp.adapters.LoanMiniStatementListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanStatementBinding
import com.deefrent.rnd.fieldapp.dtos.RepaymentScheduleDTO
import com.deefrent.rnd.fieldapp.models.loans.Transaction
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import java.util.ArrayList

class LoanStatementFragment : Fragment() {
    private lateinit var binding: FragmentLoanStatementBinding
    private val lookupViewmodel: LoanLookUpViewModel by activityViewModels()
    private var national_id = ""
    private var loanAccNo = ""
    private var loanBal = ""
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
        binding = FragmentLoanStatementBinding.inflate(layoutInflater)
        val args = LoanStatementFragmentArgs.fromBundle(requireArguments()).repayableLoan
        binding.tvRecent.text = "${args.name.capitalizeWords} Mini Statement"
        loanID = args.loanId.toString()
        initUI()
        //handleBackButton()
        getLoanMiniStatement()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
    }

    private fun initUI() {
        binding.rvMiniStatement.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.btnRefresh.setOnClickListener {
            getLoanMiniStatement()
        }
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
            if (it != null) {
                when (it) {
                    1 -> {
                        displayList.clear()
                        displayList = lookupViewmodel.loanMiniStatementList
                        miniStatementListAdapter = LoanMiniStatementListAdapter(displayList)
                        binding.rvMiniStatement.adapter = miniStatementListAdapter
                        Log.d("TAG", "onCreateView: list ${displayList.size}")
                        if(displayList.isNotEmpty()){
                            binding.apply {
                                rvMiniStatement.makeVisible()
                                llNoData.makeGone()
                            }
                        }
                        else{
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

    private fun getLoanMiniStatement() {
        if (isNetworkAvailable(requireContext())) {
            binding.apply {
                progressbar.tvWait.text="Fetching loan statement..."
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
            LoanStatementFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}