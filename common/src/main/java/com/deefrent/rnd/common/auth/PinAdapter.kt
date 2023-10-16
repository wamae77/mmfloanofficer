package com.deefrent.rnd.common.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.common.R
import com.deefrent.rnd.common.databinding.ItemPinInputBinding


class PinAdapter : ListAdapter<Pin, PinAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPinInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        holder.itemPinInputBinding.apply {
            holder.itemPinInputBinding.imageView5.apply {
                when (item.digit) {
                    "*" -> {
                        setImageResource(R.drawable.ic_circle_pin_empty)
                    }
                    else -> {
                        setImageResource(R.drawable.ic_circle_pin_full)

                    }
                }
            }
        }
    }

    class ViewHolder(val itemPinInputBinding: ItemPinInputBinding) :
        RecyclerView.ViewHolder(itemPinInputBinding.root)

}

private val DIFF_UTIL = object : DiffUtil.ItemCallback<Pin>() {

    override fun areItemsTheSame(oldItem: Pin, newItem: Pin): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Pin, newItem: Pin): Boolean {
        return oldItem == newItem
    }
}

data class Pin(
    var digit: String
)