package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.databinding.ListItemFuneralInsurancePackageBinding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FuneralCashPlanPackagesData
import com.deefrent.rnd.fieldapp.utils.FormatDigit

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class FuneralCashPlanPackagesListAdapter(
    val callback: FuneralCashPlanPackageCallback
) :
    ListAdapter<FuneralCashPlanPackagesData, FuneralCashPlanPackagesListAdapter.ViewHolder>(
        DIFF_UTIL
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemFuneralInsurancePackageBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packages = getItem(position)
        val contributionPeriod = packages.contributionPeriod
        holder.binding.apply {
            tvPackageName.text = packages.name
            if (contributionPeriod > 1) {
                val contributionPeriodMeasure =
                    packages.contributionPeriodMeasure.replace("(s)", "s")
                tvAmount.text =
                    "USD ${
                        FormatDigit.formatDigits(
                            if (packages.adultDependantContribution.isEmpty()) {
                                "0"
                            } else {
                                packages.adultDependantContribution.toDouble().toInt().toString()
                            }
                        )
                    } / $contributionPeriod $contributionPeriodMeasure"
            } else {
                val contributionPeriodMeasure =
                    packages.contributionPeriodMeasure.replace("(s)", "")
                tvAmount.text =
                    "USD ${
                        FormatDigit.formatDigits(
                            if (packages.adultDependantContribution.isEmpty()) {
                                "0"
                            } else {
                                packages.adultDependantContribution.toDouble().toInt().toString()
                            }
                        )
                    } / $contributionPeriodMeasure"
            }

            val adultDependantContribution =
                if (packages.adultDependantContribution == "null" && packages.adultDependantContribution.isEmpty()) {
                    "0"
                } else {
                    packages.adultDependantContribution.toDouble().toInt().toString()
                }
            val maxDependants = if (packages.maxDependants.toString() == "null") {
                "0"
            } else {
                packages.maxDependants.toString()
            }
            tvDependants.text =
                "Up to ${maxDependants ?: ""} dependants @ USD ${
                    FormatDigit.formatDigits(
                        adultDependantContribution
                    )
                } each"
            val cashbackAmount =
                if (packages.cashbackAmount == null) {
                    "0"
                } else {
                    packages.cashbackAmount.toString().toDouble().toInt().toString()
                }
            tvCashBack.text =
                "Get cash backs of up to USD ${
                    cashbackAmount.toString()
                    //FormatDigit.formatDigits(cashbackAmount)
                } for each beneficiary"
            btnApply.setOnClickListener {
                callback.onItemSelected(it, packages)
            }
        }
    }

    inner class ViewHolder(val binding: ListItemFuneralInsurancePackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}


private val DIFF_UTIL = object : DiffUtil.ItemCallback<FuneralCashPlanPackagesData>() {
    override fun areItemsTheSame(
        oldItem: FuneralCashPlanPackagesData,
        newItem: FuneralCashPlanPackagesData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FuneralCashPlanPackagesData,
        newItem: FuneralCashPlanPackagesData
    ): Boolean {
        return oldItem == newItem
    }

}

interface FuneralCashPlanPackageCallback {
    fun onItemSelected(view: View, item: FuneralCashPlanPackagesData)
}