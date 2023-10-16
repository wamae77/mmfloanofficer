package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.databinding.LocalIncompletesRowBinding
import com.deefrent.rnd.fieldapp.room.entities.CusomerDetailsEntityWithList
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerEntityCallBack
import kotlinx.android.synthetic.main.local_incompletes_row.view.*
import kotlin.collections.ArrayList


class IncompleteLocalRegAdapter (val items:ArrayList<CusomerDetailsEntityWithList>, private val callbark: CustomerEntityCallBack): RecyclerView.Adapter<IncompleteLocalRegAdapter.IncompleteViewHolder>() {
    private var binding: LocalIncompletesRowBinding?=null

    inner class IncompleteViewHolder(itemBinding: LocalIncompletesRowBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
        binding= LocalIncompletesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IncompleteViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) {
        val currentItems=items[position]
        holder.itemView.apply {
            val fname=currentItems.customerDetails.firstName
            val lName=currentItems.customerDetails.lastName
            binding?.tType?.text="$fname $lName"
            binding?.tAmount?.text="Id No. ${currentItems.customerDetails.nationalIdentity}"
            binding?.tvChannel?.text="${currentItems.customerDetails.phone}"
           binding?.tvDate?.text= currentItems.customerDetails.genderName
            holder.itemView.delete.setOnClickListener {
                Constants.isDelete=true
                callbark.onItemSelected(position,currentItems)
            }
            holder.itemView.resume.setOnClickListener {
                Constants.isDelete=false
                callbark.onItemSelected(position,currentItems)
            }
            /**clickListener to the notebody and title to navigate to update note screen**/
        }


    }

}