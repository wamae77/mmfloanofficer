package com.deefrent.rnd.fieldapp.ui.loanApplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanStatus2Binding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class LoanStatus2Fragment : Fragment() {
    private var _binding: FragmentLoanStatus2Binding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: LoanApplicationSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoanStatus2Binding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnApplyLoan.setOnClickListener {
            sharedViewModel.selectCalculateLoanTab(true)
        }
        observeSharedViewModel()
        return view
    }

    private fun observeSharedViewModel() {
        var selectedUserType: String = ""
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    selectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    when (selectedUserType) {
                        "Individual" -> {
                            existingAccountViewModel.apply {
                                customerDetailsResponse.observe(viewLifecycleOwner,
                                    { customerDetailsResponse ->
                                        binding.tvAccountNumber.text =
                                            "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $accountNumber"
                                    })
                            }}
                        else -> {
                            existingAccountViewModel.apply {
                                merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                    { merchantAgentDetailsResponse ->
                                        binding.tvAccountNumber.text =
                                            "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                                    })
                            }
                        }
                    }

                })
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoanStatus2Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}