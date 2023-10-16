package com.deefrent.rnd.fieldapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.ui.existingAccount.MerchantInfoFragment

class MerchantInfoListAdapter(
    private val dashboardItemList: ArrayList<DashboardItem>,
    private val context: Context,
    private val merchantInfoFragment: MerchantInfoFragment
) : RecyclerView.Adapter<MerchantInfoListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDashboardItem: TextView = view.findViewById(R.id.tvDashboardItem)
        val clItem: ConstraintLayout = view.findViewById(R.id.clItem)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MerchantInfoListAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_dashboard, viewGroup, false)

        return MerchantInfoListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MerchantInfoListAdapter.ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tvDashboardItem.text = dashboardItemList[position].dashboardItemName
        viewHolder.clItem.setOnClickListener { v ->
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dashboardItemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}