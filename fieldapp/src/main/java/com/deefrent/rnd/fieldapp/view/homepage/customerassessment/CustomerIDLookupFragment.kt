package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AccountExistsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerLookupBinding
import com.deefrent.rnd.fieldapp.dtos.CustomerIDLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.NameLookupDTO
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus

class CustomerIDLookupFragment : Fragment() {
    private lateinit var binding: FragmentCustomerLookupBinding
    private lateinit var cardBinding: AccountExistsDialogBinding

    //  private val args:CustomerIDLookupFragmentArgs by navArgs()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity())[CustomerAssessmentHomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerLookupBinding.inflate(layoutInflater)
        initUI()
        handleBackButton()
        return binding.root
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                    //findNavController().navigate(R.id.customerDetailsAssessmentFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initUI() {
        binding.apply {
            // etPhoneNumber.setText("3567876509")
            header.text = getString(R.string.customer_assessment)
            rgIDNumberName.makeVisible()
            tvPhoneLookupTitle.text = getString(R.string.use_id)
            tiPhoneNumber.hint = getString(R.string.id_number)
            etPhoneNumber.inputType = InputType.TYPE_CLASS_TEXT
            tiPhoneNumber.placeholderText = "12345678W90"
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
        viewmodel.responseGStatus.observe(viewLifecycleOwner) {
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
        binding.btnSearch.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                binding.apply {
                    val selectedRadioButtonId: Int = binding.rgIDNumberName.checkedRadioButtonId
                    if (selectedRadioButtonId == R.id.rbIDNumber) {
                        val validId = binding.etPhoneNumber.text.toString()
                        if (validId.isEmpty()) {
                            etPhoneNumber.requestFocus()
                            tiPhoneNumber.error = "Required"
                        } else {
                            tiPhoneNumber.error = ""
                            btnSearch.isEnabled = false
                            val idLookUpDTO = CustomerIDLookUpDTO()
                            idLookUpDTO.idNumber = validId
                            viewmodel.idLookup(idLookUpDTO)
                        }
                    } else if ((selectedRadioButtonId == R.id.rbName)) {
                        val validName = binding.etName.text.toString()
                        if (validName.isEmpty()) {
                            etName.requestFocus()
                            tiName.error = "Required"
                        } else {
                            tiName.error = ""
                            btnSearch.isEnabled = false
                            val nameLookupDTO = NameLookupDTO(etName.text.toString(), 0)
                            viewmodel.customerNameLookup(nameLookupDTO)
                        }
                    }
                }
            } else {
                onNoNetworkDialog(requireContext())
            }
        }
        binding.apply {
            ivBack.setOnClickListener { v -> Navigation.findNavController(v).navigateUp() }
            viewmodel.statusId.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            val selectedRadioButtonId: Int =
                                binding.rgIDNumberName.checkedRadioButtonId
                            if (selectedRadioButtonId == R.id.rbIDNumber) {
                                binding.etPhoneNumber.setText("")
                                viewmodel.stopObserving()
                                binding.btnSearch.isEnabled = true
                                findNavController().navigate(R.id.action_customerIDLookupFragment_to_customerAssessmentHomeFragment)
                            } else if ((selectedRadioButtonId == R.id.rbName)) {
                                //viewmodel.nameLookUpData.observe(viewLifecycleOwner) { nameLookUpData ->
                                if (viewmodel.customerList.isNotEmpty()) {
                                    viewmodel.stopObserving()
                                    binding.btnSearch.isEnabled = true
                                    binding.etName.setText("")
                                    findNavController().navigate(R.id.action_customerIDLookupFragment_to_customerAssessmentListFragment)
                                } else {
                                    viewmodel.stopObserving()
                                    binding.btnSearch.isEnabled = true
                                    onInfoDialog("No customer in your branch linked with the name provided")
                                }
                                //}
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