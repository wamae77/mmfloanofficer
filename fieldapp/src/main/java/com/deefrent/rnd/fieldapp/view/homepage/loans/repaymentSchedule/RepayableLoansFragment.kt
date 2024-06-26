package com.deefrent.rnd.fieldapp.view.homepage.loans.repaymentSchedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.data.adapters.RepayableLoansAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentRepayableLoansBinding
import com.deefrent.rnd.fieldapp.network.models.RepayableLoan
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanPayableCallBack
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel

class RepayableLoansFragment : Fragment(), LoanPayableCallBack {
    private lateinit var binding: FragmentRepayableLoansBinding
    private var arraylist = ArrayList<RepayableLoan>()
    private lateinit var loanProductAdapter: RepayableLoansAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
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
        binding = FragmentRepayableLoansBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }

        binding.apply {
            loanProductAdapter =
                RepayableLoansAdapter(this@RepayableLoansFragment, requireContext(), arraylist)
            binding.rvLoan.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.rvLoan.adapter = loanProductAdapter
            viewModel.loanLookUpData.observe(viewLifecycleOwner) {
                arraylist.clear()
                arraylist.addAll(it.repayableLoans)
                loanProductAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemSelected(loanProduct: RepayableLoan) {
        //val direction= PayableLoansFragmentDirections.actionPayableLoansFragmentToLoanPaymentFragment(loanProduct)
        val direction =
            RepayableLoansFragmentDirections.actionRepayableLoansFragmentToRepaymentScheduleFragment(
                loanProduct
            )
        findNavController().navigate(direction)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RepayableLoansFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}