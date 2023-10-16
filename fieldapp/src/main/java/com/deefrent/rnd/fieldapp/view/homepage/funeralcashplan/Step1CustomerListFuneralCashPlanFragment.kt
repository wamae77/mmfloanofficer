package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanCustomerListStep1Binding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerListFuneralCashPlanAdapter
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.CustomerListFuneralCashPlanAdapterAdapterCallback
import com.google.gson.Gson
import javax.inject.Inject

class Step1CustomerListFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanCustomerListStep1Binding>(
        FragmentFuneralCashPlanCustomerListStep1Binding::inflate
    ) {
    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

    private val sharedViewModel: SharedFuneralCashPlanViewModel by activityViewModels()

    private val gson = Gson()

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
                sharedViewModel.listFindCustomerByNameData.clear()
            }
        )

        /* binding.btnSearch.setOnClickListener {
             findNavController().navigate(R.id.action_step1IdLookUpFuneralCashPlanFragment_to_step2DetailsFuneralCashPlanFragment)
         }
        */

        inflateRecyclerView(sharedViewModel.listFindCustomerByNameData)
    }


    private val customerListFuneralCashPlanAdapter by lazy {
        CustomerListFuneralCashPlanAdapter(
            customerListFuneralCashPlanAdapterAdapterCallback
        )
    }

    private fun inflateRecyclerView(itemsRV: List<FindCustomerData>) {
        if (itemsRV.isEmpty()) {
            //binding.noShows.visibility = View.VISIBLE
            binding.rvAssessment.visibility = View.GONE
        } else {
            //binding.noShows.visibility = View.GONE
            binding.rvAssessment.visibility = View.VISIBLE
            customerListFuneralCashPlanAdapter.submitList(itemsRV)
            customerListFuneralCashPlanAdapter?.notifyDataSetChanged()
            binding.rvAssessment.apply {
                layoutManager = LinearLayoutManager(this.context!!)
                adapter = customerListFuneralCashPlanAdapter
                setHasFixedSize(true)
            }
        }
    }

    private val customerListFuneralCashPlanAdapterAdapterCallback =
        object : CustomerListFuneralCashPlanAdapterAdapterCallback {
            override fun onItemSelected(view: View, item: FindCustomerData) {
                when (view.id) {
                    R.id.clIncompleteReg -> {
                        val jsonString = gson.toJson(item)
                        commonSharedPreferences.saveStringData(
                            CommonSharedPreferences.CUSTOMER_INFO,
                            jsonString
                        )
                        findNavController().navigate(R.id.action_step1CustomerListFuneralCashPlanFragment_to_step2DetailsFuneralCashPlanFragment)
                    }
                    else -> {

                    }
                }
            }
        }
}