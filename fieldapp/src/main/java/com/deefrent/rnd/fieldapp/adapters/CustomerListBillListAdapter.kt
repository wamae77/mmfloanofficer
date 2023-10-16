package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.IncompleteRegItemListBinding
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerInfoCallback
import kotlinx.android.synthetic.main.incomplete_reg_item_list.view.*

class CustomerListBillListAdapter(
    val items: ArrayList<CustomerInfo>,
    private val callback: CustomerInfoCallback
) : RecyclerView.Adapter<CustomerListBillListAdapter.IncompleteViewHolder>() {
    private var binding: IncompleteRegItemListBinding? = null

    inner class IncompleteViewHolder(itemBinding: IncompleteRegItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
        binding =
            IncompleteRegItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncompleteViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) {
        val currentItems = items[position]
        holder.itemView.apply {
            val fname = currentItems.firstName
            val lName = currentItems.lastName
            binding?.tType?.text = "$fname $lName"
            binding?.tAmount?.text = "ID No. ${currentItems.idNumber}"
            binding?.tvChannel?.text = ""
            //binding?.tvChannel?.text="Status: ${currentItems.assessmentPercentage}%"
            binding?.tvDate?.text = " Customer No: ${currentItems.customerNumber}"
            holder.itemView.clIncompleteReg.setOnClickListener {
                callback.onItemSelected(position, currentItems)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}