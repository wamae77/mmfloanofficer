package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.LoanPendingApprovalItemListBinding
import com.deefrent.rnd.fieldapp.network.models.LoansPendingApproval
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.PendingLoanCallBack
import kotlinx.android.synthetic.main.loan_product_item_list.view.*
import java.util.*

class LoanPendingApprovalAdapter(val context: Context, private val items:List<LoansPendingApproval>,val callBack:PendingLoanCallBack): RecyclerView.Adapter<LoanPendingApprovalAdapter.LoanPendingApprovalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanPendingApprovalViewHolder {
        return LoanPendingApprovalViewHolder(LoanPendingApprovalItemListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: LoanPendingApprovalViewHolder, position: Int) =holder.bind(position)
   inner class LoanPendingApprovalViewHolder(private val binding: LoanPendingApprovalItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val loanProduct = items[position]
            binding.tvtitle.text=loanProduct.name
            binding.tvloanAccNo.text=  loanProduct.loanAccountNo
            val finalAmount=FormatDigit.formatDigits(loanProduct.amountApplied)
            binding.tvamount.text=  "${loanProduct.currency} $finalAmount"
            binding.tvDate.text=  (loanProduct.applicationDate)
            binding.ivEdit.setOnClickListener {
                callBack.onItemSelected(position,loanProduct)
            }


        }

    }
    override fun getItemCount(): Int {
        return items.size

    }

}
