package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AssessmentDashboardFragmentBinding
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel

class AssessmentDashboardFragment : Fragment() {
    private lateinit var binding: AssessmentDashboardFragmentBinding
    private lateinit var viewModel: AssessmentDashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AssessmentDashboardFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(AssessmentDashboardViewModel::class.java)
        handleBackButton()
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cl360View.setOnClickListener {
                findNavController().navigate(R.id.action_assessmentDashboardFragment_to_customerIDLookupFragment)
            }
            clIncompleteAess.setOnClickListener {
                findNavController().navigate(R.id.action_assessmentDashboardFragment_to_incompleteAssesmentFragment)
            }
        }
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    //findNavController().navigateUp()
                    findNavController().navigate(R.id.dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}