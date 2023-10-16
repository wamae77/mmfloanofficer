package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.AssesementCustomerDetailsAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerDetailsAssessmentBinding
import com.deefrent.rnd.fieldapp.models.DashboardItem
import com.deefrent.rnd.fieldapp.utils.callbacks.DashboardCallBack
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.onInfoDialogUp
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails.CustomerInfoViewModel
import com.deefrent.rnd.fieldapp.viewModels.CustomerIDLookUpViewModel


class CustomerDetailsAssessmentFragment : Fragment(), DashboardCallBack {
    private lateinit var binding: FragmentCustomerDetailsAssessmentBinding
    private lateinit var assesementAdapter: AssesementCustomerDetailsAdapter
    private val idLookUpViewModel: CustomerIDLookUpViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private val itemsList = ArrayList<DashboardItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerDetailsAssessmentBinding.inflate(layoutInflater)
        /* idLookUpViewModel.iDLookUpData.observe(viewLifecycleOwner){
             val idLookUpDTO=IDLookUpDTO()
             idLookUpDTO.idNumber=it.idNumber
             viewmodel.getCustomerFullDetails(idLookUpDTO)
         }*/
        //viewmodel.getCustomerFullDetails()
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v).navigateUp()
            }
            viewmodel.responseStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            rvAssessMent.makeGone()
                            progressr.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            rvAssessMent.makeVisible()
                            progressr.mainPBar.makeGone()
                        }
                        else -> {
                            rvAssessMent.makeVisible()
                            progressr.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialogUp(viewmodel.statusMessage.value)
                        }
                        else -> {
                            viewmodel.stopObserving()
                            onInfoDialogUp(getString(R.string.error_occurred))
                        }
                    }
                }
            }


        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assesementAdapter = AssesementCustomerDetailsAdapter(itemsList, this)
        binding.rvAssessMent.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvAssessMent.adapter = assesementAdapter
        prepareDashboardItems()
        handleBackButton()
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                    //findNavController().navigate(R.id.customerDetailsAssessmentFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun prepareDashboardItems(): ArrayList<DashboardItem> {
        val dashboardItemNames = listOf(
            requireContext().getString(R.string.basic_info),
            requireContext().getString(R.string.add_details),
            requireContext().getString(R.string.bs_details),
            requireContext().getString(R.string.bs_address),
            requireContext().getString(R.string.guarantors),
            requireContext().getString(R.string.residential_details),
            requireContext().getString(R.string.collaterals),
            requireContext().getString(R.string.other_borrowings),
            requireContext().getString(R.string.nok_details),
        )
        for (dashBoardItemName in dashboardItemNames.indices) {
            val dashboardItem = DashboardItem(dashboardItemNames[dashBoardItemName])
            itemsList.add(dashboardItem)
        }
        assesementAdapter.notifyDataSetChanged()
        return itemsList;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemsList.clear()
    }

    override fun onItemSelected(pos: Int) {
        when (pos) {
            0 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_customerDetailsAssessmentFragment)
            }
            1 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateAdditionalDetailsFragment)
            }
            2 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateBusinesDetailsFragment)
            }
            3 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateBusinessAddressFragment)
            }

            4 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateGuarantorsFragment)
            }
            5 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateResidentialDetailsFragment)
            }
            6 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateCollateralsFragment)
            }
            7 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateBorrowingsFragment)
            }
            8 -> {
                findNavController().navigate(R.id.action_customerDFragment_to_updateNextOfKinFragment)
            }
        }
    }

}