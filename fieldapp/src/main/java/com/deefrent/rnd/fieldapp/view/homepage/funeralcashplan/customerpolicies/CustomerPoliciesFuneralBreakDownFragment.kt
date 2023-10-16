package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCustomerPoliciesBreakdownBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesDependants
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesPayments
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.FuneralCashPlanViewModel
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesDependantAdapter
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesDependantAdapterCallback
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesPremiumsPaidAdapter
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerPoliciesPremiumsPaidAdapterCallback
import com.google.gson.Gson
import request.Dependant
import javax.inject.Inject

class CustomerPoliciesFuneralBreakDownFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCustomerPoliciesBreakdownBinding>(
        FragmentFuneralCustomerPoliciesBreakdownBinding::inflate
    ) {
    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    lateinit var cashPlanSubscriptionsPoliciesData: CashPlanSubscriptionsPoliciesData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cashPlanSubscriptionsPoliciesData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_SUBSCRIPTION_PACKAGE),
            CashPlanSubscriptionsPoliciesData::class.java
        )

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = cashPlanSubscriptionsPoliciesData.name, //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        binding.tvDependantNumber.setText(cashPlanSubscriptionsPoliciesData.dependants?.size.toString())
        binding.tvStatus.setText(if (cashPlanSubscriptionsPoliciesData.isActive == 0) "InActive" else "Active")
        binding.tvDueDate.setText(cashPlanSubscriptionsPoliciesData.lastPaymentDate.toString())
        binding.tvAmountPayable.setText(cashPlanSubscriptionsPoliciesData.amountPayable.toString())
        binding.btnCOLLECTPREMIUMS.setOnClickListener {
            findNavController().navigate(R.id.action_customerPoliciesFuneralBreakDownFragment_to_collectPremiumsFragment)
        }
        inflateUi()
    }

    private fun inflateUi() {
        Log.e("", "---------------------------\n${cashPlanSubscriptionsPoliciesData.dependants}")
        Log.e("", "---------------------------\n${cashPlanSubscriptionsPoliciesData.payments}")
        // cashPlanSubscriptionsPoliciesData.dependants?.let { inflateDependantRecyclerView(it) }
        // cashPlanSubscriptionsPoliciesData.payments?.let { inflatePremiumsPaidDataRecyclerView(it) }

        inflateDependantRecyclerView(cashPlanSubscriptionsPoliciesData.dependants!!)
        inflatePremiumsPaidDataRecyclerView(cashPlanSubscriptionsPoliciesData.payments!!)
    }

    private val customerPoliciesDependantAdapter by lazy {
        CustomerPoliciesDependantAdapter(
            customerPoliciesDependantAdapterCallback
        )
    }
    private val customerPoliciesPremiumsPaidAdapter by lazy {
        CustomerPoliciesPremiumsPaidAdapter(
            customerPoliciesPremiumsPaidAdapterCallback
        )
    }

    private fun inflateDependantRecyclerView(itemsRV: List<CashPlanSubscriptionsPoliciesDependants>) {
        if (itemsRV.isEmpty()) {
            //binding.noShows.visibility = View.VISIBLE
            binding.rvDependants.visibility = View.GONE
        } else {
            //binding.noShows.visibility = View.GONE
            binding.rvDependants.visibility = View.VISIBLE
            customerPoliciesDependantAdapter.submitList(itemsRV)
            customerPoliciesDependantAdapter?.notifyDataSetChanged()
            binding.rvDependants.apply {
                layoutManager = GridLayoutManager(this.context!!, 2)
                adapter = customerPoliciesDependantAdapter
                setHasFixedSize(true)
            }
        }
    }

    private fun inflatePremiumsPaidDataRecyclerView(itemsRV: List<CashPlanSubscriptionsPoliciesPayments>) {
        if (itemsRV.isEmpty()) {
            //binding.noShows.visibility = View.VISIBLE
            binding.rvPremiumsPaid.visibility = View.GONE
        } else {
            //binding.noShows.visibility = View.GONE
            binding.rvPremiumsPaid.visibility = View.VISIBLE
            customerPoliciesPremiumsPaidAdapter.submitList(itemsRV)
            customerPoliciesPremiumsPaidAdapter?.notifyDataSetChanged()
            binding.rvPremiumsPaid.apply {
                layoutManager = LinearLayoutManager(this.context!!)
                adapter = customerPoliciesPremiumsPaidAdapter
                setHasFixedSize(true)
            }
        }
    }

    private val customerPoliciesDependantAdapterCallback =
        object : CustomerPoliciesDependantAdapterCallback {
            override fun onItemSelected(view: View, item: Dependant) {
                when (view.id) {
                    else -> {

                    }
                }
            }
        }
    private val customerPoliciesPremiumsPaidAdapterCallback =
        object : CustomerPoliciesPremiumsPaidAdapterCallback {
            override fun onItemSelected(view: View, item: CashPlanSubscriptionsPoliciesPayments) {
                when (view.id) {
                    else -> {

                    }
                }
            }
        }

}