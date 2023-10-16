package com.deefrent.rnd.fieldapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.network.models.OtherBorrowing
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.BorrowingCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class UpdateBorrowingAdapter(
    val items: ArrayList<OtherBorrowing>,
    val context: Context,
    private val callback: BorrowingCallBack
) : RecyclerView.Adapter<UpdateBorrowingAdapter.OtherBorrowViewHolder>() {
    private var binding: AddGuarantorItemListBinding? = null

    inner class OtherBorrowViewHolder(itemBinding: AddGuarantorItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherBorrowViewHolder {
        binding =
            AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherBorrowViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OtherBorrowViewHolder, position: Int) {
        val currentItems = items[position]
        holder.itemView.apply {
            binding?.requestList?.setOnClickListener {
                callback.onItemSelected(position, currentItems)
            }
            binding?.tvtitle?.text = currentItems.institutionName
            val value = FormatDigit.formatDigits(currentItems.amount)
            val finalAmount = String.format(context.getString(R.string.kesh), value)
            binding?.textPhone?.text = "$finalAmount- ${currentItems.status}"
            binding?.initials?.text = getInitials(currentItems.institutionName).uppercase()

            /**clickListener to the notebody and title to navigate to update note screen**/
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}