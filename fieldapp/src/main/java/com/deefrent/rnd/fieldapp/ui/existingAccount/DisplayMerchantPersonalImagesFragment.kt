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
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayMerchantPersonalImagesBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayPersonalImagesFragment : Fragment() {
    private var _binding: FragmentDisplayMerchantPersonalImagesBinding? = null
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
        _binding = FragmentDisplayMerchantPersonalImagesBinding.inflate(inflater, container, false)
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
                    val signatureDocName = merchantAgentKYCList[0].signatureDocDoc
                    val signatureDocUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$signatureDocName"
                    val goodConductName = merchantAgentKYCList[0].certificateOFGoodConduct
                    val goodConductUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$goodConductName"
                    val passportPhoto = merchantAgentKYCList[0].customerPhoto
                    val passportPhotoUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$passportPhoto"
                    val shopPhoto = merchantAgentKYCList[0].shopPhoto
                    val shopPhotoUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$shopPhoto"
                    val backIDName = merchantAgentKYCList[0].backID
                    val backIDUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$backIDName"
                    val frontIDName = merchantAgentKYCList[0].frontID
                    val frontIDUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$frontIDName"
                    Glide.with(requireActivity()).load(signatureDocUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivSignatureDoc)
                    val fieldFormName = merchantAgentKYCList[0].fieldApplicationForm
                    val fieldFormUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$fieldFormName"
                    Glide.with(requireActivity()).load(backIDUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivBackID)
                    Glide.with(requireActivity()).load(frontIDUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivFrontID)
                    Glide.with(requireActivity()).load(goodConductUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivGoodConduct)
                    Glide.with(requireActivity()).load(fieldFormUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivFieldForm)
                    Glide.with(requireActivity()).load(passportPhotoUrl)
                        .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                        .into(binding.ivPassport)
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
                    binding.ivSignatureDoc.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(signatureDocUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivBackID.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(backIDUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivGoodConduct.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(goodConductUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivFrontID.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(frontIDUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivPassport.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(passportPhotoUrl)
                            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                            .into(ivImagePreview)
                        mBuilder.setView(mView)
                        val mDialog: AlertDialog = mBuilder.create()
                        mDialog.show()
                    }
                    binding.ivFieldForm.setOnClickListener {
                        val mBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context, R.style.WrapContentDialog)
                        val mView: View =
                            layoutInflater.inflate(R.layout.preview_image, null)
                        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                        Glide.with(requireActivity()).load(fieldFormUrl)
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
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayPersonalImagesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}