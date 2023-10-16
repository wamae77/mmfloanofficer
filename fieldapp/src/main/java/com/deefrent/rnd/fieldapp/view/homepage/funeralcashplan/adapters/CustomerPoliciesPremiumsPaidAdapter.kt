package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemCustomerPoliciesPremiumsPaidBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.CashPlanSubscriptionsPoliciesPayments

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class CustomerPoliciesPremiumsPaidAdapter(
    val callback: CustomerPoliciesPremiumsPaidAdapterCallback
) :
    ListAdapter<CashPlanSubscriptionsPoliciesPayments, CustomerPoliciesPremiumsPaidAdapter.ViewHolder>(
        DIFF_UTIL
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCustomerPoliciesPremiumsPaidBinding.inflate(
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
            tvDependantTitle.text = packages.paidBy.toString()
            tvAmount.text = packages.amount.toString()
        }
    }

    inner class ViewHolder(val binding: ListItemCustomerPoliciesPremiumsPaidBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}


private val DIFF_UTIL = object : DiffUtil.ItemCallback<CashPlanSubscriptionsPoliciesPayments>() {
    override fun areItemsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesPayments,
        newItem: CashPlanSubscriptionsPoliciesPayments
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CashPlanSubscriptionsPoliciesPayments,
        newItem: CashPlanSubscriptionsPoliciesPayments
    ): Boolean {
        return oldItem == newItem
    }

}

interface CustomerPoliciesPremiumsPaidAdapterCallback {
    fun onItemSelected(view: View, item: CashPlanSubscriptionsPoliciesPayments)
}