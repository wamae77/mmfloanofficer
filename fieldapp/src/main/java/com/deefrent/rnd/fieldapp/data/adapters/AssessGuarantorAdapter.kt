package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessGuarantor
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessGuarantorCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AssessGuarantorAdapter (val items:ArrayList<AssessGuarantor>, private val callback: AssessGuarantorCallBack): RecyclerView.Adapter<AssessGuarantorAdapter.GuarantorViewHolder>() {

    inner class GuarantorViewHolder(val itemBinding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
            fun bindView(position: Int){
                val currentItems=items[position]
               itemBinding.apply {
                 requestList?.setOnClickListener {
                       callback.onItemSelected(position,currentItems)
                   }
                  tvtitle?.text=currentItems.name
                   textPhone?.text=currentItems.phone
                   initials?.text = getInitials(currentItems.name).uppercase()
               }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuarantorViewHolder {
       val binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GuarantorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GuarantorViewHolder, position: Int)=holder.bindView(position)

}