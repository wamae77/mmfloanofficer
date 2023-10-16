package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.IncompleteRegItemListBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import kotlinx.android.synthetic.main.incomplete_reg_item_list.view.*

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class CustomerListFuneralCashPlanAdapter(
    val callback: CustomerListFuneralCashPlanAdapterAdapterCallback
) :
    ListAdapter<FindCustomerData, CustomerListFuneralCashPlanAdapter.ViewHolder>(
        DIFF_UTIL
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /*val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCustomerPoliciesBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)*/
        return ViewHolder(
            IncompleteRegItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItems = getItem(position)
        holder.binding.apply {
            val fname = currentItems.firstName
            val lName = currentItems.lastName
            tType?.text = "$fname $lName"
            tAmount?.text = "ID No. ${currentItems.idNumber}"
            tvChannel?.text = ""

            tvChannel?.text = "Status: ${currentItems.isFullyRegistered.toString()}%"
            tvDate?.text = " Customer No: ${currentItems.customerNumber}"
            clIncompleteReg.clIncompleteReg.setOnClickListener {
                callback.onItemSelected(it, currentItems)
            }
        }
    }

    inner class ViewHolder(val binding: IncompleteRegItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}


private val DIFF_UTIL = object : DiffUtil.ItemCallback<FindCustomerData>() {
    override fun areItemsTheSame(
        oldItem: FindCustomerData,
        newItem: FindCustomerData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FindCustomerData,
        newItem: FindCustomerData
    ): Boolean {
        return oldItem == newItem
    }

}

interface CustomerListFuneralCashPlanAdapterAdapterCallback {
    fun onItemSelected(view: View, item: FindCustomerData)
}