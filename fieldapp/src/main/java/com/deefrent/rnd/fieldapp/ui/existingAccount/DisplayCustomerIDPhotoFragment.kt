package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.BuildConfig
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayCustomerIdPhotoBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayCustomerIDPhotoFragment : Fragment() {
    private var _binding: FragmentDisplayCustomerIdPhotoBinding? = null
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
        _binding = FragmentDisplayCustomerIdPhotoBinding.inflate(inflater, container, false)
        val view = binding.root

        displayCustomerDetails()
        return view
    }

    private fun displayCustomerDetails() {
        existingAccountViewModel.apply {
            customerDetailsResponse.observe(viewLifecycleOwner
            ) { customerDetailsResponse ->
                binding.tvCustomerName.text =
                    "${customerDetailsResponse.customerDetailsData.customerDetails.firstName} " +
                            "${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                val customerKYCList =
                    customerDetailsResponse.customerDetailsData.customerDetails.customerKYCList
                val frontIDName = customerKYCList[0].frontIdCapture
                val frontIDUrl = "${BuildConfig.BASE_URL}merchant/fileupload2/$frontIDName"
                val backIDName = customerKYCList[0].backIdCapture
                val backIDUrl = "${BuildConfig.BASE_URL}merchant/fileupload2/$backIDName"
                val passportPhotoName = customerKYCList[0].passportPhoto
                val passportPhotoUrl =
                    "${BuildConfig.BASE_URL}merchant/fileupload2/$passportPhotoName"
                Glide.with(requireActivity()).load(frontIDUrl)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(binding.ivFrontID)
                Glide.with(requireActivity()).load(backIDUrl)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(binding.ivBackID)
                Glide.with(requireActivity())
                    .load(passportPhotoUrl)
                    .circleCrop()
                    .placeholder(
                        AvatarGenerator.avatarImage(
                            requireContext(), 90,
                            AvatarConstants.CIRCLE,
                            customerDetailsResponse.customerDetailsData.customerDetails.firstName,
                            AvatarConstants.COLOR900
                        )
                    )
                    .into(binding.ivAvatar)
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
                binding.ivAvatar.setOnClickListener {
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
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayCustomerIDPhotoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}