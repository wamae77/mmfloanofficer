package com.deefrent.rnd.fieldapp.ui.loanApplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCalculateLoanBinding
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import es.dmoral.toasty.Toasty
import java.io.File

class CalculateLoanFragment2 : Fragment() {
    private var _binding: FragmentCalculateLoanBinding? = null
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
        }

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
        progressDialog?.setOnDismissListener(DialogInterface.OnDismissListener {
            handler.removeCallbacks(
                runnable
            )
        })

        handler.postDelayed(runnable, 1000)
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
        binding.btnApply.setOnClickListener { v ->
            callDialog("Submitting...", requireContext(), v)
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

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalculateLoanFragment2().apply {
                arguments = Bundle().apply {
                }
            }
    }
}