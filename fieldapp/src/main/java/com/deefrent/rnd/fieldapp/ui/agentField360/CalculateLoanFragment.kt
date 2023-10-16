package com.deefrent.rnd.fieldapp.ui.agentField360

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCalculateLoanBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import java.io.File

class CalculateLoanFragment : Fragment() {
    private var _binding: FragmentCalculateLoanBinding? = null
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
        _binding = FragmentCalculateLoanBinding.inflate(inflater, container, false)
        val view = binding.root
        initViews()
        initButtonClickListeners()
        observeSharedViewModel()
        return view
    }

    private fun initViews() {
        //populate repayment period autocomplete
        val repaymentPeriods = resources.getStringArray(R.array.repayment_period)
        val repaymentPeriodsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, repaymentPeriods)
        binding.acPeriod.setAdapter(repaymentPeriodsAdapter)
        binding.acPeriod.keyListener = null
        //populate loan type autocomplete
        val loanTypes = resources.getStringArray(R.array.loan_types)
        val loanTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, loanTypes)
        binding.acLoanType.setAdapter(loanTypesAdapter)
        binding.acLoanType.keyListener = null
        //disable apply loan button
        binding.btnApply.isEnabled = false
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
                            existingAccountViewModel.apply {
                                customerDetailsResponse.observe(viewLifecycleOwner,
                                    { customerDetailsResponse ->
                                        binding.tvAccountNumber.text =
                                            "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $accountNumber"
                                    })
                            }
                        }
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
            /*customerID.observe(viewLifecycleOwner,
                { customerID ->
                    binding.tvCustomerID.text = "ID: $customerID"
                })*/
        }

    }

    private fun selectDocument() {
        val intent = Intent()
            .setType("application/pdf")
            .setAction(Intent.ACTION_OPEN_DOCUMENT)
        startActivityForResult(Intent.createChooser(intent, "Select document to attach"), 111)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            val uriString = uri.toString() //The uri with the location of the file
            val returnedFile = File(uriString)
            val absolutePath = returnedFile.absolutePath
            var displayName = ""
            if (uriString.startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = uri?.let {
                        requireActivity().contentResolver.query(
                            it,
                            null,
                            null,
                            null,
                            null
                        )
                    }
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (uriString.startsWith("file://")) {
                displayName = returnedFile.name
            }
            binding.tvAttachDocument.text = displayName
        }
    }

    private fun initButtonClickListeners() {
        binding.tvAttachDocument.setOnClickListener {
            selectDocument()
        }
        binding.btnCalculate.setOnClickListener {
            binding.btnApply.visibility = View.VISIBLE
            binding.btnReset.visibility = View.VISIBLE
            binding.llMoreDetails.visibility = View.VISIBLE
            binding.btnCalculate.visibility = View.GONE
            binding.tvRepaymentPeriod.text = "Repayment Period \n${binding.acPeriod.text}"
        }
        binding.btnReset.setOnClickListener {
            binding.btnApply.visibility = View.GONE
            binding.btnReset.visibility = View.GONE
            binding.llMoreDetails.visibility = View.GONE
            binding.btnCalculate.visibility = View.VISIBLE
        }

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalculateLoanFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}