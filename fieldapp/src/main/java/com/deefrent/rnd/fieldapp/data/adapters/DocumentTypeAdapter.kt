package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.network.models.DocumentType
import com.deefrent.rnd.fieldapp.utils.callbacks.DocTypeCallBack

class DocumentTypeAdapter(
    private val itemList: ArrayList<DocumentType>, private var callback:DocTypeCallBack) :
    RecyclerView.Adapter<DocumentTypeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDashboardItem: TextView = view.findViewById(R.id.tvDashboardItem)
        val tvRequestsCount:TextView=view.findViewById(R.id.tvRequestsCount)
        val clItem: ConstraintLayout = view.findViewById(R.id.clItem)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_doc_type_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentItem=itemList[position]
        viewHolder.tvDashboardItem.text = currentItem.name
        viewHolder.clItem.setOnClickListener { v ->
            callback.onItemSelected(position,currentItem)
        }
    }

    override fun getItemCount() = itemList.size
}