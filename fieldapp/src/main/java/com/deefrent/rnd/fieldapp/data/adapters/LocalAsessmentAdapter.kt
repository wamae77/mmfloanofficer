package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.databinding.LocalIncompletesRowBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntityWithList
import com.deefrent.rnd.fieldapp.utils.callbacks.*
import kotlin.collections.ArrayList


class LocalAsessmentAdapter (val items:ArrayList<AssessCustomerEntityWithList>, private val callback: AssessmentEntityCallBack): RecyclerView.Adapter<LocalAsessmentAdapter.IncompleteViewHolder>() {
    inner class IncompleteViewHolder(val itemBinding: LocalIncompletesRowBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
            fun bindItems(position: Int){
                val currentItems=items[position]
                itemBinding.apply {
                    val fname=currentItems.assessCustomerEntity.firstName
                    val lName=currentItems.assessCustomerEntity.lastName
                    tType?.text="$fname $lName"
                    tAmount?.text="Id No. ${currentItems.assessCustomerEntity.idNumber}"
                    tvChannel?.text="Status: ${currentItems.assessCustomerEntity.assessmentPercentage}%"
                    tvDate?.text=" Customer No: ${currentItems.assessCustomerEntity.customerNumber}"
                    delete.setOnClickListener {
                        Constants.isDelete=true
                        callback.onItemSelected(position,currentItems)
                    }
                    resume.setOnClickListener {
                        Constants.isDelete=false
                        callback.onItemSelected(position,currentItems)
                    }
                }

            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
       val binding= LocalIncompletesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IncompleteViewHolder(binding!!)
    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) =holder.bindItems(position)

}