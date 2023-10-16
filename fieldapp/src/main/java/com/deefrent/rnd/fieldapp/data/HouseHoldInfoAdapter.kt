package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.network.models.HouseholdMember
import com.deefrent.rnd.fieldapp.utils.callbacks.HouseholdMembersCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class HouseHoldInfoAdapter (val items:ArrayList<HouseholdMember>,private val callBack:HouseholdMembersCallBack): RecyclerView.Adapter<HouseHoldInfoAdapter.HouseholdViewHolder>() {
    private var binding: AddGuarantorItemListBinding?=null


    inner class HouseholdViewHolder(itemBinding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseholdViewHolder {
        binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HouseholdViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HouseholdViewHolder, position: Int) {
        val currentItems=items[position]
        holder.itemView.apply {
            binding?.ivBack?.setOnClickListener { callBack.onItemSelected(position,currentItems) }
            binding?.tvtitle?.text= "${ currentItems.fullName } - ${currentItems.relationShip}"
                binding?.textPhone?.text=currentItems.occupation
            binding?.initials?.text = getInitials(currentItems.fullName).uppercase()

            /**clickListener to the notebody and title to navigate to update note screen**/
        }


    }

}