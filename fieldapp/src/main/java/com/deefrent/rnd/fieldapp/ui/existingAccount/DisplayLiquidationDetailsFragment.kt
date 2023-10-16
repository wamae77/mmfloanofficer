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
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayLiquidationDetailsBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayLiquidationDetailsFragment : Fragment() {
    private var _binding: FragmentDisplayLiquidationDetailsBinding? = null
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
        _binding = FragmentDisplayLiquidationDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        displayMerchantAgentDetails()
        return view
    }

    private fun displayMerchantAgentDetails() {
        /*binding.tvLiquidationType.text = "${merchantDetails?.liquidationType}"
        binding.tvLiquidationRate.text = "${merchantDetails?.liquidationRate}"
        binding.tvBranchName.text = "${merchantDetails?.branchName}"
        binding.tvBankName.text = "${merchantDetails?.bankName}"
        binding.tvLiquidationAccountName.text = "${merchantDetails?.accountName}"
        binding.tvLiquidationAccountNumber.text = "${merchantDetails?.accountNumber}"*/
        existingAccountViewModel.apply {
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    binding.tvCustomerName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName}"
                    binding.tvLiquidationType.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.liquidationType}"
                    binding.tvLiquidationRate.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.liquidationRate}"
                    binding.tvLiquidationAccountName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.accountName}"
                    binding.tvLiquidationAccountNumber.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.accountNumber}"
                    binding.tvBankName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.bankName}"
                    binding.tvBranchName.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.branchName}"
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayLiquidationDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}