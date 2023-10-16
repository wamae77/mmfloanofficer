package com.deefrent.rnd.fieldapp.ui.onboardedAccounts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardedAccountsFilterBinding
import com.deefrent.rnd.fieldapp.ui.onboardAccount.FragmentAddViewpg
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.utils.autoPlayAdvertisement
import com.deefrent.rnd.fieldapp.viewModels.OnboardedAccountsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_customer_details.*
import java.text.SimpleDateFormat
import java.util.*

class OnboardedAccountsFilterFragment : Fragment() {
    private var _binding: FragmentOnboardedAccountsFilterBinding? = null
    private val binding get() = _binding!!
    private val onboardedAccountsViewModel: OnboardedAccountsViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardedAccountsFilterBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.ivBack.setOnClickListener { v ->
        }
        binding.btnSearch.setOnClickListener { v ->
            if (isValid()) {
                fetchOnboardedAccounts(v)
            }
        }
        loadSliders()
        binding.etStartDate.setOnClickListener {
            showMaterialStartDatePicker()
        }
        binding.etEndDate.setOnClickListener {
            showMaterialEndDatePicker()
        }
        return view
    }

    private fun fetchOnboardedAccounts(v: View) {
        Constants.callDialog2("Searching...", requireContext())
        onboardedAccountsViewModel.getOnboardedAccounts(
            binding.etStartDate.text.toString(),
            binding.etEndDate.text.toString(), "100", "1"
        )
            .observe(viewLifecycleOwner) { getOnboardedAccountsResponse ->
                when (getOnboardedAccountsResponse?.status) {
                    "success" -> {
                        onboardedAccountsViewModel.apply {
                            setOnboardedAccountsResponse(getOnboardedAccountsResponse)
                            setDateValues(
                                binding.etStartDate.text.toString(),
                                binding.etEndDate.text.toString()
                            )
                        }
                        Constants.cancelDialog()
                    }
                    "failed" -> {
                        Constants.cancelDialog()
                        Toasty.error(
                            requireContext(),
                            getOnboardedAccountsResponse.message,
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Constants.cancelDialog()
                        Toasty.error(
                            requireContext(),
                            "Error searching for onboarded Accounts",
                            Toasty.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
    }

    private fun showMaterialStartDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("START DATE")
        val materialDatePicker: MaterialDatePicker<Long> =
            materialDateBuilder.build() as MaterialDatePicker<Long>
        materialDatePicker.addOnPositiveButtonClickListener { selection: Long ->
            // Get the offset from our timezone and UTC.
            val timeZoneUTC: TimeZone = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().getTime()) * -1
            // Create a date format, then a date object with our offset
            val simpleFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val simpleFormat1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = Date(selection + offsetFromUTC)
            Log.d("showMaterialDatePicker", "date: " + simpleFormat1.format(date).toString())
            binding.etStartDate.setText(simpleFormat1.format(date).toString())
            //sharedViewModel.setDob(simpleFormat1.format(date).toString())
        }
        materialDatePicker.show(requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun showMaterialEndDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("END DATE")
        val materialDatePicker: MaterialDatePicker<Long> =
            materialDateBuilder.build() as MaterialDatePicker<Long>
        materialDatePicker.addOnPositiveButtonClickListener { selection: Long ->
            // Get the offset from our timezone and UTC.
            val timeZoneUTC: TimeZone = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().getTime()) * -1
            // Create a date format, then a date object with our offset
            val simpleFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val simpleFormat1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = Date(selection + offsetFromUTC)
            Log.d("showMaterialDatePicker", "date: " + simpleFormat1.format(date).toString())
            binding.etEndDate.setText(simpleFormat1.format(date).toString())
            //sharedViewModel.setDob(simpleFormat1.format(date).toString())
        }
        materialDatePicker.show(requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun isValid(): Boolean {
        var isValid = false
        when {
            binding.etStartDate.text.toString().isEmpty() -> {
                isValid = false
                Toasty.error(requireContext(), "Please select a Start Date", Toasty.LENGTH_LONG)
                    .show()
            }
            binding.etEndDate.text.toString().isEmpty() -> {
                isValid = false
                Toasty.error(requireContext(), "Please select an End Date", Toasty.LENGTH_LONG)
                    .show()
            }
            else -> {
                isValid = true
            }
        }
        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnboardedAccountsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun loadSliders() {
        val fragAd =
            FragmentAddViewpg(
                requireContext()
            )
        binding.pager.adapter = fragAd
        binding.tabDots.setupWithViewPager(binding.pager, true)
        fragAd.notifyDataSetChanged()
        autoPlayAdvertisement(binding.pager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}