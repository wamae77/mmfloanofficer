package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.FragmentDashboardItemsListBinding
import com.deefrent.rnd.fieldapp.utils.callbacks.HomeDashboardItemsCallBack
import kotlinx.android.synthetic.main.fragment_dashboard_items_list.view.*

class MainDashboardItemAdapter(
    val items: List<HomeDashboardItems>,
    private var callback: HomeDashboardItemsCallBack
) : RecyclerView.Adapter<MainDashboardItemAdapter.HomeItemViewHolder>() {
    private var binding: FragmentDashboardItemsListBinding? = null


    inner class HomeItemViewHolder(itemBinding: FragmentDashboardItemsListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemViewHolder {
        binding = FragmentDashboardItemsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeItemViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeItemViewHolder, position: Int) {
        val currentItems = items[position]
        holder.itemView.apply {
            binding?.viewLine?.setImageResource(currentItems.image)
            binding?.tvLoanOption?.text = currentItems.name

        }
        holder.itemView.cardLoan.setOnClickListener {
            callback.onItemSelected(it, currentItems)
        }

    }


}