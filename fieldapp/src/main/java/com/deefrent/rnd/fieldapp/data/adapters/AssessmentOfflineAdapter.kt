package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.OfflineTransactionRowBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntityWithList
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessmentEntityCallBack
import com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction.OfflineAssessmentFragment
import kotlin.collections.ArrayList


class AssessmentOfflineAdapter(
    val items: ArrayList<AssessCustomerEntityWithList>,
    private val callbark: AssessmentEntityCallBack,
    val offlineAssessmentFragment: OfflineAssessmentFragment
) : RecyclerView.Adapter<AssessmentOfflineAdapter.IncompleteViewHolder>() {

    inner class IncompleteViewHolder(val itemBinding: OfflineTransactionRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(position: Int) {
            val currentItems = items[position]
            val fname = currentItems.assessCustomerEntity.firstName
            val lName = currentItems.assessCustomerEntity.lastName
            itemBinding.tType?.text = "$fname $lName"
            itemBinding.tAmount?.text = "Id No. ${currentItems.assessCustomerEntity.idNumber}"
            itemBinding.tvChannel?.text = "${currentItems.assessCustomerEntity.phone}"
            itemBinding.tvDate?.text = currentItems.assessCustomerEntity.gender
            itemBinding.SyncData.setOnClickListener {
                callbark.onItemSelected(position, currentItems)
            }
            itemBinding.btnDelete.setOnClickListener {
                offlineAssessmentFragment.deleteOfflineTransaction(currentItems,position)
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