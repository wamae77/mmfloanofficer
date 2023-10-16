package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.BuildConfig
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayMerchantDetailsBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayMerchantDetailsFragment : Fragment() {
    private var _binding: FragmentDisplayMerchantDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ExistingAccountSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayMerchantDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        displayMerchantAgentDetails()
        observeSharedViewModel()
        return view
    }

    private fun displayMerchantAgentDetails() {
        existingAccountViewModel.apply {
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    binding.tvCustomerName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName}"
                    binding.tvBusinessName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName}"
                    binding.tvPhoneNo.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                    binding.tvEmail.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.email}"
                    binding.tvBusinessType.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessTypeName}"
                    binding.natureOfBusiness.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.natureBusiness}"
                    val merchantAgentKYCList =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.merchantKYCList
                    val shopPhotoName = merchantAgentKYCList[0].shopPhoto
                    val shopPhotoUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$shopPhotoName"
                    Glide.with(requireActivity())
                        .load(shopPhotoUrl)
                        .circleCrop()
                        .placeholder(
                            AvatarGenerator.avatarImage(
                                requireContext(), 90,
                                AvatarConstants.CIRCLE,
                                merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName,
                                AvatarConstants.COLOR900
                            )
                        )
                        .into(binding.ivAvatar)
                    binding.ivAvatar.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(shopPhotoUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                })
        }
    }

    private fun observeSharedViewModel() {
        sharedViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    binding.tvAccountNumber.text =
                        "$accountNumber"
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayMerchantDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}