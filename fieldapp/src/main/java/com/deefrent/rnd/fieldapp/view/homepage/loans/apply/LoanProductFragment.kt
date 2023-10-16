package com.deefrent.rnd.fieldapp.view.homepage.loans.apply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.data.LoanProductAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanProductBinding
import com.deefrent.rnd.fieldapp.network.models.LoanProduct
import com.deefrent.rnd.fieldapp.utils.callbacks.LoanProductCallBack
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel

class LoanProductFragment : Fragment(), LoanProductCallBack {
    private lateinit var binding: FragmentLoanProductBinding
    private var arraylist = ArrayList<LoanProduct>()
    private lateinit var  loanProductAdapter: LoanProductAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoanProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }

            loanProductAdapter= LoanProductAdapter(this@LoanProductFragment,requireContext(),arraylist,)
            binding.rvLoan.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.rvLoan.adapter=loanProductAdapter
            viewModel.loanLookUpData.observe(viewLifecycleOwner) {
                arraylist.clear()
                arraylist.addAll(it.products)
                loanProductAdapter.notifyDataSetChanged()
            }


        }
    }




    override fun onItemSelected(loanProduct: LoanProduct) {
        val direction=LoanProductFragmentDirections.actionLoanProductFragmentToApplyLoanFragment(
                loanProduct
            )
        findNavController().navigate(direction)
    }


}