package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListDiaryBinding
import com.deefrent.rnd.fieldapp.network.models.DiaryList
import com.deefrent.rnd.fieldapp.utils.callbacks.DiaryCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import kotlin.collections.ArrayList


class DiaryAdapter(val items: ArrayList<DiaryList>, val callBack: DiaryCallBack) :
    RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
    inner class DiaryViewHolder(val binding: ListDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(pos: Int) {
            val currentItems = items[pos]
            binding.tvMileageFromTo.text = currentItems.eventTypeName
            binding.tvDate.text = currentItems.eventDate
            binding.tvVenue.text = currentItems.venue
            binding.initials.text = getInitials(currentItems.eventTypeName).uppercase()
            binding.ivBack.setOnClickListener {
                callBack.onItemSelected(pos, currentItems)
            }
            binding.clMileage.setOnClickListener {
                callBack.onItemSelected(pos, currentItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = ListDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) =
        holder.bindView(position)

    override fun getItemViewType(position: Int): Int {
        return position
    }

}