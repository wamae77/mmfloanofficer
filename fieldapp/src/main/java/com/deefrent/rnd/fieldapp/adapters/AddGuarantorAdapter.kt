package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.Guarantor
import com.deefrent.rnd.fieldapp.utils.callbacks.GuaEntityCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AddGuarantorAdapter(val items: ArrayList<Guarantor>, val callBack: GuaEntityCallBack) :
    RecyclerView.Adapter<AddGuarantorAdapter.AddGuarantorViewHolder>() {
    inner class AddGuarantorViewHolder(val binding: AddGuarantorItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(pos: Int) {
            val currentItems = items[pos]
            binding.tvtitle.text = currentItems.name
            binding.textPhone.text = currentItems.phone
            binding.initials.text = getInitials(currentItems.name).uppercase()
            binding.ivBack.setOnClickListener {
                callBack.onItemSelected(pos, currentItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGuarantorViewHolder {
        val binding =
            AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddGuarantorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AddGuarantorViewHolder, position: Int) =
        holder.bindView(position)

}