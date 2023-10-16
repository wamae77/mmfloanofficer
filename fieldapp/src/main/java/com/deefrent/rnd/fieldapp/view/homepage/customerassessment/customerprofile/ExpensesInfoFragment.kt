package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentExpensesInfoBinding
import com.deefrent.rnd.fieldapp.dtos.ExpensesDTO
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel


class ExpensesInfoFragment : Fragment() {
 private lateinit var binding:FragmentExpensesInfoBinding

    var idnumber=""
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerAssessmentHomeViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentExpensesInfoBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.apply { pb.makeGone()
        tvPbText.makeGone()
        tvExp.makeGone()
        tvTotalExpenses.makeGone()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewmodel.iDLookUpData.observe(viewLifecycleOwner){info->
                idnumber=info!!.idNumber
                if (info?.expenses==null){
                    note.makeVisible()
                    btnContinue.text="Continue"
                }else{
                    note.makeGone()
                    btnContinue.text="Update"
                    etRent.setText(FormatDigit.formatDigits(info.expenses.rentals))
                    etfees.setText(FormatDigit.formatDigits(info.expenses.schoolFees))
                    etTransport.setText(FormatDigit.formatDigits(info.expenses.transport))
                    etfood.setText(FormatDigit.formatDigits(info.expenses.food))
                    etMedical.setText(FormatDigit.formatDigits(info.expenses.medicalAidOrContributions))
                    etother.setText(FormatDigit.formatDigits(info.expenses.other))
                }
            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val rent = etRent.text.toString().trim()
                    val fees = etfees.text.toString().trim()
                    val transport = etTransport.text.toString().trim()
                    val food = etfood.text.toString().trim()
                    val medical = etMedical.text.toString().trim()
                    val others = etother.text.toString().trim()
                    if (food.isEmpty()) {
                        tlfood.error = "Required"
                    } else if (medical.isEmpty()) {
                        tlfood.error = ""
                        tlMedical.error = "Required"
                    } else if (others.isEmpty()) {
                        tlothers.error = "Required"
                    } else {
                        tlfood.error = ""
                        tlMedical.error = ""
                        tlothers.error = ""
                        val expensesDTO = ExpensesDTO()
                        expensesDTO.id_number = idnumber
                        expensesDTO.household_expenses_rentals = rent
                        expensesDTO.household_expenses_school_fees = fees
                        expensesDTO.household_expenses_medical_aid_contributions = medical
                        expensesDTO.household_expenses_transport = transport
                        expensesDTO.household_expenses_food = food
                        expensesDTO.household_expenses_others = others
                        binding.progressbar.tvWait.text=getString(R.string.please_wait)
                        progressbar.mainPBar.makeVisible()
                        viewmodel.addExpenses(expensesDTO)
                    }
                }else{
                    toastyErrors("Check your internet connection and try again")
                }

            }
            viewmodel.statusE.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            onInfoSuccessDialog("Expense information added successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            viewmodel.stopObserving()
                            onInfoDialog(getString(R.string.error_occurred))

                        }
                    }
                }
            }
            viewmodel._responseExpensesStatus.observe(viewLifecycleOwner){
                Log.d("TAG", "responseRemGStatus: $it")
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnContinue.isEnabled=false
                            binding.progressbar.mainPBar.makeVisible()
                            binding.progressbar.tvWait.text="Updating customer expenses information..."
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE->{
                            btnContinue.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR->{
                            btnContinue.isEnabled=true
                            binding.progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
        }

    }


}