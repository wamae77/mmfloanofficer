package com.deefrent.rnd.fieldapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.databinding.ListItemCustomerDocumentBinding
import com.deefrent.rnd.fieldapp.network.models.DocumentData
import com.deefrent.rnd.fieldapp.utils.callbacks.ViewDocumentCallBack
import com.deefrent.rnd.fieldapp.utils.loadImage2
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile.ViewDocumentFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewDocAdapter(
    private var callback: ViewDocumentCallBack,
    viewDocumentFragment: ViewDocumentFragment
) :
    RecyclerView.Adapter<ViewDocAdapter.MyViewHolder>() {
    private var viewDocumentFragment: ViewDocumentFragment
    private var data: ArrayList<DocumentData> = ArrayList()

    init {
        this.viewDocumentFragment = viewDocumentFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ListItemCustomerDocumentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(data[position], viewDocumentFragment)

    fun swapData(items: List<DocumentData>) {
        this.data.clear()
        this.data.addAll(items)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ListItemCustomerDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentData, viewDocumentFragment: ViewDocumentFragment) = with(binding) {
            viewDocumentFragment.lifecycleScope.launch(Dispatchers.IO) {
                Glide.get(viewDocumentFragment.requireContext()).clearDiskCache()
                withContext(Dispatchers.Main) {
                    Glide.get(viewDocumentFragment.requireContext()).clearMemory()
                    ivCustomerDocument.loadImage2(item.url, pbLoadDocument)
                }
            }
            tvCustomerDocumentType.text = item.docTypeName
            ivCustomerDocument.setOnClickListener {
                callback.onItemSelected(position, item, ivCustomerDocument)
            }
            /*tvEdit.setOnClickListener {
                callback.onEditDocument(position, item, tvEdit, imageView)
            }*/
        }
    }
}