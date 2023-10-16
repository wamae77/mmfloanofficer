package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemCustomerPoliciesBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesData

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class CustomerPoliciesMainAdapter(
    val callback: CustomerPoliciesMainAdapterCallback
) :
    ListAdapter<CashPlanSubscriptionsPoliciesData, CustomerPoliciesMainAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCustomerPoliciesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packages = getItem(position)
        holder.binding.apply {
            tvPackageName.text = packages.name.toString()
            tvDependants.text = "${packages.dependants?.size} dependants"// packages.name.toString()
            tvDueOn.text = "Due on: ${packages.lastPaymentDate.toString()}"
            tvPackageStatus.text = if (packages.isActive == 1) "InActive" else "Active"
            tvViewMore.setOnClickListener {
                callback.onItemSelected(it, packages)
            }
        }
    }

    inner class ViewHolder(val binding: ListItemCustomerPoliciesBinding) :
        RecyclerView.ViewHolder(binding.root)

}

private val DIFF_UTIL = object : DiffUtil.ItemCallback<CashPlanSubscriptionsPoliciesData>() {
    override fun areItemsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesData,
        newItem: CashPlanSubscriptionsPoliciesData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesData,
        newItem: CashPlanSubscriptionsPoliciesData
    ): Boolean {
        return oldItem == newItem
    }

}

interface CustomerPoliciesMainAdapterCallback {
    fun onItemSelected(view: View, item: CashPlanSubscriptionsPoliciesData)
}