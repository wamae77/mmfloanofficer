package com.deefrent.rnd.fieldapp.view.auth.userlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCreateNewPinBinding
import com.deefrent.rnd.fieldapp.dtos.DefaultPinDTO
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.ForgetPinViewModel
import com.deefrent.rnd.fieldapp.utils.onInfoDialog


class DefaultPinFragment : Fragment() {
    private lateinit var pinBinding: FragmentCreateNewPinBinding
    private lateinit var pinViewmodel:ForgetPinViewModel
    private var username=""
    /**the pin inputs*/
    private var one1: String? = null
    private var two2: String? = null
    private var three3: String? = null
    private var four4: String? = null
    private var mConfirmPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pinBinding= FragmentCreateNewPinBinding.inflate(layoutInflater)
        pinViewmodel=ViewModelProvider(requireActivity()).get(ForgetPinViewModel::class.java)
        return pinBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pinViewmodel.username.observe(viewLifecycleOwner) {
            username = it
        }
        pinBinding.apply {
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
                    pinBinding.avi.makeGone()
                    when (it) {
                        1 -> {
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.GONE
                          findNavController().navigate(R.id.action_defaultPinFragment_to_createNewPinFragment)
                            pinViewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog( pinViewmodel.statusMessage.value
                            )
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.VISIBLE
                            pinViewmodel.stopObserving()

                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            clearPin()
                            pinBinding.avi.makeGone()
                            pinBinding.clPin.visibility = View.VISIBLE
                            pinViewmodel.stopObserving()
                        }
                    }
                }
            }

        }




    }
    private fun controlPinPad2(entry: String){
        pinBinding.apply {
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
               val defaultPin = DefaultPinDTO()
                val user=AppPreferences.getPreferences(requireContext(),"usernamef").toString()
                defaultPin.username=user
                defaultPin.defaultPin=mConfirmPin as String
                pinBinding.clPin.visibility=View.GONE
               pinBinding.avi.makeVisible()
               pinViewmodel.verifyDefaultPin(defaultPin)
            }

        }


    }

    private fun deletePinEntry() {
        pinBinding.apply {
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
        pinBinding.apply {
            pin1.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            one1 = null
            pin2.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            two2 = null
            pin3.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            three3 = null
            pin4.background= ContextCompat.getDrawable(requireContext(), R.drawable.inactive_pin_bg)
            four4 = null
            mConfirmPin=null
        }


    }

    override fun onResume() {
        super.onResume()
        pinBinding.appBar.setBackgroundColor(resources.getColor(com.deefrent.rnd.common.R.color.white))
    }
}

