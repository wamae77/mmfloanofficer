package com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.data.adapters.LoanPendingApprovalAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanPendingApprovalBinding
import com.deefrent.rnd.fieldapp.network.models.LoansPendingApproval
import com.deefrent.rnd.fieldapp.utils.callbacks.PendingLoanCallBack
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.loans.LoanLookUpViewModel

class LoanPendingApprovalFragment : Fragment(),PendingLoanCallBack {
    private lateinit var binding: FragmentLoanPendingApprovalBinding
    private var arraylist = ArrayList<LoansPendingApproval>()
    private lateinit var  loanProductAdapter: LoanPendingApprovalAdapter
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoanPendingApprovalBinding.inflate(layoutInflater)
        viewModel.responseStatus.observe(viewLifecycleOwner){
            if (it!=null){
                when(it){
                    GeneralResponseStatus.LOADING->{
                        binding.mainLayout.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE->{
                        binding.mainLayout.makeVisible()
                        binding.progressbar.mainPBar.makeGone()}
                    else->{binding.progressbar.mainPBar.makeGone()
                        binding.mainLayout.makeVisible()
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.apply {
            loanProductAdapter= LoanPendingApprovalAdapter(requireContext(),arraylist,this@LoanPendingApprovalFragment)
            binding.rvLoan.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.rvLoan.adapter=loanProductAdapter
            viewModel.loanLookUpData.observe(viewLifecycleOwner) {
                arraylist.clear()
                arraylist.addAll(it.loansPendingApproval)
                loanProductAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemSelected(pos: Int, lists: LoansPendingApproval) {
        val directions=LoanPendingApprovalFragmentDirections.actionPayableLoansFragmentToUpdateLoanFragment(lists)
        findNavController().navigate(directions)
    }

}