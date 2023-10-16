package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemDependantsBinding
import com.deefrent.rnd.fieldapp.utils.getInitials
import request.Dependant

/**
 * Created by Tom Munyiri on 18/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

class AddEditDependantsListAdapter(val callback: AddEditDependantsListAdapterCallback) :
    ListAdapter<Dependant, AddEditDependantsListAdapter.ViewHolder>(
        DIFF_UTIL
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemDependantsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(val binding: ListItemDependantsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val dependant = getItem(position)
            binding.apply {
                tvInitials.text = getInitials(dependant.name)
                tvDependantTitle.text = dependant.name
                tvContributionAmount.text = "Amount: USD ${dependant.contributionAmount}"
                ivClear.setOnClickListener {
                    callback.onItemSelected(it, dependant)
                }
            }
        }

    }

}

private val DIFF_UTIL = object : DiffUtil.ItemCallback<Dependant>() {
    override fun areItemsTheSame(
        oldItem: Dependant,
        newItem: Dependant
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Dependant,
        newItem: Dependant
    ): Boolean {
        return oldItem == newItem
    }

}

interface AddEditDependantsListAdapterCallback {
    fun onItemSelected(view: View, item: Dependant)
}