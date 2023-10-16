package com.deefrent.rnd.fieldapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.RepaymentScheduleItemListBinding
import com.deefrent.rnd.fieldapp.models.loans.RepaymentSchedule

class RepaymentScheduleListAdapter(
    val items: ArrayList<RepaymentSchedule>
) : RecyclerView.Adapter<RepaymentScheduleListAdapter.IncompleteViewHolder>() {
    private lateinit var binding: RepaymentScheduleItemListBinding

    inner class IncompleteViewHolder(itemBinding: RepaymentScheduleItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindView(position: Int) {
            val currentItems = items[position]
            binding.tvScheduleNumber.text = "Schedule No. ${currentItems.scheduleNo}"
            Log.d("TAG", "onBindViewHolder: ${currentItems.scheduleNo}")
            binding.tvDueDate.text = "Due Date: ${currentItems.dateDue}"
            binding.tvTotalInstallments.text =
                "Total Installments: ${currentItems.totalInstallment}"
            binding.tvTotalBalance.text = "Total Balance: ${currentItems.totalBalance}"
            binding.tvTotalRepaid.text = "Total Repaid: ${currentItems.totalRepaid}"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
        binding =
            RepaymentScheduleItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return IncompleteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) =
        holder.bindView(position)
}