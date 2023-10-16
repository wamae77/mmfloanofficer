package com.deefrent.rnd.fieldapp.view.homepage.incompleteRegistration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.IncompleteRegAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOtherRegistartionBinding
import com.deefrent.rnd.fieldapp.network.models.CustomerIncompleteData
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.callbacks.IncompleteRegCallBack
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.OnboardCustomerViewModel
import java.util.*
import kotlin.collections.ArrayList


class OtherRegistartionFragment : Fragment(), IncompleteRegCallBack {
    private val onBoardViewmodel: OnboardCustomerViewModel by activityViewModels()
    var minCollateral: String = ""
    var maxColateral: String = ""
    var miniGuarantor: String = ""
    var maxGuarantor: String = ""
    private lateinit var binding: FragmentOtherRegistartionBinding
    private lateinit var viewModel: IncompleteRegDashboardViewModel
    private lateinit var incompleteAdapter: IncompleteRegAdapter
    private var arrayList: ArrayList<CustomerIncompleteData> = arrayListOf()
    private var displayList: ArrayList<CustomerIncompleteData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherRegistartionBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(requireActivity())[IncompleteRegDashboardViewModel::class.java]
        incompleteAdapter = IncompleteRegAdapter(displayList, this)
        binding.rvIncomplete.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvIncomplete.adapter = incompleteAdapter
        searchCustomer()
        getIncompleteRegistrations()
        binding.btnRefresh.setOnClickListener {
            getIncompleteRegistrations()
        }

        return binding.root
    }

    private fun getIncompleteRegistrations() {
        if (isNetworkAvailable(requireContext())) {
            binding.search.makeGone()
            binding.rvIncomplete.makeGone()
            binding.tvError.makeGone()
            binding.btnRefresh.makeGone()
            viewModel.getIncompleteData()
           observeViewModel()
        } else {
            binding.search.makeGone()
            binding.rvIncomplete.makeGone()
            binding.tvError.makeVisible()
            binding.btnRefresh.makeVisible()
            binding.tvError.text = "Please check your internet connection and try again!"
        }
    }

    private fun observeViewModel() {
        binding.rvIncomplete.makeGone()
        binding.tvError.makeGone()
        binding.btnRefresh.makeGone()
        binding.search.makeGone()
        viewModel.incompleteData.observe(viewLifecycleOwner) {incomplete->
            minCollateral=incomplete.data.minCollaterals
            maxColateral=incomplete.data.maxCollaterals
            miniGuarantor=incomplete.data.minGuarantors
            maxGuarantor=incomplete.data.maxGuarantors
            displayList.clear()
            arrayList.clear()
            displayList.addAll(incomplete.data.incompleteItems)
            incompleteAdapter.notifyDataSetChanged()
            arrayList.addAll(incomplete.data.incompleteItems)
            if (incomplete.data.incompleteItems.isEmpty()) {
                binding.search.makeGone()
                binding.rvIncomplete.makeGone()
                binding.tvError.makeGone()
                binding.tvError.text="No pending registrations at the moment\nPlease try again later"
                binding.btnRefresh.makeVisible()
            } else {
                binding.tvError.makeGone()
                binding.search.makeVisible()
                binding.rvIncomplete.makeVisible()
                binding.btnRefresh.makeGone()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.btnRefresh.makeGone()
                        binding.rvIncomplete.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.rvIncomplete.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.rvIncomplete.makeGone()
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewModel.status.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewModel.incompleteData.observe(viewLifecycleOwner) {incomplete->
                            minCollateral=incomplete.data.minCollaterals
                            maxColateral=incomplete.data.maxCollaterals
                            miniGuarantor=incomplete.data.minGuarantors
                            maxGuarantor=incomplete.data.maxGuarantors
                            Log.d("TAG", "incompleteData:$it ")
                            displayList.clear()
                            arrayList.clear()
                            displayList.addAll(incomplete.data.incompleteItems)
                            incompleteAdapter.notifyDataSetChanged()
                            arrayList.addAll(incomplete.data.incompleteItems)
                            if (incomplete.data.incompleteItems.isEmpty()) {
                                binding.search.makeGone()
                                binding.rvIncomplete.makeGone()
                                binding.tvError.makeVisible()
                                binding.tvError.text="No pending registrations at the moment"
                                binding.btnRefresh.makeGone()
                            } else {
                                binding.tvError.makeGone()
                                binding.search.makeVisible()
                                binding.rvIncomplete.makeVisible()
                                binding.btnRefresh.makeGone()
                            }
                        }
                        viewModel.stopObserving()
                    }
                    0 -> {
                        viewModel.stopObserving()
                        binding.tvError.text = viewModel.statusMessage.value
                        binding.tvError.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.search.makeGone()
                        binding.rvIncomplete.makeGone()
                        //onInfoDialogUp(viewmodel.statusMessage.value)
                    }
                    else -> {
                        viewModel.stopObserving()
                        binding.tvError.text = getString(R.string.error_occurred)
                        binding.tvError.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.search.makeGone()
                        binding.rvIncomplete.makeGone()
                        //onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }

    }


    private fun searchCustomer() {
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    binding.tvError.visibility = View.GONE
                    binding.btnRefresh.visibility = View.GONE
                    val search = newText.toLowerCase(Locale.US)
                    arrayList.forEach {
                        if (it.fullName.toLowerCase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text="No customer found"
                        binding.btnRefresh.visibility = View.VISIBLE
                    } else {
                        binding.tvError.visibility = View.GONE
                        binding.btnRefresh.visibility = View.GONE
                    }
                    binding.rvIncomplete.adapter?.notifyDataSetChanged()
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.btnRefresh.visibility = View.GONE
                    displayList.clear()
                    displayList.addAll(arrayList)
                    binding.rvIncomplete.adapter?.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    override fun onItemSelected(pos: Int, items: CustomerIncompleteData) {
        onBoardViewmodel.apply {
         //   isFromIncompleteScreen.postValue(true)
            cIdNumber.postValue(items.idNumber)
        }
        val coll = items.collateralInfo.map {
            Collateral(
                0,
                items.idNumber,
                it.assetTypeId,
                it.assetTypeName,
                it.estimatedValue,
                it.model,
                it.name,
                it.serialNumber,
                it.channelGeneratedCode,
            true)
        }
        val gua = items.guarantorInfo.map {
            Guarantor(0,
                it.idNumber,
                items.idNumber,
                it.name,
                it.phone,
                it.relationshipId,
                it.relationshipName,
                it.address,
                it.channelGeneratedCode
            )
        }
        val custDoc=items.documents.map {
            CustomerDocsEntity(0,items.idNumber,it.docTypeCode,it.channelGeneratedCode,it.url)
        }
        val borr = items.otherBorrowings.map {
            OtherBorrowing(
                0,
                it.institutionName,
                items.idNumber,
                it.amount,
                it.amountPaidToDate,
                it.statusId,
                it.monthlyInstallmentPaid
            )
        }
        val members = items.householdMembers.map {
            HouseholdMemberEntity(
                0,
                items.idNumber,
                it.fullName,
                it.incomeOrFeesPaid,
                it.memberId,
                it.natureOfActivity,
                it.occupationId,
                it.occupation,
                it.relationshipId,
                it.relationShip
            )
        }

        val customerDetailsEntity = CustomerDetailsEntity()
        customerDetailsEntity.apply {
            nationalIdentity = items.idNumber
            isComplete=true
            isProcessed=true
            hasFinished = false
            completion=items.isCompletion
            minimumCollateral=minCollateral
            maximumColateral=maxColateral
            minimumGuarantor=miniGuarantor
            maximumGuarantor=maxGuarantor
            bsDistrictId = items.businessDistrictId
            bsDistrict = items.businessDistrictName
            customerNumber = items.customerNumber
            bsEconomicFactorId = items.economicFactorId
            bsEconomicFactor = items.economicFactorName
            bsEstablishmentType = items.establishmentTypeName
            bsEstablishmentTypeId = items.establishmentTypeId
            bsNameOfIndustry = items.nameOfIndustry
            bsNumberOfEmployees = items.numberOfEmployees
            bsPhoneNumber = items.businessPhone
            bsPhysicalAddress = items.businessPhysicalAddress
            bsTypeOfBusiness = items.businessTypeName
            bsTypeOfBusinessId = items.businessTypeId
            bsVillageId = items.businessVillageId
            bsVillage = items.businessVillageName
            alias = items.alsoKnownAs
            bsYearsInBusiness = items.yearsInBusiness
            dob = items.dob
            educationLevelId = items.educationLevelId
            educationLevel = items.educationLevel
            email = items.email
            employmentStatusId = items.empStatusId
            employmentStatus = items.empStatus
            firstName = items.firstName
            genderId = items.genderId
            genderName = items.gender
            howClientKnewMmfId = items.identifierId
            howClientKnewMmf = items.identifierId
            kinFirstName = items.kinFirstName
            kinIdNumber = items.kinIdentityNumber
            kinIdentityTypeId = items.kinIdentityTypeId
            kinIdentityType = items.kinIdentityType
            kinLastName = items.kinLastName
            kinPhoneNumber = items.kinPhone
            kinRelationshipId = items.kinRelationshipId
            kinRelationship = items.kinRelationship
            lastName = items.lastName
            numberOfChildren = items.numberOfChildren
            numberOfDependants = items.numberOfDependants
            phone = items.phone
            resAccommodationStatusId = items.resAccommodationStatusId
            resAccommodationStatus = items.resAccommodationStatusName
            resLivingSince = items.resLivingSince
            resPhysicalAddress = items.resAddress
            spouseName = items.spouseName
            spousePhone = items.spousePhone

            /**Expenses*/
            rentalsExpenses = items.expenseRentals
            food = items.expenseFood
            schoolFees = items.expenseSchoolFees
            transport = items.expenseTransport
            medicalAidOrContributions = items.expenseMedicalAidOrContributions
            otherExpenses = items.otherExpenses
            /**income*/
            netSalary = items.incomeNetSalary
            grossSalary = items.incomeOwnSalary
            totalSales = items.incomeTotalSales
            profit = items.incomeProfit
            rIncome = items.incomeRental
            donation = items.incomeRemittanceOrDonation
            otherIncome = items.otherIncomes
        }

        onBoardViewmodel.customerEntityData.postValue(customerDetailsEntity)
        onBoardViewmodel.insertCustomerFullDetails(customerDetailsEntity,gua,coll,borr,members,custDoc)
        val directions =
            IncompleteRegDashboardFragmentDirections.actionIncompleteRegDashboardFragmentToOnboardCustomerDetailsFragment(
                3
            )
        findNavController().navigate(directions)
    }


}