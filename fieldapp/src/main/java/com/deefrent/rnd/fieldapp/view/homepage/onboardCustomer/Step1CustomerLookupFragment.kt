package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AccountExistsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerLookupBinding
import com.deefrent.rnd.fieldapp.dtos.CustomerLookUpDTO
import com.deefrent.rnd.fieldapp.utils.FieldValidators
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.onNoNetworkDialog
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import javax.inject.Inject

class Step1CustomerLookupFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentCustomerLookupBinding
    private lateinit var cardBinding: AccountExistsDialogBinding
    private var sharedPreferences: SharedPreferences? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }
    var phoneNumber = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerLookupBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        initUI()
        sharedPreferences =
            requireContext().getSharedPreferences("collateralsGuarantors", Context.MODE_PRIVATE)
        binding.tvPhoneLookupTitle.setOnClickListener {
            //  findNavController().navigate(R.id.summaryFragment)
        }
        return binding.root
    }

    private fun initUI() {
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
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.btnSearch.isEnabled = false
                        binding.progressbar.mainPBar.makeVisible()
                    }

                    GeneralResponseStatus.DONE -> {
                        binding.btnSearch.isEnabled = true
                        binding.progressbar.mainPBar.makeGone()
                    }

                    else -> {
                        binding.btnSearch.isEnabled = true
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.status.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        binding.etPhoneNumber.setText("")
                        /*viewmodel.accountLookUpData.observe(viewLifecycleOwner) { data ->
                            if (!data.registered) {
                                viewmodel.cIdNumber.postValue("")
                                val direction =
                                    CustomerLookupFragmentDirections.actionOnboardCustomerLookupFragmentToCustomerDetailsFragment(
                                        2
                                    )
                                findNavController().navigate(direction)
                            } else {
                                showAccountRegisteredDialog()
                            }
                        }*/
                        if (!viewmodel.registered) {
                            viewmodel.cIdNumber.postValue("")
                            viewmodel.isFromLookup = true
                            val direction =
                                Step1CustomerLookupFragmentDirections.actionOnboardCustomerLookupFragmentToCustomerDetailsFragment(
                                    2
                                )
                            findNavController().navigate(direction)
                        } else {
                            showAccountRegisteredDialog()
                        }
                        viewmodel.stopObserving()
                    }

                    0 -> {
                        if (viewmodel.statusMessage.value!!.contains("failed to connect")) {
                            toastyErrors("Check your internet connection and try again")
                        } else {
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        viewmodel.stopObserving()
                    }

                    else -> {
                        viewmodel.stopObserving()
                        onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }
        binding.btnSearch.setOnClickListener { v ->

            if (isNetworkAvailable(requireContext())) {
                binding.apply {
                    val validMsg = FieldValidators.VALIDINPUT
                    phoneNumber =
                        FieldValidators().formatPhoneNumber(binding.etPhoneNumber.text.toString())

                    val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                    if (!validPhone.contentEquals(validMsg)) {
                        etPhoneNumber.requestFocus()
                        tiPhoneNumber.error = validPhone
                    } else {
                        tiPhoneNumber.error = ""
                        binding.progressbar.mainPBar.makeVisible()
                        val accountLookUpDTO = CustomerLookUpDTO()
                        accountLookUpDTO.phone = phoneNumber
                        accountLookUpDTO.isOfficer = 0
                        accountLookUpDTO.isCustomerCheckByOfficer = 1
                        viewmodel.customerPhoneNumber.postValue(phoneNumber)
                        viewmodel.accountLookup(accountLookUpDTO)
                    }
                }
            } else {
                onNoNetworkDialog(requireContext())
            }
        }
        binding.apply {
            ivBack.setOnClickListener { v -> findNavController().navigateUp() }
        }

    }

    private fun showAccountRegisteredDialog() {
        val dialog = Dialog(requireContext())
        cardBinding =
            AccountExistsDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnNotNow.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnApplyLoan.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.incompleteAssesmentFragment)
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }

}