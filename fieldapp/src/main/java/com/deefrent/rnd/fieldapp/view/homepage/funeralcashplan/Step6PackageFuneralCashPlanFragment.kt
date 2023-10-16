package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.dialogs.dialog_confirm.ConfirmDialogCallBacks
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep6Binding
import com.deefrent.rnd.fieldapp.dtos.VerifyUserDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.GetPayableAmountRequest
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FuneralCashPlanPackagesData
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import request.Dependant
import request.FuneralCashPlanSubscribeRequest
import javax.inject.Inject

class Step6PackageFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep6Binding>(
        FragmentFuneralCashPlanStep6Binding::inflate
    ) {

    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel
    private val sharedViewModel: SharedFuneralCashPlanViewModel by activityViewModels()
    private val pinViewmodel: PinViewModel by activityViewModels()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    private lateinit var selectedPackage: FuneralCashPlanPackagesData

    private lateinit var customerData: FindCustomerData
    private var dialogDetailCommonHashSet = HashSet<DialogDetailCommon>()

    private lateinit var payment_method: String

    override fun onResume() {
        super.onResume()

        /* val arrayAdapterPaymentAccount = ArrayAdapter(
             requireContext(),
             android.R.layout.simple_expandable_list_item_1,
             emptyList()
         )
         //ModeOfPayment
         binding.acRelationship.setAdapter(arrayAdapterPaymentAccount)
         binding.acRelationship.setOnItemClickListener { parent, view, position, id ->
             val selectedItem = arrayAdapterPaymentAccount.getItem(position)
             payment_method = selectedItem.toString()
             Log.e("", "edtModeOfPayment ${selectedItem}")
         }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val pin: String = bundle.get("pin") as String
            performAuthentication(pin)
        }

        customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )

        selectedPackage = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.SELECTED_PACKAGE),
            FuneralCashPlanPackagesData::class.java
        )

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = selectedPackage.name.toString(), //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        binding.btnContinue.setOnClickListener {
            if (validateField()) {
                sendRequest()
            }
        }

        Log.e("DEPENDATS", "dependantMutableLiveData ${sharedViewModel.dependantsList.toString()}")
        getPayableAmount(sharedViewModel.dependantsList as ArrayList<Dependant>)

    }

    private fun sendRequest() {
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "TOTAL AMOUNT:",
                content = binding.etAmount.text.toString().ifEmpty { "0" }
            )
        )
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "PACKAGE:",
                content = selectedPackage.name.toString()
            )
        )
        dialogDetailCommonHashSet.add(
            DialogDetailCommon(
                label = "DEPENDANTS:",
                content = sharedViewModel.dependantsList.size.toString()
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


    private fun performApiRequest() {
        //add yourself to the list of dependants and a beneficiary

        val name = customerData.firstName
        val phone = customerData.phone
        val idNumber = customerData.idNumber
        val dob = ""


        val funeralCashPlanSubscribeRequest = FuneralCashPlanSubscribeRequest(
            dependants = sharedViewModel.dependantsList as ArrayList<Dependant>,
            packageId = selectedPackage.id,
            paymentAmount = if (binding.etAmount.text.toString()
                    .isNullOrEmpty()
            ) 0 else binding.etAmount.text.toString().toInt(),
            customerIdNumber = customerData.idNumber
        )
        lifecycleScope.launch {
            viewModel.cashPlanSubscribe(funeralCashPlanSubscribeRequest).collect {
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

    private fun getPayableAmount(list: ArrayList<Dependant>) {
        //add yourself to the list of dependants and a beneficiary

        val name = customerData.firstName
        val phone = customerData.phone
        val idNumber = customerData.idNumber
        val dob = ""

        val getPayableAmountRequest = GetPayableAmountRequest(
            dependants = list,
            packageId = selectedPackage.id,
            customerIdNumber = customerData.idNumber.toString()
        )
        lifecycleScope.launch {
            viewModel.getPayableAmount(getPayableAmountRequest).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialogWarn(
                            responseMessage = it.error?.message.toString()
                        )
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            if (it.data?.payableAmount?.amount != null) {
                                binding.etAmount.setText(it.data?.payableAmount?.amount)
                            } else {
                                toastyErrors("Error getting payable amount")
                            }
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

    private fun validateField(): Boolean {
        if (binding.etAmount.text.toString().isEmpty()) {
            binding.etAmount.error = getString(R.string.required)
            return false
        } else {
            return true
        }
    }
}