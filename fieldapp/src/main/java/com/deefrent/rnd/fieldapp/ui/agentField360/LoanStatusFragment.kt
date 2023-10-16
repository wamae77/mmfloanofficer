package com.deefrent.rnd.fieldapp.ui.agentField360

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanStatusBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class LoanStatusFragment : Fragment() {
    private var _binding: FragmentLoanStatusBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AgentFieldSharedViewModel by activityViewModels()
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
        _binding = FragmentLoanStatusBinding.inflate(inflater, container, false)
        val view = binding.root
        initViews()
        observeSharedViewModel()
        return view
    }

    private fun initViews() {
        //populate loan type autocomplete
        val loanTypes = resources.getStringArray(R.array.loan_types)
        val loanTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, loanTypes)
        binding.acLoanType.setAdapter(loanTypesAdapter)
        binding.acLoanType.keyListener = null
    }

    private fun observeSharedViewModel() {
        var selectedUserType = ""
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    selectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    when (selectedUserType) {
                        "Individual" -> {
                            binding.tvRepayments.text = "REPAYMENTS - \n $accountNumber"
                            existingAccountViewModel.apply {
                                customerDetailsResponse.observe(viewLifecycleOwner,
                                    { customerDetailsResponse ->
                                        binding.tvAccountNumber.text =
                                            "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $accountNumber"
                                        binding.tvAgentNumber.text =
                                            "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}  ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${customerDetailsResponse.customerDetailsData.customerDetails.phoneNo}"
                                    })
                            }
                        }
                        "Agent" -> {
                            binding.tvRepayments.text = "REPAYMENTS - \n $accountNumber"
                            binding.tvAgentNumber.text = "Agent No: 3782"
                            existingAccountViewModel.apply {
                                merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                    { merchantAgentDetailsResponse ->
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                        binding.tvAccountNumber.text =
                                            "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                                    })
                            }
                        }
                        "Merchant" -> {
                            binding.tvAgentNumber.text = "Merchant No: 3782"
                            existingAccountViewModel.apply {
                                merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                    { merchantAgentDetailsResponse ->
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
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
            LoanStatusFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}