package com.deefrent.rnd.fieldapp.ui.agentField360

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentMonthlyCommissionReportBinding
import com.deefrent.rnd.fieldapp.models.Report
import com.deefrent.rnd.fieldapp.models.merchantAgentReport.Transaction
import com.deefrent.rnd.fieldapp.utils.doubleToStringNoDecimal
import com.deefrent.rnd.fieldapp.utils.formatLineChartLabels
import com.deefrent.rnd.fieldapp.utils.formatMonthFromPicker
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.deefrent.rnd.fieldapp.viewModels.ReportsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kal.rackmonthpicker.RackMonthPicker
import java.util.*
import kotlin.collections.ArrayList

class MonthlyCommissionReportFragment : Fragment() {
    private var _binding: FragmentMonthlyCommissionReportBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AgentFieldSharedViewModel by activityViewModels()
    private lateinit var lineChart: LineChart
    private var scoreList = ArrayList<Report>()
    private var transactionsList = ArrayList<Transaction>()
    private lateinit var selectedMonth: MutableLiveData<Int>
    private lateinit var selectedYear: MutableLiveData<Int>
    private lateinit var selectedMonthYear: MutableLiveData<String>
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    private lateinit var reportsViewModel: ReportsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reportsViewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        _binding = FragmentMonthlyCommissionReportBinding.inflate(inflater, container, false)
        val view = binding.root

        selectedMonth = MutableLiveData()
        selectedYear = MutableLiveData()
        selectedMonthYear = MutableLiveData()
        selectedMonthYear.observe(viewLifecycleOwner) { date ->
            binding.tvMonthYear.text = date
        }

        binding.ivMonthPicker.setOnClickListener {
            RackMonthPicker(requireActivity())
                .setLocale(Locale.ENGLISH)
                .setSelectedMonth(7)
                .setColorTheme(Color.parseColor("#f9e200"))
                .setPositiveButton { month, startDate, endDate, year, monthLabel ->
                    formatMonthYear(month, year)
                }
                .setNegativeButton {
                }.show()
        }

        lineChart = binding.lineChart
        initLineChart()
        observeSharedViewModel()
        getLastMonthYear()
        return view
    }

    private fun getLastMonthYear() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val currentYear = calendar[Calendar.YEAR]
        val lastMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        formatMonthYear(lastMonth, currentYear)

    }

    private fun formatMonthYear(month: Int, year: Int) {
        binding.tvMonthYear.text = "${formatMonthFromPicker(month)} - $year"
        getTransactionsReport("01-$month-$year")
    }

    private fun observeSharedViewModel() {
        var selectedUserType = ""
        sharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    selectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    when (selectedUserType) {
                        "Individual" -> {
                            existingAccountViewModel.apply {
                                customerDetailsResponse.observe(viewLifecycleOwner,
                                    { customerDetailsResponse ->
                                        binding.tvAccountNumber.text =
                                            "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $accountNumber"
                                        binding.tvAgentNumber.text =
                                            "${customerDetailsResponse.customerDetailsData.customerDetails.firstName}  ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${customerDetailsResponse.customerDetailsData.customerDetails.phoneNo}"
                                    })
                            }
                        }
                        "Agent" -> {
                            binding.tvAgentNumber.text = "Agent No: 3782"
                            existingAccountViewModel.apply {
                                merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                    { merchantAgentDetailsResponse ->
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                        binding.tvAccountNumber.text =
                                            "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                                    })
                            }
                        }
                        "Merchant" -> {
                            binding.tvAgentNumber.text = "Merchant No: 3782"
                            existingAccountViewModel.apply {
                                merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                    { merchantAgentDetailsResponse ->
                                        binding.tvAgentTelephone.text =
                                            "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                        binding.tvAccountNumber.text =
                                            "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $accountNumber"
                                    })
                            }
                        }
                    }

                })
        }

    }

    private fun initLineChart() {
        //hide grid lines
        lineChart.axisLeft.setDrawGridLines(true)
        lineChart.axisRight.setDrawZeroLine(true)
        lineChart.axisRight.setDrawGridLines(true)
        lineChart.axisLeft.axisMinimum = 0f
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(true)
        xAxis.setDrawAxisLine(true)
        xAxis.valueFormatter = MyAxisFormatter()

        //remove right y-axis
        lineChart.axisRight.isEnabled = false

        //remove legend
        lineChart.legend.isEnabled = false

        //remove description label
        lineChart.description.isEnabled = false

        lineChart.axisLeft.setDrawLabels(false);
        //lineChart.axisRight.setDrawLabels(false);

        //add animation
        lineChart.animateX(1000, Easing.EaseInSine)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f

        xAxis.labelRotationAngle = -45f

    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < transactionsList.size) {
                transactionsList[index].date
            } else {
                ""
            }
        }
    }

    private fun setDataToLineChart(transactionsList: List<Transaction>) {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        //transactionsList = getTransactionsReport("01-08-2021") //getScoreList()

        //you can replace this data object with  your custom object
        for (i in transactionsList.indices) {
            val transaction = transactionsList[i]
            entries.add(Entry(i.toFloat(), transaction.amount.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Amount Transacted")
        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.lineWidth = 3f
        lineDataSet.fillColor = Color.BLUE
        lineDataSet.fillAlpha = Color.DKGRAY

        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()

        lineChart.description.text = "Weeks (Week Start Date)"
        lineChart.setNoDataText("No data yet!")
    }

    /*private fun setDataToLineChart() {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        scoreList = getScoreList()

        //you can replace this data object with  your custom object
        for (i in scoreList.indices) {
            val transaction = scoreList[i]
            entries.add(Entry(i.toFloat(), transaction.amount.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Amount Transacted")
        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.lineWidth = 3f
        lineDataSet.fillColor = Color.BLUE
        lineDataSet.fillAlpha = Color.DKGRAY

        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()

        lineChart.description.text = "Weeks (Week Start Date)"
        lineChart.setNoDataText("No data yet!")
    }*/

    private fun getTransactionsReport(date: String) {
        transactionsList.clear()
        binding.tvAmount.visibility = View.GONE
        binding.tvWeeks.visibility = View.GONE
        binding.lineChart.visibility = View.GONE
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvTotalAmount.text = "-"
        reportsViewModel.getTransactionsReport(date)
            .observe(viewLifecycleOwner) { getTransactionsReportResponse ->
                if (getTransactionsReportResponse != null) {
                    val transactions = getTransactionsReportResponse.transactions
                    binding.lineChart.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                    if (transactions.isNotEmpty()) {
                        binding.tvAmount.visibility = View.VISIBLE
                        binding.tvWeeks.visibility = View.VISIBLE
                        var totalAmount = 0.0
                        for (transaction in transactions) {
                            transactionsList.add(
                                Transaction(
                                    formatLineChartLabels(transaction.date),
                                    (transaction.amount)*0.1
                                )
                            )
                            totalAmount += (transaction.amount)*0.1
                        }
                        binding.tvTotalAmount.text = doubleToStringNoDecimal(totalAmount)
                        binding.tvTotalTransactions.text="18"
                        setDataToLineChart(transactionsList)
                    }
                    Log.d("TAG", "getTransactionsReport: ${transactions.size}")
                } else {
                    binding.tvTotalTransactions.text="-"
                    binding.lineChart.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthlyCommissionReportFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}