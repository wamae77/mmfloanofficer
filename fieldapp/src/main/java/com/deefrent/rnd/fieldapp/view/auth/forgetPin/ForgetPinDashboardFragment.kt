package com.deefrent.rnd.fieldapp.view.auth.forgetPin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.databinding.FragmentForgetPinDashboardBinding
import com.deefrent.rnd.fieldapp.utils.toastyInfos

class ForgetPinDashboardFragment : Fragment() {
    private lateinit var binding: FragmentForgetPinDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentForgetPinDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivBack.setOnClickListener { findNavController().navigateUp() }
            CLSecQuiz.setOnClickListener {
            }
            CLLocation.setOnClickListener {
                toastyInfos("Coming Soon")
              //  viewModel.setResetOption("visitBranch")
            }
        }
    }




}
