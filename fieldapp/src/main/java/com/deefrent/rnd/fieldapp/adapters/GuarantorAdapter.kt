package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.network.models.GuarantorInfo
import com.deefrent.rnd.fieldapp.utils.callbacks.GuarantorCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class GuarantorAdapter(
    val items: ArrayList<GuarantorInfo>,
    private val callback: GuarantorCallBack
) : RecyclerView.Adapter<GuarantorAdapter.GuarantorViewHolder>() {
    private var binding: AddGuarantorItemListBinding? = null


    inner class GuarantorViewHolder(itemBinding: AddGuarantorItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuarantorViewHolder {
        binding =
            AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuarantorViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GuarantorViewHolder, position: Int) {
        val currentItems = items[position]
        holder.itemView.apply {
            binding?.requestList?.setOnClickListener {
                callback.onItemSelected(position, currentItems)
            }
            binding?.tvtitle?.text = currentItems.name
            binding?.textPhone?.text = currentItems.phone
            binding?.initials?.text = getInitials(currentItems.name).uppercase()

            /**clickListener to the notebody and title to navigate to update note screen**/
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}