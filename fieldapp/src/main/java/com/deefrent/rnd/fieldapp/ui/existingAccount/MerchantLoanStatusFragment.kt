package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentMerchantLoanStatus2Binding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class MerchantLoanStatusFragment : Fragment() {
    private var _binding: FragmentMerchantLoanStatus2Binding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ExistingAccountSharedViewModel by activityViewModels()
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
        _binding = FragmentMerchantLoanStatus2Binding.inflate(inflater, container, false)
        val view = binding.root

        observeSharedViewModel()
        return view
    }

    private fun observeSharedViewModel() {
        var selectedUserType = ""
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner
            ) { userType ->
                selectedUserType = userType
            }
            accountNumber.observe(viewLifecycleOwner
            ) { accountNumber ->
                when (selectedUserType) {
                    "Agent" -> {
                        binding.tvFragmentTitle.text="Agent Loan Status"
                        binding.tvRepayments.text = "REPAYMENTS - \n $accountNumber"
                        binding.tvAgentNumber.text = "Agent No: 3782"
                        existingAccountViewModel.apply {
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner
                            ) { merchantAgentDetailsResponse ->
                                binding.tvAgentTelephone.text =
                                    "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                binding.tvAccountNumber.text =
                                    "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                            }
                        }
                    }
                    "Merchant" -> {
                        binding.tvFragmentTitle.text="Merchant Loan Status"
                        binding.tvRepayments.text = "REPAYMENTS - \n $accountNumber"
                        binding.tvAgentNumber.text = "Merchant No: 3782"
                        existingAccountViewModel.apply {
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner
                            ) { merchantAgentDetailsResponse ->
                                binding.tvAgentTelephone.text =
                                    "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                binding.tvAccountNumber.text =
                                    "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                            }
                        }
                    }
                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MerchantLoanStatusFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}