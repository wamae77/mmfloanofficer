package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.os.Bundle
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
import com.google.gson.Gson


class Step11AddExpensesFragment : Fragment() {
    private lateinit var binding: FragmentExpensesInfoBinding
    private lateinit var householdEntity: List<HouseholdMemberEntity>
    private lateinit var guarantor: List<Guarantor>
    private lateinit var borrowings: List<OtherBorrowing>
    private lateinit var collateral: List<Collateral>
    private lateinit var customerDocs: ArrayList<CustomerDocsEntity>
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpensesInfoBinding.inflate(layoutInflater)
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            getSavedItemsFromRoom(customerIDNumber)
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            note.makeGone()
            llTotalIncome.makeGone()
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
                        customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                            customerDetailsEntity.apply {
                                lastStep = "AddExpensesFragment"
                                isComplete = false
                                isProcessed = false
                                hasFinished = false
                                rentalsExpenses = etRent.text.toString().trim()
                                food = etfood.text.toString().trim()
                                schoolFees = etfees.text.toString().trim()
                                transport = etTransport.text.toString().trim()
                                medicalAidOrContributions = etMedical.text.toString().trim()
                                otherExpenses = etother.text.toString().trim()
                                viewmodel.customerEntityData.postValue(customerDetailsEntity)
                                saveCustomerFullDatLocally(customerDetailsEntity)
                                Log.d(
                                    "TAG",
                                    "assessCustomerEntity: ${Gson().toJson(customerDetailsEntity)}"
                                )
                            }
                        }
                    }
                    if (Constants.fromSummary == 7) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_addExpensesFragment_to_customerAdditionalDetailsFragment)

                    }

                }


            }
            binding.ivBack.setOnClickListener { v ->
                if (Constants.fromSummary == 7) {
                    findNavController().navigate(R.id.summaryFragment)
                } else {
                    findNavController().navigate(R.id.action_addExpensesFragment_to_addIncomeFragment)

                }
            }
            handleBackButton()
        }

    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            borrowings,
            householdEntity,
            customerDocs
        )
    }


    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (Constants.fromSummary == 7) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_addExpensesFragment_to_addIncomeFragment)

                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {cde->
            Log.d("TAG", "getSavedItemsFromRoom: ${Gson().toJson(cde)}")
            Log.d("TAG", "getSavedItemsFromRoom1: ${Gson().toJson(cde.guarantors)}")
            Log.d("TAG", "getSavedItemsFromRoom1: ${Gson().toJson(cde.householdMember)}")
            Log.d("TAG", "getSavedItemsFromRoom1: ${Gson().toJson(cde.otherBorrowing)}")
            Log.d("TAG", "getSavedItemsFromRoom2: ${Gson().toJson(cde.customerDocs)}")
            Log.d("TAG", "getSavedItemsFromRoom3: ${Gson().toJson(cde.collateral)}")
            binding.apply {
                collateral=cde.collateral
                guarantor = cde.guarantors as ArrayList<Guarantor>
                borrowings = cde.otherBorrowing
                householdEntity = cde.householdMember
                customerDocs = cde.customerDocs as ArrayList<CustomerDocsEntity>
                if (cde.customerDetails.rentalsExpenses.isNotEmpty()) {
                    etRent.setText(FormatDigit.formatDigits(cde.customerDetails.rentalsExpenses))
                }
                if (cde.customerDetails.food.isNotEmpty()) {
                    etfood.setText(FormatDigit.formatDigits(cde.customerDetails.food))
                }
                if (cde.customerDetails.schoolFees.isNotEmpty()) {
                    etfees.setText(FormatDigit.formatDigits(cde.customerDetails.schoolFees))
                }
                if (cde.customerDetails.transport.isNotEmpty()) {
                    etTransport.setText(FormatDigit.formatDigits(cde.customerDetails.transport))
                }
                if (cde.customerDetails.medicalAidOrContributions.isNotEmpty()) {
                    etMedical.setText(FormatDigit.formatDigits(cde.customerDetails.medicalAidOrContributions))
                }
                if (cde.customerDetails.otherExpenses.isNotEmpty()) {
                    etother.setText(FormatDigit.formatDigits(cde.customerDetails.otherExpenses))
                }
            }
        }
    }



}