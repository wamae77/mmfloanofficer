package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deefrent.rnd.fieldapp.R

class SummaryCustomerDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SummaryCustomerDetailsFragment()
    }

    private lateinit var viewModel: SummaryCustomerDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.summary_customer_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SummaryCustomerDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}