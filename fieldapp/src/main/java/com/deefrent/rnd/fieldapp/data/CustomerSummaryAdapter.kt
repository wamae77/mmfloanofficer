package com.deefrent.rnd.fieldapp.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.deefrent.rnd.fieldapp.databinding.DetailItemsLayoutRowBinding
import com.deefrent.rnd.fieldapp.databinding.OnboardCustomerSummaryRowBinding


class CustomerSummaryAdapter(var context: Context,private val items:ArrayList<CustomData>):PagerAdapter() {
    private var onItemClickListener:((action:CustomAction)->Unit)?=null
    fun setOnItemClickListener(clickListener:((action:CustomAction)->Unit)){
        onItemClickListener=clickListener
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("TAG", "instantiateItem: ")
        //pick single item within the list
        var binding =OnboardCustomerSummaryRowBinding.inflate(LayoutInflater.from(container.context),container,false)
        binding.linearDetails.removeAllViews()
        val item=items[position]
        binding.tvTitle.text=item.title
        item.items.forEach {
            var viewBinding=DetailItemsLayoutRowBinding.inflate(LayoutInflater.from(container.context),binding.linearDetails,false)
            viewBinding.tvLabel.text=it.first
            viewBinding.tvValue.text=it.second
            binding.linearDetails.addView(viewBinding.root)
        }
        binding.tvEdit.setOnClickListener {
            onItemClickListener?.invoke(item.action)
        }
        container.addView(binding.root)
        return binding.root
    }
    override fun getCount(): Int {
        return items.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }




}