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
import com.deefrent.rnd.fieldapp.databinding.FragmentDisplayCustomerDetailsBinding
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.github.chrisbanes.photoview.PhotoView

class DisplayCustomerDetailsFragment : Fragment() {
    private var _binding: FragmentDisplayCustomerDetailsBinding? = null
    private val binding get() = _binding!!
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //customerKYC = arguments?.getParcelable("customerDetails")
        //Log.d("customerDetails", "onCreate: ${customerKYC?.frontIdCapture}")
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayCustomerDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        displayCustomerDetails()
        return view
    }

    private fun displayCustomerDetails() {
        existingAccountViewModel.apply {
            customerDetailsResponse.observe(viewLifecycleOwner,
                { customerDetailsResponse ->
                    binding.tvCustomerName.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.firstName} " +
                                "${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                    binding.surname.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.sur_name}"
                    binding.firstname.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}"
                    binding.lastname.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                    binding.dob.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.dob}"
                    binding.employmentType.text =
                        "${customerDetailsResponse.customerDetailsData.customerDetails.employmentType.employmentTypeName}"
                    val customerKYCList =
                        customerDetailsResponse.customerDetailsData.customerDetails.customerKYCList
                    val passportPhotoName = customerKYCList[0].passportPhoto
                    val passportPhotoUrl =
                        "${BuildConfig.BASE_URL}merchant/fileupload2/$passportPhotoName"
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
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayCustomerDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}