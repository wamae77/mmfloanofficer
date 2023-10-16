package com.deefrent.rnd.fieldapp.ui.onboardMerchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardMerchantBinding

class OnboardMerchantFragment : Fragment() {
    private var _binding: FragmentOnboardMerchantBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardMerchantBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
        }
        initRadioButtons()
        initButtonClickListeners()
        initViews()
        return view
    }

    private fun initViews(){
        //populate account types dropdown
        val accountTypes = resources.getStringArray(R.array.user_types)
        val accountTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, accountTypes)
        binding.acAccountType.setAdapter(accountTypesAdapter)
        binding.acAccountType.keyListener = null
        //populate customer type autocomplete
        val agentTypes = resources.getStringArray(R.array.customer_types)
        val agentTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, agentTypes)
        binding.acMerchantType.setAdapter(agentTypesAdapter)
        binding.acMerchantType.keyListener = null
        //populate reference type autocomplete
        val referenceTypes = resources.getStringArray(R.array.reference_types_agent)
        val referenceTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, referenceTypes)
        binding.acReferenceType.setAdapter(referenceTypesAdapter)
        binding.acReferenceType.keyListener = null
        //toggle visibility of reference type
        binding.acMerchantType.setOnItemClickListener { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            when (agentTypesAdapter.getItem(position)) {
                "REFERENCED" -> binding.tiReferenceType.visibility = View.VISIBLE
                else -> binding.tiReferenceType.visibility = View.GONE
            }
        }
    }

    private fun initButtonClickListeners() {
        binding.btnContinue.setOnClickListener{v->
            when(binding.btnContinue.text){
                requireContext().getString(R.string.search)->{
                }
                requireContext().getString(R.string.continue_)->{
                }
            }
        }
    }

    private fun resetNewAgentsViews() {
        binding.acAccountType.text = null
        binding.acMerchantType.text = null
        binding.acReferenceType.text = null
    }

    private fun resetExistingAgentsViews() {
        binding.acAccountType.text = null
        binding.etAccountNumber.text = null
    }

    private fun initRadioButtons() {
        binding.rgMerchants.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (radio.text) {
                requireContext().getString(R.string.new_merchant_agents) -> {
                    binding.btnContinue.text = requireContext().getString(R.string.continue_)
                    binding.tiReferenceType.visibility = View.GONE
                    binding.tiMerchantType.visibility=View.VISIBLE
                    binding.tvMerchantTypeTitle.visibility=View.VISIBLE
                    binding.tiAccountNumber.visibility=View.GONE
                    resetExistingAgentsViews()
                }
                requireContext().getString(R.string.existing_merchant_agents) -> {
                    binding.btnContinue.text = requireContext().getString(R.string.search)
                    binding.tiReferenceType.visibility = View.GONE
                    binding.tiMerchantType.visibility=View.GONE
                    binding.tvMerchantTypeTitle.visibility=View.GONE
                    binding.tiAccountNumber.visibility=View.VISIBLE
                    resetNewAgentsViews()
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
            OnboardMerchantFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}