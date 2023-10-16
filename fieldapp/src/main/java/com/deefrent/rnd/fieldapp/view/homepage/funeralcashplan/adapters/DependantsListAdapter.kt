package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemDependantsBinding
import com.deefrent.rnd.fieldapp.utils.getInitials
import request.Dependant

/**
 * Created by Tom Munyiri on 18/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

class DependantsListAdapter(private val items: List<Dependant>, val callback: DependantCallBack) :
    RecyclerView.Adapter<
            DependantsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemDependantsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(val binding: ListItemDependantsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val dependant = items[position]
            binding.apply {
                tvInitials.text = getInitials(dependant.name)
                tvDependantTitle.text = dependant.name
                tvContributionAmount.text = "Amount: USD ${dependant.contributionAmount}"
                ivClear.setOnClickListener {
                    callback.onItemSelected(dependant, position)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

}

interface DependantCallBack {
    fun onItemSelected(item: Dependant, pos: Int)
}