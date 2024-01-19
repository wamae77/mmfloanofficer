package com.deefrent.rnd.fieldapp.view.homepage.billers

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
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AccountExistsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentBillPaymentLookUpBinding
import com.deefrent.rnd.fieldapp.dtos.NameLookupDTO
import com.deefrent.rnd.fieldapp.dtos.lookUp.IDLookUpDTO
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus

class BillPaymentLookUpFragment : Fragment() {
    private lateinit var binding: FragmentBillPaymentLookUpBinding
    private lateinit var cardBinding: AccountExistsDialogBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(BillersViewModel::class.java)
    }

    val TAG  = "BillPaymentLookUp"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: kevo")
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBillPaymentLookUpBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        initUI()
        return binding.root
    }

    private fun initUI() {
        binding.apply {
            header.text = getString(R.string.bill_payment)
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
                        val validId = binding.etPhoneNumber.text.toString()
                        if (validId.isEmpty()) {
                            etPhoneNumber.requestFocus()
                            tiPhoneNumber.error = "Required"
                        } else {
                            tiPhoneNumber.error = ""
                            btnSearch.isEnabled = false
                            val idLookupDTO = IDLookUpDTO(validId, 0)
                            viewmodel.customerIDLookUp(idLookupDTO)
                        }
                    } else if ((selectedRadioButtonId == R.id.rbName)) {
                        val validId = binding.etName.text.toString()
                        if (validId.isEmpty()) {
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
            binding.ivBack.setOnClickListener {
                findNavController().navigateUp()
                //findNavController().navigate(R.id.action_billPaymentLookupFragment_to_dashboardFragment)
            }

            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            val selectedRadioButtonId: Int =
                                binding.rgIDNumberName.checkedRadioButtonId
                            if (selectedRadioButtonId == R.id.rbIDNumber) {
                                binding.etPhoneNumber.setText("")
                                findNavController().navigate(R.id.action_billPaymentLookupFragment_to_billersFragment)
                                viewmodel.stopObserving()
                                binding.btnSearch.isEnabled = true
                            } else if ((selectedRadioButtonId == R.id.rbName)) {
                                if (viewmodel.customerList.isNotEmpty()) {
                                    viewmodel.stopObserving()
                                    binding.btnSearch.isEnabled = true
                                    binding.etName.setText("")
                                    findNavController().navigate(R.id.action_billPaymentLookupFragment_to_customerListFragment)
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
                    findNavController().navigate(R.id.action_billPaymentLookupFragment_to_dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BillPaymentLookUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}