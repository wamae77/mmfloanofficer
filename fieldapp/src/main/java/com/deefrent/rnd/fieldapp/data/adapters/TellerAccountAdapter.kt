package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.TellerStatementItemListBinding
import com.deefrent.rnd.fieldapp.network.models.TellerAccountStatmentData
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import java.util.*
import kotlin.collections.ArrayList


class TellerAccountAdapter (val items:ArrayList<TellerAccountStatmentData>, val context: Context): RecyclerView.Adapter<TellerAccountAdapter.TellerViewHolder>() {
    inner class TellerViewHolder(val binding: TellerStatementItemListBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bindViews(position: Int){
                val currentItems=items[position]
                val formatAmount =FormatDigit.formatDigits(currentItems.amount)
                binding.amount.text ="${currentItems.currency} $formatAmount"
                binding.tAmount.text = currentItems.transactionType.capitalizeWords
                binding.tvDate.text = currentItems.transactionDate
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TellerViewHolder {
       val binding= TellerStatementItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TellerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TellerViewHolder, position: Int)=holder.bindViews(position)

}