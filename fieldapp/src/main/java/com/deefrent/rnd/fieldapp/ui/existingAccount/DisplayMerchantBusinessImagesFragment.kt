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
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayMerchantBusinessImagesBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayMerchantBusinessImagesFragment : Fragment() {
    private var _binding: FragmentDisplayMerchantBusinessImagesBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentDisplayMerchantBusinessImagesBinding.inflate(inflater, container, false)
        val view = binding.root

        displayMerchantAgentDetails()
        return view
    }

    private fun displayMerchantAgentDetails() {
        existingAccountViewModel.apply {
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    binding.tvCustomerName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} "
                    val merchantAgentKYCList =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.merchantKYCList
                    val businessPermitName = merchantAgentKYCList[0].businessPermitDoc
                    val businessPermitUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$businessPermitName"
                    val businessLicenseName = merchantAgentKYCList[0].businessLicense
                    val businessLicenseUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$businessLicenseName"
                    val shopPhoto = merchantAgentKYCList[0].shopPhoto
                    val shopPhotoUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$shopPhoto"
                    val termsAndConditionDocName = merchantAgentKYCList[0].termsAndConditionDoc
                    val termsAndConditionDocUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$termsAndConditionDocName"
                    val companyRegistrationDocName = merchantAgentKYCList[0].companyRegistrationDoc
                    val companyRegistrationDocUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$companyRegistrationDocName"
                    Glide.with(requireActivity()).load(companyRegistrationDocUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivCompanyRegistrationDoc)
                    val kraPinCertificateName = merchantAgentKYCList[0].kraPinCertificate
                    val kraPinCertificateUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$kraPinCertificateName"
                    Glide.with(requireActivity()).load(kraPinCertificateUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivKRAPinCertificate)
                    Glide.with(requireActivity()).load(termsAndConditionDocUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivTermsAndConditionDoc)
                    Glide.with(requireActivity()).load(businessPermitUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivBusinessPermit)
                    Glide.with(requireActivity()).load(businessLicenseUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivBusinessLicense)
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
                    binding.ivBusinessPermit.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(businessPermitUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivBusinessLicense.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(businessLicenseUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
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
                    binding.ivTermsAndConditionDoc.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(termsAndConditionDocUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivCompanyRegistrationDoc.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(companyRegistrationDocUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivKRAPinCertificate.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(kraPinCertificateUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayMerchantBusinessImagesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}