package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanCustomerPoliciesBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.CashPlanPackagesRequest
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialogWarn
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.FuneralCashPlanViewModel
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesMainAdapter
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesMainAdapterCallback
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerPoliciesFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanCustomerPoliciesBinding>(
        FragmentFuneralCashPlanCustomerPoliciesBinding::inflate
    ) {

    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private lateinit var customerData: FindCustomerData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Funeral Insurance", //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )
        inflateUi()
    }

    private fun inflateUi() {
        customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )


        performApiRequest()
        // inflateRecyclerView(CustomerPoliciesData.getCustomerPoliciesData())
    }

    private val customerPoliciesMainAdapter by lazy {
        CustomerPoliciesMainAdapter(
            customerPoliciesMainAdapterCallback
        )
    }

    private fun inflateRecyclerView(itemsRV: List<CashPlanSubscriptionsPoliciesData>) {
        if (itemsRV.isEmpty()) {
            binding.noShows.visibility = View.VISIBLE
            binding.rvPackages.visibility = View.GONE
        } else {
            binding.noShows.visibility = View.GONE
            binding.rvPackages.visibility = View.VISIBLE
            customerPoliciesMainAdapter.submitList(itemsRV)
            customerPoliciesMainAdapter?.notifyDataSetChanged()
            binding.rvPackages.apply {
                layoutManager = LinearLayoutManager(this.context!!)
                adapter = customerPoliciesMainAdapter
                setHasFixedSize(true)
            }
        }
    }

    private val customerPoliciesMainAdapterCallback =
        object : CustomerPoliciesMainAdapterCallback {
            override fun onItemSelected(view: View, item: CashPlanSubscriptionsPoliciesData) {
                when (view.id) {
                    (R.id.tvViewMore) -> {

                        commonSharedPreferences.saveStringData(
                            CommonSharedPreferences.CUSTOMER_SUBSCRIPTION_PACKAGE,
                            Gson().toJson(item)
                        )
                        findNavController().navigate(
                            R.id.action_customerPoliciesFuneralCashPlanFragment_to_customerPoliciesFuneralBreakDownFragment,
                        )
                    }
                    else -> {
                        findNavController().navigate(
                            R.id.action_customerPoliciesFuneralCashPlanFragment_to_customerPoliciesFuneralBreakDownFragment,
                        )
                    }

                }
            }
        }

    private fun performApiRequest(
    ) {
        val cashPlanPackagesRequest = CashPlanPackagesRequest(
            customerIdNumber = customerData.idNumber.toString()
        )
        lifecycleScope.launch {
            viewModel.cashPlanSubscriptions(
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
                            //inflateRecyclerView(emptyList())
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
}