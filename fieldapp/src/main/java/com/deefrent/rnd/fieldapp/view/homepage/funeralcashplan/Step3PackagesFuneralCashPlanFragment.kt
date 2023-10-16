package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep3Binding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.CashPlanPackagesRequest
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FuneralCashPlanPackagesData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.FuneralCashPlanPackagesListAdapter
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.FuneralCashPlanPackageCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject

class Step3PackagesFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep3Binding>(
        FragmentFuneralCashPlanStep3Binding::inflate
    ) {

    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

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

        inflateUi()
    }


    private fun performApiRequest(
    ) {
        val customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )
        val cashPlanPackagesRequest = CashPlanPackagesRequest(
            customerIdNumber = customerData.idNumber.toString()
        )
        lifecycleScope.launch {
            viewModel.requestGetCashPlanPackages(
                cashPlanPackagesRequest
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        onInfoDialogWarn(it.error?.message.toString())
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            it.data?.data?.let { list -> inflateRecyclerView(list) }
                        } else {
                            onInfoDialogWarn(it.data?.message.toString())
                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }

    private fun inflateUi() {
//        inflateRecyclerView(FuneralCashPlanPackagesData.getFuneralCashPlanPackagesData())
        performApiRequest()
    }

    private val myCashOutChannelsAdapter by lazy {
        FuneralCashPlanPackagesListAdapter(
            funeralCashPlanPackageCallback
        )
    }

    private fun inflateRecyclerView(itemsRV: List<FuneralCashPlanPackagesData>) {
        if (itemsRV.isEmpty()) {
            binding.noShows.visibility = View.VISIBLE

            binding.rvPackages.visibility = View.GONE
        } else {
            binding.noShows.visibility = View.GONE
            binding.rvPackages.visibility = View.VISIBLE
            myCashOutChannelsAdapter.submitList(itemsRV)
            myCashOutChannelsAdapter?.notifyDataSetChanged()
            binding.rvPackages.apply {
                layoutManager = LinearLayoutManager(this.context!!)
                adapter = myCashOutChannelsAdapter
                setHasFixedSize(true)
            }
        }
    }

    private val funeralCashPlanPackageCallback =
        object : FuneralCashPlanPackageCallback {
            override fun onItemSelected(view: View, item: FuneralCashPlanPackagesData) {
                when (view.id) {
                    (R.id.btnApply) -> {
                        val args = Bundle()
                        args.putParcelable("Package", item)
                        commonSharedPreferences.saveStringData(
                            CommonSharedPreferences.SELECTED_PACKAGE,
                            Gson().toJson(item)
                        )
                        findNavController().navigate(
                            R.id.action_step3IdLookUpFuneralCashPlanFragment_to_step4PackagesFuneralCashPlanFragment,
                            args
                        )
                    }
                    else -> {
                        val args = Bundle()
                        args.putParcelable("Package", item)
                        commonSharedPreferences.saveStringData(
                            CommonSharedPreferences.SELECTED_PACKAGE,
                            Gson().toJson(item)
                        )
                        findNavController().navigate(
                            R.id.action_step3IdLookUpFuneralCashPlanFragment_to_step4PackagesFuneralCashPlanFragment,
                            args
                        )
                    }

                }
            }
        }
}