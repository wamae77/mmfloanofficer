package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AccountExistsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerLookupBinding
import com.deefrent.rnd.fieldapp.dtos.LoanLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.NameLookupDTO
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.isSummaryBackArrow
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.lookupId
import com.google.gson.Gson

class LoanLookupFragment : Fragment() {
    private lateinit var binding: FragmentCustomerLookupBinding
    private lateinit var cardBinding: AccountExistsDialogBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerLookupBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        initUI()
        return binding.root
    }

    private fun initUI() {
        binding.apply {
            header.text = getString(R.string.customer_loans)
            tvPhoneLookupTitle.text = getString(R.string.use_id_loan)
            tiPhoneNumber.hint = getString(R.string.id_number)
            tiPhoneNumber.placeholderText = "12345678W90"
            rgIDNumberName.makeVisible()
            etPhoneNumber.inputType = InputType.TYPE_CLASS_TEXT
            if (isSummaryBackArrow) {
                etPhoneNumber.setText(lookupId)
            } else {
                etPhoneNumber.setText("")
            }
        }
        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tiPhoneNumber.error = null
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tiPhoneNumber.error = null
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tiPhoneNumber.error = null
            }
        })
        //radio buttons listener
        binding.rgIDNumberName.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (radio.text) {
                resources.getString(R.string.id_number) -> {
                    binding.tiPhoneNumber.makeVisible()
                    binding.tiName.makeGone()
                }

                resources.getString(R.string.name) -> {
                    binding.tiPhoneNumber.makeGone()
                    binding.tiName.makeVisible()
                }
            }
        }
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }

                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }

                    else -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        binding.btnSearch.setOnClickListener { v ->
            if (isNetworkAvailable(requireContext())) {
                binding.apply {
                    val selectedRadioButtonId: Int = binding.rgIDNumberName.checkedRadioButtonId
                    if (selectedRadioButtonId == R.id.rbIDNumber) {
                        etPhoneNumber.requestFocus()
                        val validId = binding.etPhoneNumber.text.toString()
                        if (validId.isEmpty()) {
                            etPhoneNumber.requestFocus()
                            tiPhoneNumber.error = "required"
                        } else {
                            tiPhoneNumber.error = ""
                            btnSearch.isEnabled = false
                            val loanLookUpDTO = LoanLookUpDTO()
                            loanLookUpDTO.idNumber = validId
                            loanLookUpDTO.isLoan = 1
                            viewmodel.loanLookUp(loanLookUpDTO)
                        }
                    } else if ((selectedRadioButtonId == R.id.rbName)) {
                        etName.requestFocus()
                        val validId = binding.etName.text.toString()
                        if (validId.isEmpty()) {
                            etName.requestFocus()
                            tiName.error = "required"
                        } else {
                            tiName.error = ""
                            btnSearch.isEnabled = false
                            val loanLookUpDTO = NameLookupDTO(etName.text.toString(), 1)
                            viewmodel.loanNameLookup(loanLookUpDTO)
                        }
                    }
                }
            } else {
                onNoNetworkDialog(requireContext())
            }
        }
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                findNavController().navigate(R.id.action_loanLookupFragment_to_dashboardFragment)
            }

            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            val selectedRadioButtonId: Int =
                                binding.rgIDNumberName.checkedRadioButtonId
                            if (selectedRadioButtonId == R.id.rbIDNumber) {
                                val loanLookupData = viewmodel._loanLookUpData.value

//                                val gson = Gson()
//                                val jsonString = gson.toJson(loanLookupData)
//                                CommonSharedPreferences(requireContext()).saveStringData(
//                                    key = CommonSharedPreferences.LOANLOOKUPDATA,
//                                    value = jsonString
//                                )
//                                if (loanLookupData?.fingerPrintRegId == null) {
//                                    //findNavController().navigate(R.id.enrollOneFingerPrintFragment)
//                                    findNavController().navigate(R.id.updateCustomerFingerPrintFragment)
//                                } else {
                                    if (loanLookupData?.isAssessed == true) {
                                        binding.etPhoneNumber.setText("")
                                        findNavController().navigate(R.id.action_loanLookupFragment_to_loanHomeFragment)
                                        viewmodel.stopObserving()
                                        binding.btnSearch.isEnabled = true
                                    } else {
                                        onInfoDialogWarn(getString(R.string.customer_not_assessed))
                                    }
//                                }
                            } else if ((selectedRadioButtonId == R.id.rbName)) {
                                if (viewmodel.customerList.isNotEmpty()) {
                                    viewmodel.stopObserving()
                                    binding.btnSearch.isEnabled = true
                                    binding.etName.setText("")
                                    Log.d("TAG", "initUI: ${viewmodel.customerList.size}")
                                    findNavController().navigate(R.id.action_loanLookupFragment_to_customerLoanListFragment)
                                } else {
                                    viewmodel.stopObserving()
                                    binding.btnSearch.isEnabled = true
                                    onInfoDialog("No customer in your branch linked with the name provided")
                                }
                            }
                        }

                        0 -> {
                            viewmodel.stopObserving()
                            binding.btnSearch.isEnabled = true
                            onInfoDialog(viewmodel.statusMessage.value)
                        }

                        else -> {
                            viewmodel.stopObserving()
                            binding.btnSearch.isEnabled = true
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
        }
        handleBackButton()

    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_loanLookupFragment_to_dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun showAccountRegisteredDialog() {
        val dialog = Dialog(requireContext())
        cardBinding =
            AccountExistsDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialog.dismiss()
            // viewModel.stopObserving()
        }
        cardBinding.btnNotNow.setOnClickListener {
            dialog.dismiss()
            //  viewModel.stopObserving()
        }
        cardBinding.btnApplyLoan.setOnClickListener {
            dialog.dismiss()
            //  viewModel.stopObserving()
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }


}