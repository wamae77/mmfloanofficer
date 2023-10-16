package com.deefrent.rnd.fieldapp.ui.dsrProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentDsrProfileBinding
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.DsrProfileViewModel
import es.dmoral.toasty.Toasty

class DSRProfileFragment : Fragment() {
    private var _binding: FragmentDsrProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dsrProfileViewModel: DsrProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dsrProfileViewModel = ViewModelProvider(this).get(DsrProfileViewModel::class.java)
        _binding = FragmentDsrProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
            v.findNavController()
                .navigate(R.id.action_DSRProfileFragment_to_dashboardFragment)
        }
        getDSRProfile()
        return view
    }

    private fun getDSRProfile() {
        Constants.callDialog2("Getting profile details...", requireContext())
        dsrProfileViewModel.getDsrProfile().observe(viewLifecycleOwner) { getDsrProfileResponse ->
            if (getDsrProfileResponse != null) {
                Constants.cancelDialog()
                binding.tvCustomerName.text =
                    "${getDsrProfileResponse.dsrProfileData.dsrProfile.firstName} " +
                            "${getDsrProfileResponse.dsrProfileData.dsrProfile.lastName}"
                Glide.with(requireActivity())
                    .load("")
                    .circleCrop()
                    .placeholder(
                        AvatarGenerator.avatarImage(
                            requireContext(), 90,
                            AvatarConstants.CIRCLE,
                            getDsrProfileResponse.dsrProfileData.dsrProfile.firstName,
                            AvatarConstants.COLOR900
                        )
                    )
                    .into(binding.ivAvatar)
                binding.tvUsername.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.username
                binding.tvMobileNumber.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.mobileNo
                binding.tvEmail.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.email
                binding.tvStaffNumber.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.staffNo
                binding.tvLocation.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.location
                binding.tvDSRTeam.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.dsrTeam.teamName
                binding.tvDSRTeamLocation.text =
                    getDsrProfileResponse.dsrProfileData.dsrProfile.dsrTeam.teamLocation
            } else {
                Constants.cancelDialog()
                Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DSRProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}