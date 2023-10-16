package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerLoanStatusBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel

class CustomerLoanStatusFragment : Fragment() {
    private var _binding: FragmentCustomerLoanStatusBinding? = null
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
        _binding = FragmentCustomerLoanStatusBinding.inflate(inflater, container, false)
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
                    "Individual" -> {
                        binding.tvRepayments.text = "REPAYMENTS - \n $accountNumber"
                        existingAccountViewModel.apply {
                            customerDetailsResponse.observe(viewLifecycleOwner
                            ) { customerDetailsResponse ->
                                binding.tvAccountNumber.text =
                                    "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $accountNumber"
                                binding.tvAgentNumber.text =
                                    "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}  ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                                binding.tvAgentTelephone.text =
                                    "Tel: ${customerDetailsResponse.customerDetailsData.customerDetails.phoneNo}"
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
            CustomerLoanStatusFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}