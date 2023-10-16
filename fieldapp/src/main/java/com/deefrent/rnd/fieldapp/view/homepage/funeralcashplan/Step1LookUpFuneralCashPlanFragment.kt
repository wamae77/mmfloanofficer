package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AccountExistsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep1Binding
import com.deefrent.rnd.fieldapp.dtos.LoanLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.NameLookupDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.FindCustomerByIdNumberRequest
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.FindCustomerByNameRequest
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject

class Step1LookUpFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep1Binding>(
        FragmentFuneralCashPlanStep1Binding::inflate
    ) {
    private lateinit var cardBinding: AccountExistsDialogBinding

    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel
    private val sharedViewModel: SharedFuneralCashPlanViewModel by activityViewModels()
    private val gson = Gson()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.listFindCustomerByNameData.clear()
        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = getString(com.deefrent.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        /* binding.btnSearch.setOnClickListener {
             findNavController().navigate(R.id.action_step1IdLookUpFuneralCashPlanFragment_to_step2DetailsFuneralCashPlanFragment)
         }
        */
        initUI()


    }


    private fun initUI() {
        binding.apply {
            tvPhoneLookupTitle.text = getString(R.string.use_id_loan)
            tiPhoneNumber.hint = getString(R.string.id_number)
            tiPhoneNumber.placeholderText = "12345678W90"
            rgIDNumberName.makeVisible()
            etPhoneNumber.inputType = InputType.TYPE_CLASS_TEXT
            if (Constants.isSummaryBackArrow) {
                etPhoneNumber.setText(Constants.lookupId)
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
                            loanLookUpDTO.isCashPlan = 1
                            //viewmodel.loanLookUp(loanLookUpDTO)
                            performApiRequestFindCustomerByIdNumber(validId)
                        }
                    } else if ((selectedRadioButtonId == R.id.rbName)) {
                        etName.requestFocus()
                        val validName = binding.etName.text.toString()
                        if (validName.isEmpty()) {
                            etName.requestFocus()
                            tiName.error = "required"
                        } else {
                            tiName.error = ""
                            btnSearch.isEnabled = false
                            val loanLookUpDTO = NameLookupDTO(etName.text.toString(), 1)
                            //viewmodel.loanNameLookup(loanLookUpDTO)
                            performApiRequestFindCustomerByName(validName)
                        }
                    }
                }
            } else {
                onNoNetworkDialog(requireContext())
            }
        }
    }

    private fun performApiRequestFindCustomerByName(
        customerName: String
    ) {
        val findCustomerByNameRequest = FindCustomerByNameRequest(
            name = customerName.trim()
        )
        lifecycleScope.launch {
            viewModel.findCustomerByName(
                findCustomerByNameRequest
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialogWarn(it.error?.message.toString())
                        binding.btnSearch.isEnabled = true
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        binding.btnSearch.isEnabled = true
                        if (it.data?.status == 1) {
                            binding.progressbar.mainPBar.makeGone()
                            binding.etName.setText("")
                            Log.d("TAG", "initUI:")
                            val jsonString = gson.toJson(it.data?.data?.first())

                            it.data?.data?.let { list ->
                                sharedViewModel.listFindCustomerByNameData.addAll(
                                    list
                                )
                            }
                            findNavController().navigate(R.id.action_step1IdLookUpFuneralCashPlanFragment_to_step1CustomerListFuneralCashPlanFragment)
                        } else {
                            onInfoDialog("No customer in your branch linked with the name provided")
                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }

    private fun performApiRequestFindCustomerByIdNumber(
        idNumber: String
    ) {
        val findCustomerByIdNumberRequest = FindCustomerByIdNumberRequest(
            idNumber = idNumber
        )
        lifecycleScope.launch {
            viewModel.findCustomerByIdNumber(
                findCustomerByIdNumberRequest
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialogWarn(it.error?.message.toString())
                        binding.btnSearch.isEnabled = true
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            binding.etPhoneNumber.setText("")
                            val jsonString = gson.toJson(it.data?.data)
                            commonSharedPreferences.saveStringData(
                                CommonSharedPreferences.CUSTOMER_INFO,
                                jsonString
                            )

                            findNavController().navigate(R.id.action_step1IdLookUpFuneralCashPlanFragment_to_step2DetailsFuneralCashPlanFragment)
                            binding.btnSearch.isEnabled = true
                        } else {
                            binding.btnSearch.isEnabled = true
                            onInfoDialogWarn(getString(R.string.customer_not_assessed))
                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }
}