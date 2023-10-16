package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.LoanProductItemListBinding
import com.deefrent.rnd.fieldapp.network.models.DisbursableLoan
import com.deefrent.rnd.fieldapp.utils.callbacks.PendingDisburseCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import kotlinx.android.synthetic.main.loan_product_item_list.view.*
class DisburseLoanProductAdapter(private val callBack: PendingDisburseCallBack, val context: Context, private val items:List<DisbursableLoan>): RecyclerView.Adapter<DisburseLoanProductAdapter.LoanProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanProductViewHolder {
        return LoanProductViewHolder(LoanProductItemListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: LoanProductViewHolder, position: Int) {

        val loanItems = items[position]
        holder.bind(loanItems)
        holder.itemView.Cl_loan_product.setOnClickListener {
            callBack.onItemSelected(loanItems)
        }
    }
   inner class LoanProductViewHolder(private val binding: LoanProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loanProduct: DisbursableLoan) {
            binding.tvtitle.text=loanProduct.name
            binding.textPhone.text= "Loan account: ${loanProduct.loanAccountNo}"
            binding?.initials?.text = getInitials(loanProduct.name).uppercase()

        }

    }
    override fun getItemCount(): Int {
        return items.size

    }

}
