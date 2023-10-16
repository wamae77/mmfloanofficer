package com.deefrent.rnd.fieldapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Merchant
import com.deefrent.rnd.fieldapp.ui.onboardedAccounts.MerchantAccountsFragment
import com.deefrent.rnd.fieldapp.utils.capitalizeWords

class OnboardedMerchantsListAdapter(private val merchants: ArrayList<Merchant>,
                                    private val context: Context,
                                    private val merchantAccountsFragment: MerchantAccountsFragment
) : RecyclerView.Adapter<OnboardedMerchantsListAdapter.ViewHolder>(){
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
        holder.tvBusinessName.text = merchants[position].businessName.capitalizeWords
        holder.tvNatureBusiness.text =
            "Nature of Business: ${merchants[position].natureOfBusiness.capitalizeWords}"
        holder.tvRegisterDate.text = "Onboarded on: ${merchants[position].registeredDate}"
    }

    override fun getItemCount() = merchants.size
}