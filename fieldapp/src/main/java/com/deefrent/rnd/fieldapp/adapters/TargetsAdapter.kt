package com.deefrent.rnd.fieldapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.targets.Target
import com.deefrent.rnd.fieldapp.utils.FormatDigit

class TargetsAdapter(private val salesTargetItemList: ArrayList<Target>) :
    RecyclerView.Adapter<TargetsAdapter.VHolder>() {

    class VHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvLabel: TextView = v.findViewById(R.id.tvLabel)
        val tvTotalTargets: TextView = v.findViewById(R.id.tvTotalTargets)
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvFreq: TextView = v.findViewById(R.id.tvFreq)
        val targetProgress: ProgressBar = v.findViewById(R.id.progressIndicator)
        val card: CardView = v.findViewById(R.id.mcard)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_sales_target, viewGroup, false)


        return VHolder(view)
    }

    override fun onBindViewHolder(holder: VHolder, pos: Int) {

        val productTarget = FormatDigit.convertStringToDouble(salesTargetItemList[pos].target)
        val achievedTarget = FormatDigit.convertStringToDouble(salesTargetItemList[pos].achieved)
        holder.tvProductName.text = salesTargetItemList[pos].type
        holder.tvTotalTargets.text = "${achievedTarget.toInt()}/${productTarget.toInt()}"
        holder.targetProgress.progress = FormatDigit.convertStringToDouble(salesTargetItemList[pos].achievementPercentage).toInt()
        Log.d("TAG", "onBindViewHolder: ${FormatDigit.convertStringToDouble(salesTargetItemList[pos].achievementPercentage).toInt()}")
        holder.tvFreq.text = "${salesTargetItemList[pos].from} - ${salesTargetItemList[pos].to}"
        holder.tvLabel.text = "${salesTargetItemList[pos].achievementPercentage} %"
        /* holder.card.setOnClickListener {
             Navigation.findNavController(it).navigate(R.id.agentAccountsFragment)
         }*/
    }

    override fun getItemCount() = salesTargetItemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}