package com.deefrent.rnd.fieldapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.network.models.CollateralInfo
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.CollateralCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class UpdateCollateralAdapter(
    val items: ArrayList<CollateralInfo>,
    val context: Context,
    private val callback: CollateralCallBack
) : RecyclerView.Adapter<UpdateCollateralAdapter.CollateralViewHolder>() {
    private var binding: AddGuarantorItemListBinding? = null

    inner class CollateralViewHolder(itemBinding: AddGuarantorItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollateralViewHolder {
        binding =
            AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollateralViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CollateralViewHolder, position: Int) {
        val currentItems = items[position]
        holder.itemView.apply {
            binding?.requestList?.setOnClickListener {
                callback.onItemSelected(position, currentItems)
            }
            binding?.tvtitle?.text = "${currentItems.assetTypeName}- ${currentItems.name}"
            val value = FormatDigit.formatDigits(currentItems.estimatedValue.toString().trim())
            binding?.textPhone?.text = String.format(context.getString(R.string.kesh), value)
            binding?.initials?.text = getInitials(currentItems.name).uppercase()

            /**clickListener to the notebody and title to navigate to update note screen**/
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}