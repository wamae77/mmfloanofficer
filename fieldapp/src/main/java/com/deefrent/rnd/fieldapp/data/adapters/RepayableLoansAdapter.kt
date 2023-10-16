package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.LoanProductItemListBinding
import com.deefrent.rnd.fieldapp.network.models.RepayableLoan
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanPayableCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import kotlinx.android.synthetic.main.loan_product_item_list.view.*
import java.util.*

class RepayableLoansAdapter(private val callBack: LoanPayableCallBack, val context: Context, private val items:List<RepayableLoan>): RecyclerView.Adapter<RepayableLoansAdapter.LoanPayableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanPayableViewHolder {
        return LoanPayableViewHolder(LoanProductItemListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: LoanPayableViewHolder, position: Int)=holder.bind(position)
   inner class LoanPayableViewHolder(private val binding: LoanProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val loanProduct = items[position]
            binding.tvtitle.text=loanProduct.name
            binding.textPhone.text= "Loan account: ${loanProduct.loanAccountNo}"
            binding.initials.text = getInitials(loanProduct.name).uppercase()
            binding.ClLoanProduct.setOnClickListener {
                callBack.onItemSelected(loanProduct)
            }
        }

    }
    override fun getItemCount(): Int {
        return items.size

    }

}
