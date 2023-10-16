package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep2Binding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import com.google.gson.Gson
import javax.inject.Inject

class Step2DetailsFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep2Binding>(
        FragmentFuneralCashPlanStep2Binding::inflate
    ) {
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }


    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = getString(com.deefrent.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        inflateUI()


    }

    private fun inflateUI() {
        binding.cVApplyForFuneralInsuranceture.setOnClickListener {
            findNavController().navigate(R.id.action_step2IdLookUpFuneralCashPlanFragment_to_step3PackagesFuneralCashPlanFragment)
        }
        binding.cVCustomerPolicies.setOnClickListener {
            findNavController().navigate(R.id.action_step2IdLookUpFuneralCashPlanFragment_to_customerPoliciesFuneralCashPlanFragment)
        }


        val customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )
        binding.tvPolicies.text = customerData.policies.toString()

        val idNumber = customerData?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
        val customerName = "${customerData?.firstName} ${customerData?.lastName}"
        val creditRating = customerData.creditRating

        /*       binding.tvCreditRating.text =
                   if (creditRating.isNotEmpty()) "Credit Rating: $creditRating" else "Credit Rating: Not Known"*/

        binding.tvAccountNumber.text = String.format(
            getString(R.string.acc), "$customerName -" +
                    "\n$idNumber"
        )
    }


}