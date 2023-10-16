package com.deefrent.rnd.fieldapp.ui.agentField360

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentQuestionnaireSearchResultsBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import kotlin.properties.Delegates

class QuestionnaireSearchResultsFragment : Fragment() {
    private var _binding: FragmentQuestionnaireSearchResultsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AgentFieldSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFromAgent360=it.getBoolean("isFromAgent360")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestionnaireSearchResultsBinding.inflate(inflater, container, false)
        val view = binding.root
        observeSharedViewModel()
        return view
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
                            binding.tvSurveyTitle.text="Individual Questionnaire Survey"
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
                            binding.tvSurveyTitle.text="Agent Questionnaire Survey"
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
                            binding.tvSurveyTitle.text="Merchant Questionnaire Survey"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private var isFromAgent360 by Delegates.notNull<Boolean>()
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuestionnaireSearchResultsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}