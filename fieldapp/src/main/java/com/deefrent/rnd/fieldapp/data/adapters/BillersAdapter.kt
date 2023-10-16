package com.deefrent.rnd.fieldapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.databinding.BillersItemListBinding
import com.deefrent.rnd.fieldapp.network.models.Biller
import com.deefrent.rnd.fieldapp.utils.ShimmerPlaceHolder
import com.deefrent.rnd.fieldapp.utils.callbacks.BillersCallback
import com.deefrent.rnd.fieldapp.utils.getInitials2
import com.deefrent.rnd.fieldapp.utils.makeVisible

class BillersAdapter(
    val context: Context,
    val billerList: List<Biller>,
    val callBack: BillersCallback
) : RecyclerView.Adapter<BillersAdapter.BillerViewHolder>() {

    inner class BillerViewHolder(val binding: BillersItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int) {
            val billerItems = billerList[position]
            binding.tvTitleHolder.text = billerItems.name
            binding.initials.makeVisible()
            if (billerItems.logoUrl.isNotEmpty()) {
                Glide.with(context).load(billerItems.logoUrl)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(binding.groupIcon)
            } else {
                binding.initials.text = getInitials2(billerItems.name.uppercase())
            }
            binding.ClMerchants.setOnClickListener {
                callBack.onItemSelected(
                    billerItems,
                    position
                )
            }
            /*if (billerItems.u.isNullOrEmpty()) {

                val splited: List<String> = billerItems.name?.split("\\s".toRegex())
                if( splited.count() == 2){
                    val firstName=splited[0]
                    val name2=(firstName).toUpperCase(Locale.ENGLISH)
                    val lastName=splited[1]
                    name1=(lastName).toUpperCase(Locale.ENGLISH)
                    posone=name1[0].toString().toUpperCase(Locale.ENGLISH)
                    postwo=name2[0].toString().toUpperCase(Locale.ENGLISH)
                    itemView.initials.text=" $postwo $posone"
                }else if (splited.count()===3){
                    val firstName=splited[0]
                    val name2=(firstName).toUpperCase(Locale.ENGLISH)
                    val lastName=splited[1]
                    name1=(lastName).toUpperCase(Locale.ENGLISH)
                    posone=name1[0].toString().toUpperCase(Locale.ENGLISH)
                    postwo=name2[0].toString().toUpperCase(Locale.ENGLISH)
                    itemView.initials.text=" $postwo $posone"
                }else{
                    val names=billerItems.name
                    posone=names[0].toString().toUpperCase(Locale.ENGLISH)
                    postwo=names[0].toString().toUpperCase(Locale.ENGLISH)
                    itemView.initials.text=" $postwo $posone"
                }

            } else {
                bindImage(itemView.iv_zukuIcon,billerItems.logoUrl)
            }*/
            /*itemView.cardBiller.setOnClickListener {
                callBack.onItemSelected(billerItems)
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillerViewHolder {
        val binding = BillersItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return BillerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return billerList.size
    }

    override fun onBindViewHolder(holder: BillerViewHolder, position: Int) =
        holder.bindItems(position)
}
