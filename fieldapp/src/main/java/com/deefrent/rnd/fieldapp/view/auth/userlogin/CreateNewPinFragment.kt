package com.deefrent.rnd.fieldapp.view.auth.userlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCreateNewPinBinding
import com.deefrent.rnd.fieldapp.view.auth.onboarding.AccountLookUpViewModel

class CreateNewPinFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentCreateNewPinBinding
    private lateinit var pinViewmodel: AccountLookUpViewModel

    /**the pin inputs*/
    private var one1: String? = null
    private var two2: String? = null
    private var three3: String? = null
    private var four4: String? = null
    private var mConfirmPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewPinBinding.inflate(layoutInflater)
        binding.tvEnterPin.text = "Create new pin"

        pinViewmodel = ViewModelProvider(requireActivity()).get(AccountLookUpViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
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
        }


    }

    private fun controlPinPad2(entry: String) {
        binding.apply {
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
                pinViewmodel.setPin(mConfirmPin.toString())
                findNavController().navigate(R.id.action_createNewPinFragment_to_confirmNewPinFragment)
                clearPin()
            }

        }


    }

    private fun deletePinEntry() {
        binding.apply {
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
        binding.apply {
            pin1.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            one1 = null
            pin2.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            two2 = null
            pin3.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            three3 = null
            pin4.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            four4 = null
            mConfirmPin = null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.appBar.setBackgroundColor(resources.getColor(R.color.white))
    }
}