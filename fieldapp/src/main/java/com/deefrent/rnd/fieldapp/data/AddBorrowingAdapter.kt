package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.OtherBorrowing
import com.deefrent.rnd.fieldapp.utils.callbacks.BorrowEntityCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AddBorrowingAdapter (val items:ArrayList<OtherBorrowing>,val callBack:BorrowEntityCallBack): RecyclerView.Adapter<AddBorrowingAdapter.AddGuarantorViewHolder>() {

    inner class AddGuarantorViewHolder(val binding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bindViews(pos:Int){
                val currentItems=items[pos]
                binding.tvtitle.text =currentItems.institutionName
                binding.textPhone.text =currentItems.amount
                binding.initials.text = getInitials(currentItems.institutionName).uppercase()
                binding.ivBack.setOnClickListener {
                    callBack.onItemSelected(pos,currentItems)
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGuarantorViewHolder {
       val binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddGuarantorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AddGuarantorViewHolder, position: Int) = holder.bindViews(position)

}