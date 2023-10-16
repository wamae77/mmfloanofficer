package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

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
import com.deefrent.rnd.fieldapp.data.adapters.PagerAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentIncompleteAssesmentBinding
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.android.material.tabs.TabLayoutMediator

class IncompleteAssesmentFragment : Fragment() {
    private var isToLocal=false
    private lateinit var binding:FragmentIncompleteAssesmentBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIncompleteAssesmentBinding.inflate(layoutInflater)
        viewmodel.isToLocal.observe(viewLifecycleOwner) { track ->
            Log.d("TAG", "onViewtrack: $track")
            isToLocal=track
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
        binding.apply {
            ivBack.setOnClickListener {
                if (isToLocal|| Constants.fromSummarySuccess){
                    viewmodel.isToLocal.postValue(true)
                    findNavController().navigate(R.id.assessmentDashboardFragment)
                }else{
                    Constants.fromSummarySuccess=false
                    viewmodel.isToLocal.postValue(false)
                    findNavController().navigate(R.id.assessmentDashboardFragment)
                }

            }

        }
        handleBackButton()
    }
    private fun setViewPager() {
        val adapter = PagerAdapter(this)
        binding.viewPager.adapter = adapter

        //Tab titles
        val titles = arrayOf("Local Assessment", "Other Assessment")
        adapter.addFragment(LocalAssessmetFragment())
        adapter.addFragment(OtherAssessmentFragment())
        //Attach tab mediator
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position -> tab.text = (titles[position]) }.attach()

        // Line disables swiping to allow viewpager in recyclerview to be swipeable
        //  binding.viewPager.isUserInputEnabled = false
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    /*if (isToLocal|| Constants.fromSummarySuccess){
                        viewmodel.isToLocal.postValue(true)
                        findNavController().navigateUp()
                        //findNavController().navigate(R.id.assessmentDashboardFragment)
                    }else{
                        Constants.fromSummarySuccess=false
                        viewmodel.isToLocal.postValue(false)
                        findNavController().navigateUp()
                        //findNavController().navigate(R.id.assessmentDashboardFragment)
                    }*/
                    if (isToLocal|| Constants.fromSummarySuccess){
                        viewmodel.isToLocal.postValue(true)
                        findNavController().navigate(R.id.assessmentDashboardFragment)
                    }else{
                        Constants.fromSummarySuccess=false
                        viewmodel.isToLocal.postValue(false)
                        findNavController().navigate(R.id.assessmentDashboardFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}