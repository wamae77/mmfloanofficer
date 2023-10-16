package com.deefrent.rnd.fieldapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.incompleteOnBoarding.IncompleteProcessListItem
import com.deefrent.rnd.fieldapp.ui.incompleteOnBoarding.IncompleteOnBoardingFragment

class IncompleteListItemAdapter(
    private val incompleteItemList: ArrayList<IncompleteProcessListItem>,
    private val context: Context,
    private val incompleteOnBoardingFragment: IncompleteOnBoardingFragment
) : RecyclerView.Adapter<IncompleteListItemAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserType: TextView = view.findViewById(R.id.tvUserType)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnResume: Button = view.findViewById(R.id.btnResume)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_incomplete_onboarding, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tvUserType.text = "${incompleteItemList[position].userType} Account"
        viewHolder.tvDate.text = incompleteItemList[position].date
        viewHolder.btnResume.setOnClickListener {
            when (incompleteItemList[position].userType) {
                "Individual" -> {
                    incompleteOnBoardingFragment.fetchCustomerDetails(incompleteItemList[position].roomDBId)
                }
                "Merchant" -> {
                    incompleteOnBoardingFragment.fetchMerchantAgentDetails(incompleteItemList[position].roomDBId)
                }
                "Agent" -> {
                    incompleteOnBoardingFragment.fetchMerchantAgentDetails(incompleteItemList[position].roomDBId)
                }
            }
        }
        viewHolder.btnDelete.setOnClickListener {
            when (incompleteItemList[position].userType) {
                "Individual" -> {
                    incompleteOnBoardingFragment.deleteIncompleteCustomer(
                        incompleteItemList[position].roomDBId,
                        position
                    )
                }
                "Merchant" -> {
                    incompleteOnBoardingFragment.deleteIncompleteMerchantAgent(
                        incompleteItemList[position].roomDBId,
                        position
                    )
                }
                "Agent" -> {
                    incompleteOnBoardingFragment.deleteIncompleteMerchantAgent(
                        incompleteItemList[position].roomDBId,
                        position
                    )
                }
            }
        }
    }

    override fun getItemCount() = incompleteItemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}