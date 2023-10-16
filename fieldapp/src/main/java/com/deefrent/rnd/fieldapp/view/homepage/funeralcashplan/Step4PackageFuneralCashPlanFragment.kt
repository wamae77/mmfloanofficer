package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep4Binding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FuneralCashPlanPackagesData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_funeral_cash_plan_step_4.*
import javax.inject.Inject

class Step4PackageFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep4Binding>(
        FragmentFuneralCashPlanStep4Binding::inflate
    ) {
    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel
    private val pinViewmodel: PinViewModel by activityViewModels()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private lateinit var selectedPackage: FuneralCashPlanPackagesData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedPackage = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.SELECTED_PACKAGE),
            FuneralCashPlanPackagesData::class.java
        )

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = selectedPackage.name, //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )


        binding.tvDependantsDesc.text = "Up to 4 dependants \n Minor ${
            selectedPackage.minorContributionAmount
        } USD\n Adult ${
            selectedPackage.adultDependantContribution
        } USD"
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(
                R.id.action_step4IdLookUpFuneralCashPlanFragment_to_step5PackagesFuneralCashPlanFragment
            )
        }
    }
}