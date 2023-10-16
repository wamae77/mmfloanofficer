package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentExpensesInfoBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.FormatDigit.Companion.roundTo
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.gson.Gson
import java.util.ArrayList


class AssessExpensesFragment : Fragment() {
    private lateinit var binding: FragmentExpensesInfoBinding
    private lateinit var guarantor :List<AssessGuarantor>
    private lateinit var collateral :List<AssessCollateral>
    private lateinit var household :List<AssessHouseholdMemberEntity>
    private lateinit var otherBorrowing :List<AssessBorrowing>
    private var customerDocs: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpensesInfoBinding.inflate(layoutInflater)
        handleBackButton()
        viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
            getSavedItemFroomRoom(nationalId)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            note.makeGone()
            btnContinue.text = "Continue"
            btnContinue.setOnClickListener {
                val rent = etRent.text.toString().trim()
                val fees = etfees.text.toString().trim()
                val transports = etTransport.text.toString().trim()
                val foods = etfood.text.toString().trim()
                val medicals = etMedical.text.toString().trim()
                val others = etother.text.toString().trim()
                if (foods.isEmpty()) {
                    tlfood.error = "Required"
                } else if (medicals.isEmpty()) {
                    tlfood.error = ""
                    tlMedical.error = "Required"
                } else if (others.isEmpty()) {
                    tlothers.error = "Required"
                } else {
                    tlfood.error = ""
                    tlMedical.error = ""
                    tlothers.error = ""
                    viewmodel.apply {
                        assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                            detailsEntity.apply {
                                lastStep = "AssessExpensesFragment"
                               isComplete = false
                                hasFinished = false
                                isProcessed = false
                                expenseRentals=etRent.text.toString().trim()
                                expenseFood = etfood.text.toString().trim()
                                expenseSchoolFees=etfees.text.toString().trim()
                                expenseTransport=etTransport.text.toString().trim()
                                expenseMedicalAidOrContributions=etMedical.text.toString().trim()
                                otherExpenses=etother.text.toString().trim()
                                viewmodel.assessCustomerEntity.postValue(detailsEntity)
                               saveAssessmentDataLocally(detailsEntity)

                            }
                        }
                    }
                 findNavController().navigate(R.id.action_assessExpensesFragment_to_assessAdditionalDetailsFragment)

                }


            }
            binding.ivBack.setOnClickListener { v ->
                findNavController().navigate(R.id.assessIncomeFragment)
            }

            val textWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
                    if (etRent.text.toString().trim().isNotEmpty()
                        || (etfees.text.toString().trim()).isNotEmpty()
                        ||(etTransport.text.toString().trim()).isNotEmpty()
                        ||(etfood.text.toString().trim()).isNotEmpty()
                        || (etMedical.getText().toString().trim()).isNotEmpty()
                        ||(etother.getText().toString().trim()).isNotEmpty()
                    ) {
                        val firtValue = if (TextUtils.isEmpty(etRent.getText().toString().trim())
                        ) 0f else etRent.text.toString().trim().toFloat()
                        val secondValue = if (TextUtils.isEmpty(
                                etfees.text.toString().trim()
                            )
                        ) 0f else etfees.text.toString().trim().toFloat()
                        val thirdValue = if (TextUtils.isEmpty(
                                etTransport.text.toString().trim()
                            )
                        ) 0f else etTransport.getText().toString().trim().toFloat()
                        val forthValue = if (TextUtils.isEmpty(
                                etfood.getText().toString().trim()
                            )
                        ) 0f else
                            etfood.getText().toString().trim().toFloat()
                        val fifthValue = if (TextUtils.isEmpty(
                                etMedical.getText().toString().trim()
                            )
                        ) 0f else
                            etMedical.getText().toString().trim().toFloat()
                        val sithValue = if (TextUtils.isEmpty(
                                etother.getText().toString().trim()
                            )
                        ) 0f else
                            etother.getText().toString().trim().toFloat()
                        val answer =
                            firtValue + secondValue + thirdValue + forthValue + fifthValue + sithValue
                        Log.e("RESULT", answer.toString())
                        val finalAns= roundTo(answer.toDouble())
                        tvTotalExpenses.setText(finalAns.toString())
                    } else {
                        tvTotalExpenses.setText("0")
                    }
                }

                override fun afterTextChanged(editable: Editable?) {}
            }
            etRent.addTextChangedListener(textWatcher)
            etfees.addTextChangedListener(textWatcher)
            etfood.addTextChangedListener(textWatcher)
            etTransport.addTextChangedListener(textWatcher)
            etMedical.addTextChangedListener(textWatcher)
            etother.addTextChangedListener(textWatcher)


        }

    }

    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity,customerDocs,
            collateral,
            guarantor,
            otherBorrowing,household
        )
    }
    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.assessIncomeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getSavedItemFroomRoom(parentId:String){
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {info->
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(info)}",)
                collateral = info.assessCollateral
                Log.e("TAG", "assessCollateral: $collateral",)
                guarantor = info.assessGua
                otherBorrowing = info.assessBorrow
                household = info.householdMember
                customerDocs.clear()
                customerDocs.addAll(info.customerDocs)
                binding.apply {
                    if (info.assessCustomerEntity.expenseRentals.isNotEmpty()){
                        etRent.setText(roundTo(info.assessCustomerEntity.expenseRentals.toDouble()))
                    }
                    if (info.assessCustomerEntity.expenseTransport.isNotEmpty()){
                        etTransport.setText(roundTo(info.assessCustomerEntity.expenseTransport.toDouble()))
                    }
                    if (info.assessCustomerEntity.expenseSchoolFees.isNotEmpty()){
                        etfees.setText(roundTo(info.assessCustomerEntity.expenseSchoolFees.toDouble()))
                    }
                    if (info.assessCustomerEntity.expenseFood.isNotEmpty()){
                        etfood.setText(roundTo(info.assessCustomerEntity.expenseFood.toDouble()))
                    }
                    if (info.assessCustomerEntity.expenseMedicalAidOrContributions.isNotEmpty()){
                        etMedical.setText(roundTo(info.assessCustomerEntity.expenseMedicalAidOrContributions.toDouble()))
                    }
                    if (info.assessCustomerEntity.otherExpenses.isNotEmpty()){
                        etother.setText(roundTo(info.assessCustomerEntity.otherExpenses.toDouble()))
                    }
                }

            }
        }
    }



}