package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.LoanHistoryAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentLoanHistoryBinding
import com.deefrent.rnd.fieldapp.network.models.LoanHistory
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible


class LoanHistoryFragment : Fragment() {
    private lateinit var binding:FragmentLoanHistoryBinding
    private val items: ArrayList<LoanHistory> = ArrayList()
    private lateinit var loanHistoryAdapter: LoanHistoryAdapter
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(LoanLookUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoanHistoryBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            loanHistoryAdapter = LoanHistoryAdapter(items,this@LoanHistoryFragment)
            rvHistory.layoutManager =LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvHistory.adapter = loanHistoryAdapter
            viewmodel.loanLookUpData.observe(viewLifecycleOwner){
                Log.d("TAG", "onView history$it: ")
                if (it.loanHistory.isNotEmpty()) {
                    mainLayout.makeVisible()
                    note.makeGone()
                    items.clear()
                    items.addAll(it.loanHistory)
                }else{
                    note.makeVisible()
                    mainLayout.makeGone()
                }
                loanHistoryAdapter.notifyDataSetChanged()

            }
        }
    }

    fun navigateToLoanHistoryDetailsFragment(loanHistory: LoanHistory){
        viewmodel.loanHistoryItem=loanHistory
        findNavController().navigate(R.id.action_loanHistoryFragment_to_loanHistoryDetailsFragment)
    }

}