package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.AddHouseAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentAssessSummaryBinding
import com.deefrent.rnd.fieldapp.databinding.SummaryFailedDialogBinding
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel

class AssessmentSummaryFragment : Fragment() {
    private lateinit var binding: FragmentAssessSummaryBinding
    private lateinit var cardBinding: SummaryFailedDialogBinding
    private lateinit var addHouseHoldAdapter: AddHouseAdapter
    private lateinit var assessCustEntity: AssessCustomerEntity
    private lateinit var householdEntity: List<HouseholdMemberEntity>
    private val items: ArrayList<HouseholdMemberEntity> = ArrayList()
    private var visibleByDefault: Boolean = false
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
        binding = FragmentAssessSummaryBinding.inflate(layoutInflater)
        binding.apply {
            addHouseHoldAdapter = AddHouseAdapter(items)
            rvMember.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvMember.adapter = addHouseHoldAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            llExpensesLayout.makeVisible()
            ivExpensesForward.setOnClickListener {
                Log.d("TAG", "Clickable:Clickable")

                if (llExpensesLayout.visibility == View.GONE) {
                    llExpensesLayout.makeVisible()
                } else {
                    llExpensesLayout.makeGone()
                }
            }
            ivIncomesForward.setOnClickListener {
                if (llIncomeLayout.visibility == View.GONE) {
                    llIncomeLayout.makeVisible()
                } else {
                    llIncomeLayout.makeGone()
                }
            }
           /* viewmodel.householdMemberEntity.observe(viewLifecycleOwner) {
                householdEntity=it
                val household = it.map { memb ->
                    HouseholdMember(
                        memb.fullName,
                        memb.incomeOrFeesPaid,
                        "",
                        memb.natureOfActivity,
                        memb.occupationId,
                        memb.relationshipId
                    )
                }
                householdMembers = household
                items.clear()
                items.addAll(it)
                binding.rvMember.adapter?.notifyDataSetChanged()
                val json = Gson()
                Log.d("TAG", "household: ${json.toJson(household)}")
            }*/
            btnSubmit.setOnClickListener {
                val remarks = etAssessment.text.toString()
                if (remarks.isEmpty()) {
                    toastyErrors("Add assessment remarks")
                } else if (!binding.cbAccount.isChecked) {
                    toastyErrors("Check to confirm the details provided are valid")
                } else {
                    assessmentRemarks = etAssessment.text.toString()
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { assessEntity ->
                       /* assessCustEntity = assessEntity
                        customerIdNumber = assessEntity.customerIdNumber
                        assessEntity.isComplete = true
                        assessEntity.lastStep = "AssessmentSummaryFragment"
                        assessEntity.assessmentRemarks = etAssessment.text.toString()
                        viewmodel.assessCustomerEntity.postValue(assessEntity)
                        saveCustomerAssessDataLocally(assessEntity)*/
                    }

                }
            }
        }
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {

                    GeneralResponseStatus.LOADING -> {
                        binding.btnSubmit.isEnabled = false
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.btnSubmit.isEnabled = true
                        binding.progressbar.mainPBar.makeGone()
                    }
                    else -> {
                        binding.btnSubmit.isEnabled = true
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewmodel.deleteAssessedCustomer(assessCustEntity)
                        findNavController().navigate(R.id.action_assessmentSummaryFragment_to_assessSuccessFragment)
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        viewmodel.stopObserving()
                        onInfoDialog(viewmodel.statusMessage.value)
                    }
                    else -> {
                        viewmodel.stopObserving()
                        showErrorDialog()

                    }
                }
            }
        }
    }

    companion object {
        private var customerIdNumber: String = ""
        private var assessmentRemarks: String = ""
        private var expenseFood: String = ""
        private var expenseRentals: String = ""
        private var expenseMedicalAidOrContributions: String = ""
        private var expenseSchoolFees: String = ""
        private var expenseTransport: String = ""
        private var incomeOtherBusinesses: String = ""
        private var incomeOwnSalary: String = ""
        private var incomeRemittanceOrDonation: String = ""
        private var incomeRental: String = ""
        private var otherExpenses: String = ""
        private var otherIncomes: String = ""
        private var householdMembers: List<HouseholdMember> = arrayListOf()
    }

   /* private fun saveCustomerAssessDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertCustomerDetails(assessCustomerEntity)
    }*/

    private fun showErrorDialog() {
        val dialog = Dialog(requireContext())
        cardBinding = SummaryFailedDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnNotNow.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_assessmentSummaryFragment_to_dashboardFragment)
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }


}
