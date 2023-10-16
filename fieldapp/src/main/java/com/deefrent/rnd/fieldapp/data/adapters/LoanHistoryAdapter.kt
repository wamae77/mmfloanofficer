package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.LoanHistoryItemListBinding
import com.deefrent.rnd.fieldapp.network.models.LoanHistory
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.getInitials
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanHistoryFragment
import java.util.*
import kotlin.collections.ArrayList


class LoanHistoryAdapter (val items:ArrayList<LoanHistory>, private val loanHistoryFragment: LoanHistoryFragment): RecyclerView.Adapter<LoanHistoryAdapter.LoanHistoryViewHolder>() {
    private var binding: LoanHistoryItemListBinding?=null


    inner class LoanHistoryViewHolder(itemBinding: LoanHistoryItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanHistoryViewHolder {
        binding= LoanHistoryItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LoanHistoryViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LoanHistoryViewHolder, position: Int) {
        val currentItems=items[position]
        holder.itemView.apply {
          //  binding?.ivBack?.setOnClickListener { callBack.onItemSelected(position,currentItems) }
            binding?.tvtitle?.text=currentItems.name
                binding?.tvStatus?.text=currentItems.status
            binding?.tvAmountDis?.text=FormatDigit.formatDigits(currentItems.amountDisbursed)
           // if (currentItems.disbursementDate.isNotEmpty()){
                binding?.tvDate?.text= currentItems.disbursementDate
          //  }
            binding?.initials?.text = getInitials(currentItems.name).uppercase()
            binding?.ClLoanProduct?.setOnClickListener {
                loanHistoryFragment.navigateToLoanHistoryDetailsFragment(currentItems)
            }
            /**clickListener to the notebody and title to navigate to update note screen**/
        }
    }

}