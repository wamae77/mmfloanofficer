package com.deefrent.rnd.fieldapp.ui.agentField360

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerExperienceBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import es.dmoral.toasty.Toasty

class CustomerExperienceFragment : Fragment() {
    private var _binding: FragmentCustomerExperienceBinding? = null
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
        _binding = FragmentCustomerExperienceBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.tvSubmitSurvey.setOnClickListener { v ->
            if (isValid()) {
                callDialog("Submitting...", requireContext(), v)
            }
        }
        observeSharedViewModel()
        return view
    }

    private var progressDialog: ProgressDialog? = null
    private fun callDialog(message: String?, context: Context?, v: View) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
        val handler = Handler()
        val runnable = Runnable {
            progressDialog?.dismiss()
            Toasty.success(requireContext(), "Submitted successfully", Toasty.LENGTH_LONG).show()
        }
        progressDialog?.setOnDismissListener {
            handler.removeCallbacks(
                runnable
            )
        }

        handler.postDelayed(runnable, 1000)
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.et1.text.toString().isNullOrEmpty() ||
            binding.et2.text.toString().isNullOrEmpty() ||
            binding.rb1.checkedRadioButtonId == -1 ||
            binding.rb2.checkedRadioButtonId == -1 ||
            binding.rb3.checkedRadioButtonId == -1
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please answer all the questions", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun observeSharedViewModel() {
        var selectedUserType = ""
        sharedViewModel.apply {
            userType.observe(
                viewLifecycleOwner
            ) { userType ->
                selectedUserType = userType
            }
            accountNumber.observe(
                viewLifecycleOwner
            ) { accountNumber ->
                when (selectedUserType) {
                    "Individual" -> {
                        existingAccountViewModel.apply {
                            customerDetailsResponse.observe(
                                viewLifecycleOwner
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
                    "Agent" -> {
                        binding.tvAgentNumber.text = "Agent No: 3782"
                        existingAccountViewModel.apply {
                            merchantAgentDetailsResponse.observe(
                                viewLifecycleOwner
                            ) { merchantAgentDetailsResponse ->
                                binding.tvAgentTelephone.text =
                                    "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                binding.tvAccountNumber.text =
                                    "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                            }
                        }
                    }
                    "Merchant" -> {
                        binding.tvAgentNumber.text = "Merchant No: 3782"
                        existingAccountViewModel.apply {
                            merchantAgentDetailsResponse.observe(
                                viewLifecycleOwner
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
            CustomerExperienceFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}