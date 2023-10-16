package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.dialogs.dialog_confirm.ConfirmDialogCallBacks
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCollectPremiumsBinding
import com.deefrent.rnd.fieldapp.dtos.VerifyUserDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.CashPlanSubscriptionPayRequest
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialogWarn
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.FuneralCashPlanViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject

class CollectPremiumsFragment :
    BaseMoneyMartBindedFragment<FragmentCollectPremiumsBinding>(
        FragmentCollectPremiumsBinding::inflate
    ) {
    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    lateinit var cashPlanSubscriptionsPoliciesData: CashPlanSubscriptionsPoliciesData
    private lateinit var customerData: FindCustomerData
    private var dialogDetailCommonHashSet = HashSet<DialogDetailCommon>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val pin: String = bundle.get("pin") as String

            performAuthentication(pin)
        }

        cashPlanSubscriptionsPoliciesData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_SUBSCRIPTION_PACKAGE),
            CashPlanSubscriptionsPoliciesData::class.java
        )
        customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )
        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = getString(com.deefrent.rnd.common.R.string.collect_premiums),
            action = {
                findNavController().navigateUp()
            }
        )

        binding.btnCOLLECTPREMIUMS.setOnClickListener {
            if (validateField()) {
                sendRequest()
                //  findNavController().navigate(R.id.dashboardFragment)
            }
        }


    }

    private fun validateField(): Boolean {

        if (binding.edtAmount.text.toString().isEmpty()) {
            binding.edtAmount.error = getString(R.string.required)
            return false
        } else if (binding.edtAmount.text.toString()
                .toInt() > cashPlanSubscriptionsPoliciesData.amountPayable
        ) {
            binding.edtAmount.error =
                "You can not pay more than expected" //getString(R.string.required)
            return false
        } else {
            return true
        }
    }

    private fun sendRequest() {
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "TOTAL PAYABLE AMOUNT:",
                content = cashPlanSubscriptionsPoliciesData.amountPayable.toString()
            )
        )
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "PACKAGE:",
                content = cashPlanSubscriptionsPoliciesData.name.toString()
            )
        )
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "DEPENDANTS:",
                content = cashPlanSubscriptionsPoliciesData.dependants?.size.toString()
            )
        )
        lifecycleScope.launch {
            showConfirmationDialog(
                "Confirm Payment",
                "",
                dialogDetailCommons = dialogDetailCommonHashSet.toMutableList(),
                dialogCallbackTelco
            )
        }
    }

    private val dialogCallbackTelco = object : ConfirmDialogCallBacks {
        override fun confirm() {
            lifecycleScope.launch {
                findNavController().navigate(R.id.commonAuthFragment)
            }
        }

        override fun cancel() {
            Log.e("", "cancel")
        }
    }

    private fun performApiRequest(
    ) {
        val cashPlanSubscriptionPayRequest = CashPlanSubscriptionPayRequest(
            subscriptionId = cashPlanSubscriptionsPoliciesData.id,
            amount = binding.edtAmount.text.toString().toInt(),
            walletAccountId = 0,
            customerIdNumber = customerData.idNumber.toString()
        )
        lifecycleScope.launch {
            viewModel.cashPlanSubscriptionPay(
                cashPlanSubscriptionPayRequest
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        onInfoDialogWarn(
                            responseMessage = it.error?.message.toString()
                        )
                        binding.progressbar.mainPBar.makeGone()
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            findNavController().navigate(R.id.dashboardFragment)
                        } else {
                            onInfoDialogWarn(
                                responseMessage = it.data?.message.toString()
                            )
                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }

    private fun performAuthentication(pin: String) {
        val verifyUserDTO = VerifyUserDTO()
        verifyUserDTO.password = pin
        lifecycleScope.launch {
            viewModel.verifyUser(verifyUserDTO).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        onInfoDialogWarn(
                            responseMessage = it.error?.message.toString()
                        )
                        binding.progressbar.mainPBar.makeGone()
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            performApiRequest()
                        } else {
                            onInfoDialogWarn(
                                responseMessage = it.data?.message.toString()
                            )
                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }

}