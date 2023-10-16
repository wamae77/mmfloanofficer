package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.IncompleteRegItemListBinding
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanLookUpDataCallBack
import kotlinx.android.synthetic.main.incomplete_reg_item_list.view.*

class CustomerListLoanAdapter(
    val items: ArrayList<LoanLookupData>,
    private val callback: LoanLookUpDataCallBack
) : RecyclerView.Adapter<CustomerListLoanAdapter.IncompleteViewHolder>() {
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