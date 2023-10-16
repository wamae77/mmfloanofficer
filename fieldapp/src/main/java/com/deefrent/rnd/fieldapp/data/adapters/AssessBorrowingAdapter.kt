package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessBorrowing
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessBorrowingCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AssessBorrowingAdapter (val items:ArrayList<AssessBorrowing>, val context: Context, private val callback: AssessBorrowingCallBack): RecyclerView.Adapter<AssessBorrowingAdapter.OtherBorrowViewHolder>() {

    inner class OtherBorrowViewHolder(val itemBinding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
            fun bindViews(position: Int){
                val currentItems=items[position]
                itemBinding.apply {
                  requestList?.setOnClickListener {
                        callback.onItemSelected(position,currentItems)
                    }
                    tvtitle?.text=currentItems.institutionName
                    val value=FormatDigit.formatDigits(currentItems.amount)
                    val  finalAmount=String.format(context.getString(R.string.kesh),value)
                   textPhone?.text= "$finalAmount- ${currentItems.status}"
                   initials?.text = getInitials(currentItems.institutionName).uppercase()
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherBorrowViewHolder {
       val binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OtherBorrowViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OtherBorrowViewHolder, position: Int)=holder.bindViews(position)



}