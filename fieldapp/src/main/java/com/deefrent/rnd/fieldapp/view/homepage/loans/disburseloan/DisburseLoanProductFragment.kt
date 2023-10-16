package com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.DisburseLoanProductAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentDisburseLoanProductBinding
import com.deefrent.rnd.fieldapp.network.models.DisbursableLoan
import com.deefrent.rnd.fieldapp.utils.callbacks.PendingDisburseCallBack
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel
import kotlinx.android.synthetic.main.fragment_disburse_loan.*

class DisburseLoanProductFragment : Fragment(), PendingDisburseCallBack {
private lateinit var binding:FragmentDisburseLoanProductBinding
    private var arraylist = ArrayList<DisbursableLoan>()
    private lateinit var  loanProductAdapter: DisburseLoanProductAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDisburseLoanProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.loanHomeFragment)
        }
        binding.apply {
            loanProductAdapter= DisburseLoanProductAdapter(this@DisburseLoanProductFragment,requireContext(),arraylist,)
            binding.rvLoan.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.rvLoan.adapter=loanProductAdapter
            viewModel.loanLookUpData.observe(viewLifecycleOwner) {
                arraylist.clear()
                arraylist.addAll(it.disbursableLoans)
                loanProductAdapter.notifyDataSetChanged()
            }
        }
    }
  override fun onItemSelected(items: DisbursableLoan) {
        val direction=DisburseLoanProductFragmentDirections.actionDisburseLoanProductFragmentToDisburseLoanFragment(items)
        findNavController().navigate(direction)
    }

}