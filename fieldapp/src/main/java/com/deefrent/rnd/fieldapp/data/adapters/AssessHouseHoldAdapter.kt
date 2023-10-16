package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessHouseholdMemberEntity
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessMemberCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AssessHouseHoldAdapter (val items:ArrayList<AssessHouseholdMemberEntity>, private val callBack:AssessMemberCallBack): RecyclerView.Adapter<AssessHouseHoldAdapter.HouseholdViewHolder>() {
    inner class HouseholdViewHolder(val itemBinding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
            fun bindViews(position: Int){
                val currentItems=items[position]
                itemBinding.apply {
                   ivBack?.setOnClickListener { callBack.onItemSelected(position,currentItems) }
                   tvtitle?.text= "${ currentItems.fullName } - ${currentItems.relationship}"
                   textPhone?.text=currentItems.occupation
                   initials?.text = getInitials(currentItems.fullName).uppercase()
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseholdViewHolder {
       val binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HouseholdViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HouseholdViewHolder, position: Int) =holder.bindViews(position)

}