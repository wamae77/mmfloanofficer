package com.deefrent.rnd.fieldapp.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.LoanProductItemListBinding
import com.deefrent.rnd.fieldapp.network.models.LoanProduct
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanProductCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import kotlinx.android.synthetic.main.loan_product_item_list.view.*
import java.util.*

class LoanProductAdapter(private val callBack: LoanProductCallBack, val context: Context, private val items:List<LoanProduct>): RecyclerView.Adapter<LoanProductAdapter.LoanProductViewHolder>() {

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

        fun bind(loanProduct: LoanProduct) {
            binding.tvtitle.text=loanProduct.name
            val finalamount= FormatDigit.formatDigits(loanProduct.limit)
            binding.textPhone.text= "Loan limit: $finalamount"
            binding?.initials?.text = getInitials(loanProduct.name).uppercase()

        }

    }
    override fun getItemCount(): Int {
        return items.size

    }

}
