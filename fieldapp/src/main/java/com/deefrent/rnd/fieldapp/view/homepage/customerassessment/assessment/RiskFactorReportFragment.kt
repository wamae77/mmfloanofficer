package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentRiskFactorReportBinding
import com.deefrent.rnd.fieldapp.utils.toastyErrors
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel


class RiskFactorReportFragment : Fragment() {

    private lateinit var binding: FragmentRiskFactorReportBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRiskFactorReportBinding.inflate(layoutInflater)
        binding.apply {
            btnSubmit.setOnClickListener {
                when (btnSubmit.text) {
                    "CHECK LOAN LIMIT" -> simulateLoanLimitCheck(
                        "Checking Loan Limit...",
                        requireContext()
                    )
                    "SUBMIT" -> {
                        val remarks = etAssessment.text.toString()
                        if (remarks.isEmpty()) {
                            toastyErrors("Add assessment remarks")
                        } else {
                            assessmentRemarks = etAssessment.text.toString()
                            /*viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { assessEntity ->
                                assessCustEntity = assessEntity
                                customerIdNumber = assessEntity.customerIdNumber
                                assessEntity.isComplete = true
                                assessEntity.lastStep = "RiskFactorAnalysisFragment"
                                assessEntity.assessmentRemarks = etAssessment.text.toString()
                                viewmodel.assessCustomerEntity.postValue(assessEntity)
                            }*/
                        }
                    }
                }
            }
        }
        callDialog("Calculating Risk Factor...", requireContext())
        binding.btnCheckLoanLimit.setOnClickListener {
            callDialog2("Checking Loan Limit...", requireContext(), "loanLimit")
        }
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    private fun callDialog(message: String?, context: Context?) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
        val handler = Handler()
        val runnable = Runnable {
            progressDialog?.dismiss()
        }
        progressDialog?.setOnDismissListener {
            binding.apply {
                cvRiskFactorAnalysis.visibility = View.VISIBLE
                //tvAss.visibility=View.VISIBLE
                //tlassessment.visibility=View.VISIBLE
                btnSubmit.visibility = View.VISIBLE
                //cbAccount.visibility=View.VISIBLE
                cvLoanLimit.visibility = View.VISIBLE
            }
            handler.removeCallbacks(
                runnable
            )
        }

        handler.postDelayed(runnable, 1000)
    }

    private fun simulateLoanLimitCheck(message: String?, context: Context?) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
        val handler = Handler()
        val runnable = Runnable {
            progressDialog?.dismiss()
        }
        progressDialog?.setOnDismissListener {
            binding.apply {
                //tvAss.visibility=View.VISIBLE
                tlassessment.visibility = View.VISIBLE
                btnSubmit.text = "SUBMIT"
                cbAccount.visibility = View.VISIBLE
                tvLoanLimit.text = "USD 100"
            }
            handler.removeCallbacks(
                runnable
            )
        }

        handler.postDelayed(runnable, 1000)
    }

    private fun callDialog2(message: String?, context: Context?, action: String) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
        val handler = Handler()
        val runnable = Runnable {
            progressDialog?.dismiss()
        }
        when (action) {
            "loanLimit" -> binding.tvLoanLimit.text = "-"
        }
        progressDialog?.setOnDismissListener {
            when (action) {
                "loanLimit" -> binding.tvLoanLimit.text = "USD 100"
            }
            handler.removeCallbacks(
                runnable
            )
        }

        handler.postDelayed(runnable, 1000)
    }

    companion object {
        private var assessmentRemarks: String = ""
        private var customerIdNumber: String = ""

    }

}