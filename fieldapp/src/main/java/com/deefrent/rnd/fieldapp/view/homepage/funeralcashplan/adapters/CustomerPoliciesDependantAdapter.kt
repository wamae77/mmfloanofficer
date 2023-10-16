package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemCustomerPoliciesDependantsBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesDependants
import request.Dependant

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class CustomerPoliciesDependantAdapter(
    val callback: CustomerPoliciesDependantAdapterCallback
) :
    ListAdapter<CashPlanSubscriptionsPoliciesDependants, CustomerPoliciesDependantAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /*val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCustomerPoliciesBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)*/
        return ViewHolder(
            ListItemCustomerPoliciesDependantsBinding.inflate(
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
            tvDependantTitle.text = packages.name.toString()
            tvContributionAmount.text = ""//packages..toString()
        }
    }

    inner class ViewHolder(val binding: ListItemCustomerPoliciesDependantsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}


private val DIFF_UTIL = object : DiffUtil.ItemCallback<CashPlanSubscriptionsPoliciesDependants>() {
    override fun areItemsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesDependants,
        newItem: CashPlanSubscriptionsPoliciesDependants
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesDependants,
        newItem: CashPlanSubscriptionsPoliciesDependants
    ): Boolean {
        return oldItem == newItem
    }

}

interface CustomerPoliciesDependantAdapterCallback {
    fun onItemSelected(view: View, item: Dependant)
}