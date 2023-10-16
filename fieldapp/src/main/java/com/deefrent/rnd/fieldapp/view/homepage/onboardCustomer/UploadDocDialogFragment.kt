package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentUploadDocDialogBinding
import com.deefrent.rnd.fieldapp.room.entities.CustomerDetailsEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.google.gson.Gson

class UploadDocDialogFragment : Fragment() {
    private lateinit var binding:FragmentUploadDocDialogBinding
    private lateinit var customerDetailEntity: CustomerDetailsEntity
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUploadDocDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.ivHome.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            getSavedItemsFromRoom(customerIDNumber)
        }
        binding.apply {
            viewmodel.responseGStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.mainPBar.makeVisible()
                            binding.progressbar.tvWait.text = "Uploading documents..."
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusDocCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            findNavController().navigate(R.id.onboardCustomerSuccessFragment)
                            Log.e("TAG", "initializeUI: ${Gson().toJson(customerDetailEntity)}", )
                            viewmodel.deleteCustomerD(customerDetailEntity)
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            findNavController().navigate(R.id.dashboardFragment)
                            toastyErrors("Error Occurred while uploading the documents...\nkindly try again later...")
                            viewmodel.stopObserving()

                        }
                    }
                }
            }
        }
    }
    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId)
            .observe(viewLifecycleOwner) { cdewithList ->
                customerDetailEntity = cdewithList.customerDetails
                Log.d("TAG", "MAINCHECK: ${Gson().toJson(cdewithList)}")
            }
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}