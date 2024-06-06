package com.deefrent.rnd.fieldapp.view.auth.onboarding
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentPhoneVerificationBinding
import com.deefrent.rnd.fieldapp.dtos.VerifyOtpDTO
import com.deefrent.rnd.fieldapp.services.SmsBroadcastReceiver
import com.deefrent.rnd.fieldapp.utils.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class PhoneVerificationFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)
            .get(AccountLookUpViewModel::class.java)
    }
    private lateinit var binding: FragmentPhoneVerificationBinding
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private var timerStarted:Boolean=false
    var check = Constants.TIMEDIFF * 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneVerificationBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(R.id.action_phoneVerification_to_pinFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSmsUserConsent()
        initCountdownTimer()
        val mobile= AppPreferences.getPreferences(requireContext(), "mobile")
        val number=mobile?.replace("(?<=.{3}).(?=.{3})".toRegex(),"*")
        binding.helloTxt.text= String.format(getString(R.string.we_have_sent_verification_code),number)

        hideKeyboard()
        viewModel.verifyStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                binding.progressbar.mainPBar.makeGone()
                when (it) {
                    1 -> {
                        binding.progressbar.mainPBar.makeGone()
                        findNavController().navigate(R.id.action_phoneVerification_to_pinFragment)
                    }
                    0 -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialog(viewModel.statusMessage.value)
                        viewModel.stopObserving()
                    }
                    else -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialog(getString(R.string.error_occurred))
                        viewModel.stopObserving()
                    }
                }
            }
        }
    }

    private fun initCountdownTimer()
    {
        timerStarted = true
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long)
            {
                val minutes = (millisUntilFinished / 1000)  / 60
                val seconds = (millisUntilFinished / 1000)  % 60
                val timeLeftFormatted =
                    java.lang.String.format(Locale.getDefault(), "%02d min :%02d sec"  , minutes, seconds)
                binding.resendTime.setText(timeLeftFormatted )
                binding.btnContinue.isEnabled = false
                binding.btnContinue.setOnClickListener {
                    binding.btnContinue.isClickable=false
                }


            }
            override fun onFinish()
            {
                binding.btnContinue.isEnabled = true
                binding.btnContinue.isClickable=true
                binding.btnContinue.setOnClickListener {
                    initCountdownTimer()
                    viewModel.resendOTP()
                }
                timerStarted = false
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_USER_CONSENT -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)!!
                    val code = fetchVerificationCode(message)
                    val digits = code.map(Character::getNumericValue)
                    binding.apply {
                        codeOne.setText(digits[0].toString().trim())
                        codeTwo.setText(digits[1].toString().trim())
                        codeThree.setText(digits[2].toString().trim())
                        codeFour.setText(digits[3].toString().trim())
                        codeFive.setText(digits[4].toString().trim())
                        codeSix.setText(digits[5].toString().trim())
                        binding.progressbar.mainPBar.makeVisible()
                        val verifyOtpDTO = VerifyOtpDTO()
                        verifyOtpDTO.token = code
                        viewModel.verifyDeviceOTP(verifyOtpDTO)                    }
                }
            }
        }
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(this.requireActivity()).also {
            //We can add user phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver().also {
            it.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    intent?.let { context -> startActivityForResult(context, REQ_USER_CONSENT) }

                }

                override fun onFailure() {
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.registerReceiver(smsBroadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }else{
            activity?.registerReceiver(smsBroadcastReceiver, intentFilter)
        }
    }

    /**
     * This method extracts the verification code from a message
     * @param in: The message where message ought to be extracted
     * @param codeLength: size of the verification code e.g 0100 is 4
     * @return returns the code
     */

    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
        //comment

    }


    companion object {
        const val REQ_USER_CONSENT = 100
    }
}