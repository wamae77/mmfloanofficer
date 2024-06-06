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
import com.deefrent.rnd.fieldapp.dtos.NewPinDTO
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.onboarding.AccountLookUpViewModel
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.toastyErrors

class ConfirmNewPinFragment : BaseDaggerFragment() {
    private lateinit var pinViewmodel: AccountLookUpViewModel
    private var username=""
    private var pin=""
    /**the pin inputs*/
    private var one1: String? = null
    private var two2: String? = null
    private var three3: String? = null
    private var four4: String? = null
    private var mConfirmPin: String? = null
    private lateinit var binding: FragmentCreateNewPinBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewPinBinding.inflate(layoutInflater)
        pinViewmodel=ViewModelProvider(requireActivity()).get(AccountLookUpViewModel::class.java)
        binding.tvEnterPin.text = "Confirm new pin"
        pinViewmodel.username.observe(viewLifecycleOwner) {
            username = it
        }
        pinViewmodel.pin.observe(viewLifecycleOwner) {
            pin = it
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener { findNavController().navigateUp() }
            btnOne.setOnClickListener { controlPinPad2("1") }
            btnTwo.setOnClickListener{controlPinPad2("2")}
            btnThree.setOnClickListener{ controlPinPad2("3")}
            btnFour.setOnClickListener{ controlPinPad2("4")}
            btnFive.setOnClickListener { controlPinPad2("5") }
            btnSix.setOnClickListener{controlPinPad2("6")}
            btnSeven.setOnClickListener { controlPinPad2("7") }
            btnEight.setOnClickListener{controlPinPad2("8")}
            btnNine.setOnClickListener{ controlPinPad2("9")}
            btnZero.setOnClickListener{ controlPinPad2("0")}
            btnDelete.setOnClickListener { deletePinEntry() }
            pinViewmodel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                        avi.makeGone()
                    when (it) {
                        1 -> {
                            clearPin()
                            avi.makeGone()
                            clPin.visibility = View.GONE
                            findNavController().navigate(R.id.action_confirmNewPinFragment_to_pinFragment)
                            pinViewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(pinViewmodel.statusMessage.value
                            )
                            clearPin()
                            avi.makeGone()
                            clPin.visibility = View.VISIBLE
                            pinViewmodel.stopObserving()

                        }
                        else -> {
                            onInfoDialog( getString(R.string.error_occurred))
                            clearPin()
                            avi.makeGone()
                            clPin.visibility = View.VISIBLE
                            pinViewmodel.stopObserving()
                        }
                    }
                }
            }
        }
    }
    private fun controlPinPad2(entry: String){
        binding.apply {
            if (one1==null) {
                one1 = entry
                pin1.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            }else if (two2==null){
                two2=entry
                pin2.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            }else if (three3==null){
                three3=entry
                pin3.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            }else if (four4==null){
                four4=entry
                pin4.background = ContextCompat.getDrawable(requireContext(), R.drawable.pin_active)
            }

            if (mConfirmPin == null) {
                mConfirmPin = entry
            } else {
                mConfirmPin += entry
            }
            if (mConfirmPin!!.length == 4) {
                if (mConfirmPin!=pin){
                    toastyErrors("Pin entered does not match")
                    clearPin()
                }else{
                    val newPinDTO= NewPinDTO()
                    newPinDTO.username= username
                    newPinDTO.password=pin
                    newPinDTO.confirm=mConfirmPin as String
                    clPin.visibility=View.GONE
                    avi.makeVisible()
                    pinViewmodel.setNewPIn(newPinDTO)
                }

            }

        }


    }

    private fun deletePinEntry() {
        binding.apply {
            if (mConfirmPin != null && mConfirmPin!!.length > 0) {
                mConfirmPin = mConfirmPin!!.substring(0, mConfirmPin!!.length - 1)
            }
            if (four4 != null) {
                pin4.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                four4 = null
            } else if (three3 != null) {
                pin3.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                three3 = null
            } else if (two2 != null) {
                pin2.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                two2 = null
            } else if (one1 != null) {
                pin1.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
                one1 = null
            }
        }
    }
    private fun clearPin(){
        binding.apply {
            pin1.background= ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            one1 = null
            pin2.background= ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            two2 = null
            pin3.background= ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            three3 = null
            pin4.background= ContextCompat.getDrawable(requireContext(), R.drawable.pin_inactive)
            four4 = null
            mConfirmPin = null
        }


    }
    override fun onResume() {
        super.onResume()
        binding.appBar.setBackgroundColor(resources.getColor(com.deefrent.rnd.common.R.color.white))
    }

}