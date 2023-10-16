package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentAssessSuccessBinding
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel

class AssessSuccessFragment : Fragment() {
    private lateinit var binding:FragmentAssessSuccessBinding
    private val lookupViewModel: LoanLookUpViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentAssessSuccessBinding.inflate(layoutInflater)
        binding.apply {
           /* viewmodel.parentId.observe(viewLifecycleOwner){
                val loanLookUpDTO= LoanLookUpDTO()
                loanLookUpDTO.idNumber = it
                loanLookUpDTO.isLoan=1
                lookupViewModel.loanLookUp(loanLookUpDTO)
            }*/
            ivBack.setOnClickListener {
                findNavController().navigate(R.id.dashboardFragment)

            }
            btnGoAssess.setOnClickListener {
                viewmodel.parentId.observe(viewLifecycleOwner){
                    Constants.isSummaryBackArrow=true
                    Constants.lookupId=it
                }
                findNavController().navigate(R.id.action_assessSuccessFragment_to_loanLookupFragment)
            }
            btnNotNow.setOnClickListener {
                findNavController().navigate(R.id.dashboardFragment)

            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                   findNavController().navigate(R.id.dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }



}