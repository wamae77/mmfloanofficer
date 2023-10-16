package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemMiniStatementBinding
import com.deefrent.rnd.fieldapp.models.loans.Transaction
import com.deefrent.rnd.fieldapp.utils.capitalizeWords

class LoanMiniStatementListAdapter(val items: List<Transaction>) :
    RecyclerView.Adapter<LoanMiniStatementListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemMiniStatementBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)


    inner class ViewHolder(private val binding: ListItemMiniStatementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val transaction = items[position]
            binding.tvTransactionType.text = transaction.transactionType.capitalizeWords
            binding.tvRefNo.text = "RefNo: ${transaction.refNo}"
            binding.tvLoanBalanceValue.text = "${transaction.currency} ${transaction.balance}"
            binding.tvTransactionDateValue.text = transaction.transactionDate
            binding.tvDebitAmountValue.text = "${transaction.currency} ${transaction.debit}"
            binding.tvCreditAmountValue.text = "${transaction.currency} ${transaction.credit}"
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
