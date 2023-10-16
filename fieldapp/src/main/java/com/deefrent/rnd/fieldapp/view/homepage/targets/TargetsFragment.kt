package com.deefrent.rnd.fieldapp.view.homepage.targets

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.TargetsAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentTargetsBinding
import com.deefrent.rnd.fieldapp.models.targets.Target
import com.deefrent.rnd.fieldapp.utils.FormatDigit
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.TargetsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.*
import kotlin.collections.ArrayList

class TargetsFragment : Fragment() {
    private lateinit var binding: FragmentTargetsBinding
    private lateinit var targetsViewModel:  TargetsViewModel
    private lateinit var targetsAdapter: TargetsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mPieChart: PieChart
    var targetsList = arrayListOf<Target>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTargetsBinding.inflate(inflater, container, false)
        targetsViewModel =
            ViewModelProvider(requireActivity())[TargetsViewModel::class.java]
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_targetsFragment_to_dashboardFragment)
        }
        initUI()
        binding.rvTargets.makeGone()
        binding.pieChart.makeGone()
        /*val item4 = Target("New Customer Accounts", "Weekly", 13, 1, 400)
        val item5 = Target("Assessment", "Weekly", 10, 15, 400)
        val item3 = Target("Loans", "Monthly", 14, 13, 300)
        val item1 = Target("Airtime Sales", "Daily", 40, 3, 100)
        val item2 = Target("Bill Payment Sales", "Daily", 32, 3, 100)
        list.add(item1)
        list.add(item2)
        list.add(item3)
        list.add(item4)
        list.add(item5)*/

        targetsAdapter = TargetsAdapter(targetsList)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvTargets.apply {
            layoutManager = linearLayoutManager
            adapter = targetsAdapter
        }
        //loadTransactionGraph(list)
        getSalesTargets()
        binding.btnRefresh.setOnClickListener {
            getSalesTargets()
        }
        return binding.root
    }

    private fun getSalesTargets() {
        Log.d("TAG", "getSalesTargets: hello")
        if (isNetworkAvailable(requireContext())) {
            binding.rvTargets.makeGone()
            binding.pieChart.makeGone()
            binding.tvError.makeGone()
            binding.btnRefresh.makeGone()
            targetsViewModel.getTargetsData()
            observeViewModel()
        } else {
            binding.rvTargets.makeGone()
            binding.pieChart.makeGone()
            binding.tvError.makeVisible()
            binding.btnRefresh.makeVisible()
            binding.tvError.text = "Please check your internet connection and try again!"
        }
    }

    private fun observeViewModel() {
        binding.rvTargets.makeGone()
        binding.pieChart.makeGone()
        binding.tvError.makeGone()
        binding.btnRefresh.makeGone()
        targetsViewModel.targetsData.observe(viewLifecycleOwner) {
            Log.d("TAG", "incompleteData:$it ")
            targetsList.clear()
            targetsList.addAll(it)
            targetsAdapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                binding.rvTargets.makeGone()
                binding.pieChart.makeGone()
                binding.tvError.makeGone()
                binding.tvError.text="No targets at the moment\nPlease try again later"
                binding.btnRefresh.makeVisible()
            } else {
                binding.tvError.makeGone()
                binding.rvTargets.makeVisible()
                binding.pieChart.makeVisible()
                binding.btnRefresh.makeGone()
            }
        }
    }

    fun capitalizeWords(inputString: String): String {
        val space = " "
        val splitedStr = inputString.split(space)
        return splitedStr.joinToString(space) {
            it.capitalize()
        }
    }

    private fun loadTargetsGraph(list: ArrayList<Target>) {
        mPieChart.setUsePercentValues(false)
        mPieChart.description.isEnabled = false
        mPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        mPieChart.dragDecelerationFrictionCoef = 0.99f
        mPieChart.setDrawSlicesUnderHole(true)
        mPieChart.isDrawHoleEnabled = true
        mPieChart.setEntryLabelTextSize(8f)
        mPieChart.holeRadius = 60f
        mPieChart.setEntryLabelColor(Color.BLACK)

        mPieChart.setHoleColor(Color.WHITE)
        mPieChart.transparentCircleRadius = 1f

        val yValues = ArrayList<PieEntry>()

        var total = 0
        for (item in list) {
            if ((FormatDigit.convertStringToDouble(item.achieved)).toInt() > 0) {
                total += (FormatDigit.convertStringToDouble(item.achieved)).toInt()
            }
        }
        Log.d("TAG", "loadTargetsGraph: $total")
        for (item in list) {

            if ((FormatDigit.convertStringToDouble(item.achieved)).toInt() > 0) {
                yValues.add(
                    PieEntry(
                        (FormatDigit.convertStringToDouble(item.achieved)).toFloat(), "${
                            capitalizeWords(
                                item.type.lowercase(Locale.ROOT)
                            )
                        } ${(FormatDigit.convertStringToDouble(item.achieved)).toInt()}"
                    )
                )
            }
        }


        if (total < 1) {
            mPieChart.centerText = "You have not achieved any targets yet"

        } else {
            mPieChart.centerText = "Total\n$total"

        }

        mPieChart.setCenterTextSize(12f)
        mPieChart.animateX(2000, Easing.EaseInOutQuad)
        mPieChart.legend.isEnabled = false
        mPieChart.setDrawEntryLabels(true)
        mPieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        mPieChart.legend.isWordWrapEnabled = true
        mPieChart.legend.maxSizePercent = 0.20f
        mPieChart.minAngleForSlices = 12f

        val dataSet = PieDataSet(yValues, "")
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 14f
        dataSet.setDrawValues(false)
        dataSet.isUsingSliceColorAsValueLineColor = true
        dataSet.setColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA);
        //  dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChart))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)
        binding.pieChart.data = data
    }


    private fun initUI() {
        // initToolbar()
        mPieChart = binding.pieChart
        mPieChart.setNoDataText("");
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        targetsViewModel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.btnRefresh.makeGone()
                        binding.rvTargets.makeGone()
                        binding.pieChart.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.rvTargets.makeVisible()
                        binding.pieChart.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.rvTargets.makeGone()
                        binding.pieChart.makeGone()
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        targetsViewModel.status.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        targetsViewModel.targetsData.observe(viewLifecycleOwner) {
                            Log.d("TAG", "incompleteData:$it ")
                            targetsList.clear()
                            targetsList.addAll(it)
                            targetsAdapter.notifyDataSetChanged()
                            if (it.isEmpty()) {
                                binding.rvTargets.makeGone()
                                binding.pieChart.makeGone()
                                binding.tvError.makeVisible()
                                binding.tvError.text="No targets at the moment\nPlease try again later"
                                binding.btnRefresh.makeGone()
                            } else {
                                binding.tvError.makeGone()
                                binding.rvTargets.makeVisible()
                                binding.pieChart.makeVisible()
                                loadTargetsGraph(targetsList)
                                binding.btnRefresh.makeGone()
                            }
                        }
                        targetsViewModel.stopObserving()
                    }
                    0 -> {
                        targetsViewModel.stopObserving()
                        binding.tvError.text = targetsViewModel.statusMessage.value
                        binding.tvError.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.rvTargets.makeGone()
                        binding.pieChart.makeGone()
                    }
                    else -> {
                        targetsViewModel.stopObserving()
                        binding.tvError.text = getString(R.string.error_occurred)
                        binding.tvError.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.rvTargets.makeGone()
                        binding.pieChart.makeGone()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TargetsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}