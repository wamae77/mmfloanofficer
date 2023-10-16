package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.utils.callbacks.DashboardCallBack

class AssesementCustomerDetailsAdapter(
    private val dashboardItemList: ArrayList<DashboardItem>,
    private var callback:DashboardCallBack
) :
    RecyclerView.Adapter<AssesementCustomerDetailsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDashboardItem: TextView = view.findViewById(R.id.tvDashboardItem)
        val tvRequestsCount:TextView=view.findViewById(R.id.tvRequestsCount)
        val clItem: ConstraintLayout = view.findViewById(R.id.clItem)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_customer_info_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvDashboardItem.text = dashboardItemList[position].dashboardItemName
        viewHolder.clItem.setOnClickListener { v ->
            callback.onItemSelected(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dashboardItemList.size
}