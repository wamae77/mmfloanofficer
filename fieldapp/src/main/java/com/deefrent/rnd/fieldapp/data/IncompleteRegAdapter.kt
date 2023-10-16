package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.IncompleteRegItemListBinding
import com.deefrent.rnd.fieldapp.network.models.CustomerIncompleteData
import com.deefrent.rnd.fieldapp.utils.callbacks.IncompleteRegCallBack
import com.deefrent.rnd.fieldapp.utils.formatDate
import kotlinx.android.synthetic.main.incomplete_reg_item_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class IncompleteRegAdapter (val items:ArrayList<CustomerIncompleteData>,private val callbark: IncompleteRegCallBack): RecyclerView.Adapter<IncompleteRegAdapter.IncompleteViewHolder>() {
    private var binding: IncompleteRegItemListBinding?=null

    inner class IncompleteViewHolder(itemBinding: IncompleteRegItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
        binding= IncompleteRegItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IncompleteViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) {
        val currentItems=items[position]
        holder.itemView.apply {
            binding?.tType?.text=currentItems.fullName
            binding?.tAmount?.text=currentItems.status
            binding?.tvChannel?.text="Via ${currentItems.channel}"
            binding?.tvDate?.text= formatDate(currentItems.dateRecorded)
            holder.itemView.clIncompleteReg.setOnClickListener {
                callbark.onItemSelected(position,currentItems)
            }
            /**clickListener to the notebody and title to navigate to update note screen**/
        }


    }

}