package com.deefrent.rnd.fieldapp.view.auth.userlogin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.utils.CURRENCY_CODE
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentLoginPinBinding
import com.deefrent.rnd.fieldapp.dtos.LoginDTO
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.utils.getGreetings
import com.deefrent.rnd.fieldapp.utils.hideKeyboard
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.onInfoDialogWarn
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.view.auth.onboarding.AccountLookUpViewModel
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.view.printreceipt.MoneyMartPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.jiboostfieldapp.ui.cLEARPRINTINGDATA
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs


class LoginPinFragment : BaseDaggerFragment() {
    private var username = ""
    private var firstname = ""
    private var canSetQuiz: Int = 0
    private var canChangePin: Boolean = false
    private lateinit var pinBinding: FragmentLoginPinBinding
    private lateinit var viewModel: AccountLookUpViewModel
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()

    /**the pin inputs*/
    private var one1: String? = null
    private var two2: String? = null
    private var three3: String? = null
    private var four4: String? = null
    private var mConfirmPin: String? = null

    /* @Inject
     lateinit var viewModelFactory: ViewModelProvider.Factory*/
    /*private val loginSessionSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)
            .get(LoginSessionSharedViewModel::class.java)
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.token = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pinBinding = FragmentLoginPinBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(AccountLookUpViewModel::class.java)
        username = AppPreferences.getPreferences(requireContext(), "usernamef").toString()
        firstname = AppPreferences.getPreferences(requireContext(), "firstNamef").toString()
        handleBackButton()
        viewModel.changePassword.observe(viewLifecycleOwner) { changePass ->
            Log.d("QUIZ oooCHECK", "$changePass")
            canChangePin = changePass
        }
        viewModel.isSecQuizSet.observe(viewLifecycleOwner) { setSec ->
            Log.d("QUIZ CHECK", "$setSec")
            canSetQuiz = setSec
        }
        hideKeyboard()
        changeStatusBarColor()
        return pinBinding.root
    }

    private fun changeStatusBarColor() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.white)
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }*/
    }

    private fun resetStatusBarColor() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            requireActivity().window.decorView.systemUiVisibility = 0
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.kcb_darker_blue)
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        resetStatusBarColor()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetStatusBarColor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetStatusBarColor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (firstname.isEmpty()) {
            pinBinding.tvGreetings.text = String.format(
                getGreetings()!!,
                ""
            )
        } else {
            pinBinding.tvGreetings.text = String.format(
                getGreetings()!!, firstname.capitalizeWords
            )
        }
        pinBinding.apply {
            ivBack.setOnClickListener { findNavController().navigateUp() }
            tvForgotPin.setOnClickListener {
                forgetPinDialog()
                //  findNavController().navigate(R.id.action_pinFragment_to_forgetPinDashboardFragment)
            }
            ivBack.setOnClickListener { findNavController().navigateUp() }
            btnOne.setOnClickListener { controlPinPad2("1") }
            btnTwo.setOnClickListener { controlPinPad2("2") }
            btnThree.setOnClickListener { controlPinPad2("3") }
            btnFour.setOnClickListener { controlPinPad2("4") }
            btnFive.setOnClickListener { controlPinPad2("5") }
            btnSix.setOnClickListener { controlPinPad2("6") }
            btnSeven.setOnClickListener { controlPinPad2("7") }
            btnEight.setOnClickListener { controlPinPad2("8") }
            btnNine.setOnClickListener { controlPinPad2("9") }
            btnZero.setOnClickListener { controlPinPad2("0") }
            btnDelete.setOnClickListener { deletePinEntry() }
            viewModel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.GONE
                            AppPreferences.setPreference(requireContext(), "isFirstLogin", "false")
                            cLEARPRINTINGDATA()
                            if (canChangePin) {
                                findNavController().navigate(R.id.action_pinFragment_to_createNewPinFragment)
                            } else {
                                loginSessionSharedViewModel.setIsFromLoginScreen(true)
                                findNavController().navigate(R.id.action_pinFragment_to_dashboardFragment)
                            }
                            viewModel.stopObserving()
                        }

                        0 -> {
                            if ("${viewModel.statusMessage.value}".contains("failed to connect")) {
                                toastyErrors("Check your internet connection and try again")
                            } else {

                                onInfoDialogWarn(viewModel.statusMessage.value)
                            }
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.VISIBLE
                            viewModel.stopObserving()

                        }

                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.VISIBLE
                            viewModel.stopObserving()
                        }
                    }
                }
            }
        }
        //
        pinBinding.view.visibilityView(false)
        pinBinding.view.setOnClickListener {
            if (pinBinding.clTestBtn.visibility == View.VISIBLE) {
                pinBinding.clTestBtn.visibility = View.GONE
            } else {
                pinBinding.clTestBtn.visibility = View.VISIBLE
            }
        }
        simulateTestForBioMetrics()

    }

    private fun simulateTestForBioMetrics() {
        pinBinding.btnReciept.setOnClickListener {
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

        pinBinding.btnFingerPrint.setOnClickListener {
            findNavController().navigate(R.id.enrollFingerPrintFragmentMethod2)
        }
    }

    private fun controlPinPad2(entry: String) {
        pinBinding.apply {
            if (one1 == null) {
                one1 = entry
                pin1.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            } else if (two2 == null) {
                two2 = entry
                pin2.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            } else if (three3 == null) {
                three3 = entry
                pin3.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            } else if (four4 == null) {
                four4 = entry
                pin4.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            }
            if (mConfirmPin == null) {
                mConfirmPin = entry
            } else {
                mConfirmPin += entry
            }
            if (mConfirmPin!!.length == 4) {
                val loginDTO = LoginDTO()
                loginDTO.password = mConfirmPin as String
                loginDTO.username = username
                loginDTO.isOfficer = 1
                if (isNetworkAvailable(requireContext())) {
                    pinBinding.clPin.visibility = View.GONE
                    pinBinding.avi.makeVisible()
                    viewModel.loginUser(loginDTO)
                } else {
                    clearPin()
                    pinBinding.clPin.makeVisible()
                    pinBinding.avi.makeGone()
                    toastyErrors("Check your internet connection and try again")
                }
            }
        }
    }

    private fun deletePinEntry() {
        pinBinding.apply {
            if (mConfirmPin != null && mConfirmPin!!.length > 0) {
                mConfirmPin = mConfirmPin!!.substring(0, mConfirmPin!!.length - 1)
            }
            if (four4 != null) {
                pin4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                four4 = null
            } else if (three3 != null) {
                pin3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                three3 = null
            } else if (two2 != null) {
                pin2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                two2 = null
            } else if (one1 != null) {
                pin1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                one1 = null
            }
        }
    }

    private fun clearPin() {
        pinBinding.apply {
            pin1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            one1 = null
            pin2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            two2 = null
            pin3.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            three3 = null
            pin4.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            four4 = null
            mConfirmPin = null
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

    fun exitDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Exit!")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("YES") { _, which ->
                requireActivity().finish()
            }
            .setNegativeButton("CANCEL") { dialog, which ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
        builder.show()
    }

    private fun forgetPinDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Forgot pin?")
            .setMessage("Have you forgotten your pin? Kindly visit the headquarter for pin reset, Thank you!")
            .setPositiveButton("OKAY") { dialog, which ->
                dialog.dismiss()
            }
            /*.setNegativeButton("CANCEL") {dialog, which ->
                dialog.dismiss()
            }*/
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
        builder.show()
    }

}