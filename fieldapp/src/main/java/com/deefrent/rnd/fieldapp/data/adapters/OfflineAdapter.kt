package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.OfflineTransactionRowBinding
import com.deefrent.rnd.fieldapp.room.entities.CusomerDetailsEntityWithList
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerEntityCallBack
import com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction.OfflineRegFragment
import kotlin.collections.ArrayList


class OfflineAdapter(
    val items: ArrayList<CusomerDetailsEntityWithList>,
    private val callbark: CustomerEntityCallBack,
    val offlineRegistrationFragment: OfflineRegFragment
) : RecyclerView.Adapter<OfflineAdapter.IncompleteViewHolder>() {

    inner class IncompleteViewHolder(val itemBinding: OfflineTransactionRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(position: Int) {
            val currentItems = items[position]
            val fname = currentItems.customerDetails.firstName
            val lName = currentItems.customerDetails.lastName
            itemBinding.tType?.text = "$fname $lName"
            itemBinding.tAmount?.text = "Id No. ${currentItems.customerDetails.nationalIdentity}"
            itemBinding.tvChannel?.text = "${currentItems.customerDetails.phone}"
            itemBinding.tvDate?.text = currentItems.customerDetails.genderName
            itemBinding.SyncData.setOnClickListener {
                callbark.onItemSelected(position, currentItems)
            }
            itemBinding.btnDelete.setOnClickListener {
                offlineRegistrationFragment.deleteOfflineTransaction(currentItems,position)
            }
            /**clickListener to the notebody and title to navigate to update note screen**/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteViewHolder {
        val binding =
            OfflineTransactionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncompleteViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IncompleteViewHolder, position: Int) =
        holder.bindViews(position)

}