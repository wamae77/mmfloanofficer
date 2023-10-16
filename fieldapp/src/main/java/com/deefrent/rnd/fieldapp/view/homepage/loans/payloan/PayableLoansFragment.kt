package com.deefrent.rnd.fieldapp.view.homepage.loans.payloan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.data.adapters.LoanPayableAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentPayableLoansBinding
import com.deefrent.rnd.fieldapp.network.models.RepayableLoan
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanPayableCallBack
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel

class PayableLoansFragment : Fragment(), LoanPayableCallBack {
    private lateinit var binding: FragmentPayableLoansBinding
    private var arraylist = ArrayList<RepayableLoan>()
    private lateinit var loanProductAdapter: LoanPayableAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPayableLoansBinding.inflate(layoutInflater)
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
                LoanPayableAdapter(this@PayableLoansFragment, requireContext(), arraylist)
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
        val direction =
            PayableLoansFragmentDirections.actionPayableLoansFragmentToLoanPaymentFragment(
                loanProduct
            )
        findNavController().navigate(direction)
    }

}