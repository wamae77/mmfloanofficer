package com.deefrent.rnd.fieldapp.ui.loanApplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanApplicationBinding

class LoanApplicationFragment : Fragment() {
    private var _binding: FragmentLoanApplicationBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoanApplicationBinding.inflate(inflater, container, false)
        val view = binding.root

        initButtonClickListeners()
        initViews()
        return view
    }

    private fun initViews() {
        //populate repayment period autocomplete
        val repaymentPeriods = resources.getStringArray(R.array.repayment_period)
        val repaymentPeriodsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, repaymentPeriods)
        binding.acPeriod.setAdapter(repaymentPeriodsAdapter)
        binding.acPeriod.keyListener = null
    }

    private fun initButtonClickListeners() {
        binding.btnCalculate.setOnClickListener {
            binding.btnApply.visibility = View.VISIBLE
            binding.btnReset.visibility = View.VISIBLE
            binding.llMoreDetails.visibility = View.VISIBLE
            binding.btnCalculate.visibility = View.GONE
        }
        binding.btnReset.setOnClickListener {
            binding.btnApply.visibility = View.GONE
            binding.btnReset.visibility = View.GONE
            binding.llMoreDetails.visibility = View.GONE
            binding.btnCalculate.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoanApplicationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}