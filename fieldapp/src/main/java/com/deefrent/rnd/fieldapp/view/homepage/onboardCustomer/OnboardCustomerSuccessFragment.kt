package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardCustomerSuccessBinding
import com.deefrent.rnd.fieldapp.utils.Constants

class OnboardCustomerSuccessFragment : Fragment() {
    private lateinit var binding:FragmentOnboardCustomerSuccessBinding
    private var firstname=""
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentOnboardCustomerSuccessBinding.inflate(layoutInflater)
        binding.apply {
            checkTitle.text="Congratulations!! Customer's account has been opened successfully"

            /*  viewmodel.cFirstName.observe(viewLifecycleOwner){
                  firstname=it
              }
              viewmodel.cLastName.observe(viewLifecycleOwner){name->
                  val customername="$firstname $name"
                  checkTitle.text="$customername Account has been opened successfully"
              }*/
            ivBack.setOnClickListener {
                findNavController().navigate(R.id.action_onboardCustomerSuccessFragment_to_dashboardFragment)
            }
            btnNotNow.setOnClickListener {
                findNavController().navigate(R.id.action_onboardCustomerSuccessFragment_to_dashboardFragment)
            }
            btnGoAssess.setOnClickListener {
                Constants.fromSummarySuccess=true
                findNavController().navigate(R.id.action_onboardSuccessFragment_to_incompleteAssesmentFragment)
            }
        }
        handleBackButton()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_onboardCustomerSuccessFragment_to_dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }



}