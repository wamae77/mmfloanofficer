package com.deefrent.rnd.fieldapp.view.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.TellerAccountAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentTellerStatementsBinding
import com.deefrent.rnd.fieldapp.network.models.TellerAccountStatmentData
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.ProfileViewModel


class TellerStatementsFragment : Fragment() {
    private lateinit var binding: FragmentTellerStatementsBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var telletAdapter:TellerAccountAdapter
    private var displayList: ArrayList<TellerAccountStatmentData> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTellerStatementsBinding.inflate(layoutInflater)
        telletAdapter = TellerAccountAdapter(displayList,requireContext())
        binding.rvTeller.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvTeller.adapter = telletAdapter
        viewmodel.getTellerStatement()
        getIncompleteAssessments()
        binding.btnRefresh.setOnClickListener {
            viewmodel.getTellerStatement()
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        return binding.root
    }

    private fun getIncompleteAssessments() {
        if (isNetworkAvailable(requireContext())) {
            binding.CLView.makeGone()
            binding.noNewRequest.makeGone()
            binding.btnRefresh.makeGone()
            observeViewModel()
        } else {
            binding.CLView.makeGone()
            binding.noNewRequest.makeVisible()
            binding.btnRefresh.makeVisible()
            binding.noNewRequest.text = "Please check your internet connection and try again!"
        }
    }

    private fun observeViewModel() {
        binding.CLView.makeGone()
        binding.noNewRequest.makeGone()
        binding.btnRefresh.makeGone()
        viewmodel.tellerStatement.observe(viewLifecycleOwner) {
            displayList.clear()
            displayList.addAll(it)
            telletAdapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                binding.CLView.makeGone()
                binding.noNewRequest.makeVisible()
                binding.noNewRequest.text="There are no teller account statement at the moment"
                binding.btnRefresh.makeVisible()
            } else {
                binding.noNewRequest.makeGone()
                binding.CLView.makeVisible()
                binding.btnRefresh.makeGone()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.CLView.makeGone()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text="Fetching teller account statement..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.CLView.makeVisible()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.CLView.makeGone()
                        binding.noNewRequest.makeVisible()
                        binding.noNewRequest.text="Oops! Error occurred while fetching the account statement, please try again later"
                        binding.btnRefresh.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewmodel.tellerStatement.observe(viewLifecycleOwner) {
                            displayList.clear()
                            displayList.addAll(it)
                            telletAdapter.notifyDataSetChanged()
                            if (it.isEmpty()) {
                                binding.CLView.makeGone()
                                binding.noNewRequest.makeVisible()
                                binding.noNewRequest.text="There are no teller account statement at the moment"
                                binding.btnRefresh.makeVisible()
                            } else {
                                binding.noNewRequest.makeGone()
                                binding.CLView.makeVisible()
                                binding.btnRefresh.makeGone()
                            }
                        }
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        viewmodel.stopObserving()
                        binding.noNewRequest.text = viewmodel.statusMessage.value
                        binding.noNewRequest.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.CLView.makeGone()
                    }
                    else -> {
                        viewmodel.stopObserving()
                        binding.CLView.makeGone()
                        binding.noNewRequest.makeVisible()
                        binding.noNewRequest.text="Oops! Error occurred while fetching the account statement, please try again later"
                        binding.btnRefresh.makeVisible()
                        //onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }
    }



}