package com.deefrent.rnd.fieldapp.ui.customerComplains

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.bodies.existingAccounts.SearchExistingAccountBody
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerComplainsBinding
import com.deefrent.rnd.fieldapp.models.userTypes.UserType
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import es.dmoral.toasty.Toasty

class CustomerComplainsFragment : Fragment() {
    private var _binding: FragmentCustomerComplainsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CustomerComplainsSharedViewModel by activityViewModels()
    private lateinit var dataViewModel: DataViewModel
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
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentCustomerComplainsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSearch.setOnClickListener { v ->
            if (isValid()) {
                updateSharedViewModel()
                when (binding.acUserTypes.text.toString()) {
                    "Individual" -> searchCustomerAccount(v) //callDialog("Searching...", requireContext(), v)
                    else -> searchMerchantAgent(v)
                }
            }
        }
        getUserTypes()
        return view
    }

    private fun searchCustomerAccount(v: View) {
        Constants.callDialog2("Searching...", requireContext())
        val searchExistingAccountBody =
            SearchExistingAccountBody(Integer.parseInt(binding.etAccountNumber.text.toString()))
        existingAccountViewModel.getCustomerDetails(searchExistingAccountBody)
            .observe(viewLifecycleOwner) { getCustomerDetailsResponse ->
                when (getCustomerDetailsResponse.status) {
                    "success" -> {
                        existingAccountViewModel.apply {
                            setExistingCustomerDetailsResponse(getCustomerDetailsResponse)
                            setAccountNumber(binding.etAccountNumber.text.toString())
                            setUserType(binding.acUserTypes.text.toString())
                        }
                        val bundle = Bundle()
                        bundle.putBoolean("isFromAgent360", false)

                        Constants.cancelDialog()
                    }
                    "failed" -> {
                        Constants.cancelDialog()
                        Toasty.error(
                            requireContext(),
                            getCustomerDetailsResponse.message,
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

    private fun searchMerchantAgent(v: View) {
        Constants.callDialog2("Searching...", requireContext())
        existingAccountViewModel.getMerchantAgentDetails(binding.etAccountNumber.text.toString())
            .observe(viewLifecycleOwner) { getMerchantAgentDetailsResponse ->
                when (getMerchantAgentDetailsResponse.status) {
                    "success" -> {
                        existingAccountViewModel.apply {
                            setExistingMerchantAgentDetailsResponse(getMerchantAgentDetailsResponse)
                            setAccountNumber(binding.etAccountNumber.text.toString())
                            setUserType(binding.acUserTypes.text.toString())
                        }
                        val bundle = Bundle()
                        bundle.putBoolean("isFromAgent360", false)

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

    private fun getUserTypes() {
        dataViewModel.fetchUserTypes().observe(viewLifecycleOwner) { fetchUserTypesResponse ->
            if (fetchUserTypesResponse != null) {
                val userTypes: List<UserType> = fetchUserTypesResponse.userTypeData.userType
                populateUserTypes(userTypes)
            } else {
                Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun populateUserTypes(userTypeList: List<UserType>) {
        //populate user type autocomplete
        val userTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userTypeList)
        binding.acUserTypes.setAdapter(userTypesAdapter)
        binding.acUserTypes.keyListener = null
        binding.acUserTypes.setOnItemClickListener { parent, _, position, _ ->
            val selected: UserType = parent.adapter.getItem(position) as UserType
            sharedViewModel.setUserAccountTypeId(selected.id)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerComplainsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}