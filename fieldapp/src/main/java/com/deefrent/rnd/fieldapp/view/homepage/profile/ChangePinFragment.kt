package com.deefrent.rnd.fieldapp.view.homepage.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentChangePinBinding
import com.deefrent.rnd.fieldapp.dtos.ChangePinDTO
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialog
import com.deefrent.rnd.fieldapp.utils.toastySuccess
import com.deefrent.rnd.fieldapp.viewModels.ProfileViewModel
import es.dmoral.toasty.Toasty

class ChangePinFragment : Fragment() {
    private var _binding: FragmentChangePinBinding? = null
    private lateinit var viewmodel: ProfileViewModel
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePinBinding.inflate(inflater, container, false)
        val view = binding.root
        viewmodel=ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_onboardChangePINFragment_to_dashboardFragment)
        }
        viewmodel.status.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        binding.btnSubmit.isEnabled=true
                        binding.progressbar.mainPBar.makeGone()
                        toastySuccess("PIN changed successfully")
                        findNavController().navigate(R.id.pinFragment)
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        binding.progressbar.mainPBar.makeGone()
                        binding.btnSubmit.isEnabled=true
                        viewmodel.stopObserving()
                        onInfoDialog(viewmodel.statusMessage.value)
                    }
                    else -> {
                        binding.btnSubmit.isEnabled=true
                        binding.progressbar.mainPBar.makeGone()
                        viewmodel.stopObserving()

                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener{
            if(isValid()){
                binding.progressbar.mainPBar.makeVisible()
                binding.btnSubmit.isEnabled=false
                val changePinDTO=ChangePinDTO()
                changePinDTO.old_password=binding.etCurrentPIN.text.toString()
                changePinDTO.new_password=binding.etConfirmNewPIN.text.toString()
                viewmodel.changePin(changePinDTO)
               // changePIN(binding.etConfirmNewPIN.text.toString())
            }
        }
        return view
    }

    private fun changePIN(pin: String) {

    }

    private fun isValid(): Boolean {
        var isValid: Boolean
        if (binding.etCurrentPIN.text.toString().isEmpty() || binding.etNewPIN.text.toString()
                .isEmpty()
            || binding.etConfirmNewPIN.text.toString().isEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the fields", Toasty.LENGTH_LONG)
                .show()
        } else if (binding.etConfirmNewPIN.text.toString()!=binding.etNewPIN.text.toString()) {
            isValid=false
            Toasty.error(requireContext(), "The PINs you entered do not match", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePinFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}