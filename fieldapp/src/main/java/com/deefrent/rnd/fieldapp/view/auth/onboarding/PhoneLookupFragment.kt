package com.deefrent.rnd.fieldapp.view.auth.onboarding

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.utils.CURRENCY_CODE
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentPhoneLookupBinding
import com.deefrent.rnd.fieldapp.databinding.NoAccountDialogBinding

import com.deefrent.rnd.fieldapp.dtos.AccountLookUpDTO
import com.deefrent.rnd.fieldapp.utils.FieldValidators
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.view.printreceipt.MoneyMartPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs

class PhoneLookupFragment : Fragment() {
    private var validatedPhone = ""
    private lateinit var binding: FragmentPhoneLookupBinding
    private lateinit var cardBinding: NoAccountDialogBinding
    private lateinit var viewModel:AccountLookUpViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (AppPreferences.getPreferences(requireContext(), "isFirstLogin") == ("false")) {
            findNavController().navigate(R.id.pinFragment)
        }
        handleBackButton()
        binding = FragmentPhoneLookupBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(AccountLookUpViewModel::class.java)
        initializeUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.statusLookup.observe(viewLifecycleOwner) {
                if (null != it) {
                    binding.btnSignIn.isEnabled = true
                    binding.progressbar.mainPBar.makeGone()
                    when (it) {
                        1 -> {
                            binding.etPhoneNumber.setText("")
                            viewModel.stopObserving()
                            viewModel.accountLookUpData.observe(viewLifecycleOwner) { data ->
                                if (data == null) {
                                    showNoAccDialog()
                                    viewModel.stopObserving()
                                } else {
                                    AppPreferences
                                    AppPreferences.setPreference(
                                        requireContext(),
                                        "usernamef",
                                        data?.username
                                    )
                                    AppPreferences.setPreference(
                                        requireContext(),
                                        "firstNamef",
                                        data?.firstName
                                    )
                                    findNavController().navigate(R.id.action_phoneLookup_to_phoneVerification)
                                }
                            }
                            binding.btnSignIn.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()                       }
                        0 -> {
                            viewModel.stopObserving()
                            binding.btnSignIn.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(viewModel.statusMessage.value)
                        }
                        else -> {
                            viewModel.stopObserving()
                            binding.btnSignIn.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
        }


        simulateTestForBioMetrics()
        binding.view.setOnClickListener {
            if (binding.clTestBtn.visibility == View.VISIBLE) {
                binding.clTestBtn.visibility = View.GONE
            } else {
                binding.clTestBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    exitDialog()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    fun exitDialog(){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Exit!")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("YES") { _, which ->
                requireActivity().finish()
            }
            .setNegativeButton("CANCEL") {dialog, which ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
        builder .show()
    }

    private fun showNoAccDialog() {
        val dialogBuilder=Dialog(requireContext())
        cardBinding =
            NoAccountDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialogBuilder.dismiss()
        }
        cardBinding.BTNOK.setOnClickListener {
            dialogBuilder.dismiss()
        }

        dialogBuilder.setContentView(cardBinding.root)
        dialogBuilder.show()
        dialogBuilder.setCancelable(false)
    }

    private fun initializeUI() {
/*
        binding.phoneNumberEdTxt2.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                binding.phoneNumberCV.makeVisible()
                binding.phoneNumber.makeGone()
                binding.phoneNumberEdTxt.isFocusableInTouchMode = true
                binding.phoneNumberEdTxt.requestFocus()
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.phoneNumberEdTxt, InputMethodManager.SHOW_IMPLICIT)
            }
        }
*/
        binding.etPhoneNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tiPhoneNumber.error=null
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tiPhoneNumber.error=null
            }
            override fun afterTextChanged(p0: Editable?) {
                binding.tiPhoneNumber.error=null
            }

        })
        binding.btnSignIn.setOnClickListener { v ->
            binding.apply {
                val validMsg = FieldValidators.VALIDINPUT
                validatedPhone = FieldValidators().formatCodePhoneNumber(binding.etPhoneNumber.text.toString())
                val validPhone = FieldValidators().validCodePhoneNUmber(validatedPhone)
                if (!validPhone.contentEquals(validMsg)) {
                    etPhoneNumber.requestFocus()
                    tiPhoneNumber.isErrorEnabled = true
                    tiPhoneNumber.error = validPhone
                } else {
                    tiPhoneNumber.isErrorEnabled = false
                    tiPhoneNumber.error = ""
                    btnSignIn.isEnabled = false
                    binding.progressbar.mainPBar.makeVisible()
                    val accountLookUpDTO=AccountLookUpDTO()
                    accountLookUpDTO.phone=validatedPhone
                    accountLookUpDTO.isOfficer=1
                    Log.d("TAG", "initializeUI: $validatedPhone")
                    AppPreferences.setPreference(requireContext(), "mobile",validatedPhone)
                    viewModel.accountLookup(accountLookUpDTO)
                }
            }
        }
    }


    private fun simulateTestForBioMetrics() {
        binding.btnReciept.setOnClickListener {
            val receiptTextArray = arrayOf<String>(
                MoneyMartPrintServiceActivity.centeredText("COLLECTIONS", 48),
                MoneyMartPrintServiceActivity.centeredText("Validation Successful", 44),
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "Txn Amount:    $CURRENCY_CODE 12000",
                "Txn Fee:       $CURRENCY_CODE 120",
                "Excise Duty:   $CURRENCY_CODE 100",
                "Total Amount:  $CURRENCY_CODE 36000",
                MoneyMartPrintServiceActivity.SEPARATOR_LINE,
                "\n",
                "A/C NO :  " + "QWETYUIUYTRE",
                "PHONE NO :   " + "0798997948"
            )

            PrinterConfigs.RECEIPT_TEXT_ARRAY = receiptTextArray
            PrinterConfigs.AGENT_CODE = "12334567"
            PrinterConfigs.TERMINAL_NO = "terminalID"
            PrinterConfigs.AGENT_NAME = "agentName" //operatorName
            PrinterConfigs.SERVER_BY = "operatorName"
            PrinterConfigs.AGENT_BRANCH_STREET = "bankStreet" //operatorId
            PrinterConfigs.TRANSACTION_REFERENCE = "5256565456545"//
            PrinterConfigs.TIME_OF_TRANSACTION_REQUEST = getCurrentDateTimeString()//
            PrinterConfigs.FINISH_ACTIVITY_ON_PRINT = true//
            PrinterConfigs.HAS_SIGNATURE_BITMAP = false//
            PrinterConfigs.QR_AUTH_CODE_TO_PRINT = ""//


            val intent = Intent(requireActivity(), MoneyMartPrintServiceActivity::class.java)
            startActivityForResult(intent, 234)
        }

        binding.btnFingerPrint.setOnClickListener {

            findNavController().navigate(R.id.enrollFingerPrintFragmentMethod2)
        }
    }

}