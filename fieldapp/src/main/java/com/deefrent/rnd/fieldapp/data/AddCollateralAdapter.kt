package com.deefrent.rnd.fieldapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorItemListBinding
import com.deefrent.rnd.fieldapp.room.entities.Collateral
import com.deefrent.rnd.fieldapp.utils.callbacks.ColCallBack
import com.deefrent.rnd.fieldapp.utils.getInitials
import java.util.*
import kotlin.collections.ArrayList


class AddCollateralAdapter (val items:ArrayList<Collateral>,private val callBack:ColCallBack): RecyclerView.Adapter<AddCollateralAdapter.AddGuarantorViewHolder>() {
    inner class AddGuarantorViewHolder(private val itemBinding: AddGuarantorItemListBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(pos: Int) {
            val currentItems = items[pos]
            with(itemBinding) {
                tvtitle.text = currentItems.name
                textPhone.text = currentItems.estimateValue
                initials.text = getInitials(currentItems.name).uppercase()
                ivBack.setOnClickListener {
                    callBack.onItemSelected(pos, currentItems)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGuarantorViewHolder {
      val  binding= AddGuarantorItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddGuarantorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: AddGuarantorViewHolder, position: Int)=holder.bindViews(position)


    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

}
