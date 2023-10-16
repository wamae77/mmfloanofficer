package com.deefrent.rnd.fieldapp.ui.assetManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentAddAssetBinding
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import es.dmoral.toasty.Toasty

class AddAssetFragment : Fragment() {
    private var _binding: FragmentAddAssetBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AssetManagementSharedViewModel by activityViewModels()
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
        _binding = FragmentAddAssetBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnSearch.setOnClickListener { v ->
            if (isValid()) {
                updateSharedViewModel()
                searchMerchantAgent(v)
                //callDialog("Searching...", requireContext(), v)
            }
        }
        initDropDowns()
        return view
    }

    private fun searchMerchantAgent(v: View) {
        Constants.callDialog2("Searching...", requireContext())
        existingAccountViewModel.getMerchantAgentDetails(binding.etAccountNumber.text.toString())
            .observe(viewLifecycleOwner) { getMerchantAgentDetailsResponse ->
                when (getMerchantAgentDetailsResponse.status) {
                    "success" -> {
                        /*val customerGeomapDetails =
                            getCustomerGeomapDetailsResponse.customerGeomapData.customerGeomapDetails
                        val bundle = bundleOf("customerGeomapDetails" to customerGeomapDetails)*/
                        existingAccountViewModel.apply {
                            setExistingMerchantAgentDetailsResponse(getMerchantAgentDetailsResponse)
                            setAccountNumber(binding.etAccountNumber.text.toString())
                            setUserType(binding.acUserTypes.text.toString())
                        }

                        Constants.cancelDialog()
                    }
                    "failed" -> {
                        Constants.cancelDialog()
                        Toasty.error(
                            requireContext(),
                            getMerchantAgentDetailsResponse.message,
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Constants.cancelDialog()
                        Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG)
                            .show()
                    }
                }
            }

    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.acUserTypes.text.toString().isNullOrEmpty() ||
            binding.etAccountNumber.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun updateSharedViewModel() {
        sharedViewModel.apply {
            setAccountNumber(binding.etAccountNumber.text.toString())
            setUserType(binding.acUserTypes.text.toString())
            setUserAccountTypeId(1)
        }
    }

    private fun initDropDowns() {
        //populate user type autocomplete
        val userTypes = resources.getStringArray(R.array.merchant_types)
        val userTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userTypes)
        binding.acUserTypes.setAdapter(userTypesAdapter)
        binding.acUserTypes.keyListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAssetFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}