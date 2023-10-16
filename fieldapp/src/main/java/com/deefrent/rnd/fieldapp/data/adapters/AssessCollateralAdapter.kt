package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.AssessCollateral
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessColCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AssessCollateralAdapter (val items:ArrayList<AssessCollateral>, val context: Context, private val callback: AssessColCallBack): RecyclerView.Adapter<AssessCollateralAdapter.CollateralViewHolder>() {
    inner class CollateralViewHolder(val binding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bindViews(position: Int){
                val currentItems=items[position]
                binding.requestList.setOnClickListener {
                    callback.onItemSelected(position,currentItems)
                }
                binding.tvtitle.text ="${currentItems.assetType} - ${currentItems.name}"
                val value=FormatDigit.formatDigits(currentItems.estimateValue.toString().trim())
                binding.textPhone.text = String.format(context.getString(R.string.kesh),value)
                binding.initials.text = getInitials(currentItems.name).uppercase()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollateralViewHolder {
       val binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CollateralViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CollateralViewHolder, position: Int)=holder.bindViews(position)

}