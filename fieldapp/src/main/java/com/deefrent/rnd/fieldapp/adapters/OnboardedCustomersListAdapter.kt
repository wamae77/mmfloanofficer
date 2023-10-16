package com.deefrent.rnd.fieldapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Customer
import com.deefrent.rnd.fieldapp.utils.capitalizeWords

class OnboardedCustomersListAdapter(private val customers: ArrayList<Customer>)
    : RecyclerView.Adapter<OnboardedCustomersListAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBusinessName: TextView = view.findViewById(R.id.tvBusinessName)
        val tvNatureBusiness: TextView = view.findViewById(R.id.tvNatureBusiness)
        val tvRegisterDate: TextView = view.findViewById(R.id.tvRegisterDate)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_onboarded_agent, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.tvBusinessName.text = "${customers[position].firstName.capitalizeWords} " +
                "${customers[position].lastName.capitalizeWords}"
        holder.tvNatureBusiness.visibility=View.GONE
        holder.tvRegisterDate.text = "Onboarded on: ${customers[position].createdDate}"
    }

    override fun getItemCount() = customers.size
}