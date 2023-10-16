package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListMileageBinding
import com.deefrent.rnd.fieldapp.models.mileage.Mileage
import com.deefrent.rnd.fieldapp.utils.callbacks.MileageCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import kotlin.collections.ArrayList

class MileageAdapter(val items: ArrayList<Mileage>, val callBack: MileageCallBack) :
    RecyclerView.Adapter<MileageAdapter.MileageViewHolder>() {
    inner class MileageViewHolder(val binding: ListMileageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(pos: Int) {
            val currentItems = items[pos]
            binding.tvMileageFromTo.text = "${currentItems.from} - ${currentItems.to}"
            binding.tvDate.text = currentItems.travel_date
            binding.initials.text = getInitials("${currentItems.from} ${currentItems.to}").uppercase()
            binding.ivBack.setOnClickListener {
                callBack.onItemSelected(pos, currentItems)
            }
            binding.clMileage.setOnClickListener {
                callBack.onItemSelected(pos, currentItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MileageViewHolder {
        val binding = ListMileageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MileageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MileageViewHolder, position: Int) =
        holder.bindView(position)

    override fun getItemViewType(position: Int): Int {
        return position
    }

}