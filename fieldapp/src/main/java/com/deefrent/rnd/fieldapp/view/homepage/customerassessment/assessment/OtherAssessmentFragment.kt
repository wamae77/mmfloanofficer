package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.IncompleteAsessmentAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOtherAssessmentBinding
import com.deefrent.rnd.fieldapp.network.models.CustomerAssessmentData
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerAssessmentDataCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class OtherAssessmentFragment : Fragment(), CustomerAssessmentDataCallBack {
    private lateinit var binding: FragmentOtherAssessmentBinding
    var minCollateral: String = ""
    var maxColateral: String = ""
    var miniGuarantor: String = ""
    var maxGuarantor: String = ""
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private lateinit var incompleteAdapter: IncompleteAsessmentAdapter
    private var arrayList: ArrayList<CustomerAssessmentData> = arrayListOf()
    private var displayList: ArrayList<CustomerAssessmentData> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherAssessmentBinding.inflate(layoutInflater)
        incompleteAdapter = IncompleteAsessmentAdapter(displayList, this)
        binding.rvIncomplete.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvIncomplete.adapter = incompleteAdapter

        searchCustomer()
        getIncompleteAssessments()
        binding.btnRefresh.setOnClickListener {
            getIncompleteAssessments()
        }
        return binding.root
    }

    private fun getIncompleteAssessments() {
        if (isNetworkAvailable(requireContext())) {
            binding.search.makeGone()
            binding.rvIncomplete.makeGone()
            binding.tvError.makeGone()
            binding.btnRefresh.makeGone()
            viewmodel.getCustomerIncompleteData()
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
        viewmodel.incompleteData.observe(viewLifecycleOwner) { incomplete ->
            minCollateral = incomplete.data.minCollaterals
            maxColateral = incomplete.data.maxCollaterals
            miniGuarantor = incomplete.data.minGuarantors
            maxGuarantor = incomplete.data.maxGuarantors
            displayList.clear()
            arrayList.clear()
            displayList.addAll(incomplete.data.customerAssData)
            incompleteAdapter.notifyDataSetChanged()
            arrayList.addAll(incomplete.data.customerAssData)
            if (incomplete.data.customerAssData.isEmpty()) {
                binding.search.makeGone()
                binding.rvIncomplete.makeGone()
                binding.tvError.makeVisible()
                binding.tvError.text =
                    "No pending assessments at the moment\nPlease try again later"
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
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.search.makeGone()
                        binding.rvIncomplete.makeGone()
                        binding.tvError.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Fetching incomplete assessments..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.search.makeVisible()
                        binding.rvIncomplete.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.search.makeGone()
                        binding.rvIncomplete.makeGone()
                        binding.btnRefresh.makeVisible()
                        binding.tvError.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.status.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewmodel.incompleteData.observe(viewLifecycleOwner) { incomplete ->
                            minCollateral = incomplete.data.minCollaterals
                            maxColateral = incomplete.data.maxCollaterals
                            miniGuarantor = incomplete.data.minGuarantors
                            maxGuarantor = incomplete.data.maxGuarantors
                            displayList.clear()
                            arrayList.clear()
                            displayList.addAll(incomplete.data.customerAssData)
                            incompleteAdapter.notifyDataSetChanged()
                            arrayList.addAll(incomplete.data.customerAssData)
                            if (incomplete.data.customerAssData.isEmpty()) {
                                binding.search.makeGone()
                                binding.rvIncomplete.makeGone()
                                binding.tvError.makeVisible()
                                binding.tvError.text =
                                    "No pending assessments at the moment\nPlease try again later"
                                binding.btnRefresh.makeVisible()
                            } else {
                                binding.tvError.makeGone()
                                binding.search.makeVisible()
                                binding.rvIncomplete.makeVisible()
                                binding.btnRefresh.makeGone()
                            }
                        }
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        viewmodel.stopObserving()
                        binding.tvError.text = viewmodel.statusMessage.value
                        binding.tvError.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.search.makeGone()
                        binding.rvIncomplete.makeGone()
                        //onInfoDialogUp(viewmodel.statusMessage.value)
                    }
                    else -> {
                        viewmodel.stopObserving()
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
                        if (it.firstName.toLowerCase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "No customer found"
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

    override fun onItemSelected(pos: Int, items: CustomerAssessmentData) {
        Constants.isFromLocal = false
        Log.e("TAG", "assessmentPercentage:${items.assessmentPercentage} ")
        viewmodel.parentId.postValue(items.idNumber)
        viewmodel._customerHouseholdMember.postValue(items.householdMembers)
        viewmodel.customerGuarantor.postValue(items.guarantorInfo)
        viewmodel._customerCollateral.postValue(items.collateralInfo)
        viewmodel.customerBorrowings.postValue(items.otherBorrowings)
        viewmodel.incompleteItems.postValue(items)
        val custDoc = items.documents.map {
            AssessCustomerDocsEntity(
                0,
                items.idNumber,
                it.docTypeCode,
                it.channelGeneratedCode,
                it.url
            )
        }
        val coll = items.collateralInfo.map { collateralInfo ->
            AssessCollateral(
                0,
                items.idNumber,
                collateralInfo.assetTypeId,
                collateralInfo.assetTypeName,
                collateralInfo.estimatedValue,
                collateralInfo.model,
                collateralInfo.name,
                collateralInfo.serialNumber, collateralInfo.channelGeneratedCode
            )
        }
        val gua = items.guarantorInfo.map { guarantorInfo ->
            AssessGuarantor(
                0,
                guarantorInfo.idNumber,
                items.idNumber,
                guarantorInfo.name,
                guarantorInfo.phone,
                guarantorInfo.relationshipId,
                guarantorInfo.relationshipName,
                guarantorInfo.address, guarantorInfo.channelGeneratedCode
            )
        }
        val borr = items.otherBorrowings.map { otherBorrowings ->
            AssessBorrowing(
                0,
                otherBorrowings.institutionName,
                items.idNumber,
                otherBorrowings.amount,
                otherBorrowings.amountPaidToDate,
                otherBorrowings.statusId, otherBorrowings.status,
                otherBorrowings.monthlyInstallmentPaid
            )
        }
        val houseHold = items.householdMembers.map { householdMembers ->
            AssessHouseholdMemberEntity(
                0,
                items.idNumber,
                householdMembers.fullName,
                householdMembers.incomeOrFeesPaid,
                householdMembers.natureOfActivity,
                householdMembers.occupation,
                householdMembers.occupationId,
                householdMembers.relationShip,
                householdMembers.relationshipId
            )
        }
        Log.d("TAG", "onItemSelected: ${items.assessmentPercentage}")
        val assessEntity = AssessCustomerEntity()
        assessEntity.apply {
            isComplete = true
            hasFinished = false
            isProcessed = false
            minimumCollateral = minCollateral
            maximumColateral = maxColateral
            minimumGuarantor = miniGuarantor
            maximumGuarantor = maxGuarantor
            phone = items.phone
            idNumber = items.idNumber
            firstName = items.firstName
            lastName = items.lastName
            gender = items.gender
            alsoKnownAs = items.alsoKnownAs
            genderId = items.genderId.toString()
            customerNumber = items.customerNumber
            dob = items.dob
            emailAddress = items.email
            spouseName = items.spouseName
            spousePhone = items.spousePhone
            educationLevel = items.educationLevel
            educationLevelId = items.educationLevelId
            identifier = items.identifier
            identifierId = items.identifierId
            numberOfChildren = items.numberOfChildren
            numberOfDependants = items.numberOfDependants
            empStatus = items.empStatus
            empStatusId = items.empStatusId
            businessTypeId = items.businessTypeId
            businessTypeName = items.businessTypeName
            economicFactorId = items.economicFactorId
            economicFactorName = items.economicFactorName
            nameOfIndustry = items.nameOfIndustry
            establishmentTypeId = items.establishmentTypeId
            establishmentTypeName = items.establishmentTypeName
            yearsInBusiness = items.yearsInBusiness
            businessPhysicalAddress = items.businessPhysicalAddress
            businessDistrictId = items.businessDistrictId
            businessDistrictName = items.businessDistrictName
            businessVillageId = items.businessVillageId
            businessVillageName = items.businessVillageName
            businessPhone = items.businessPhone
            numberOfEmployees = items.numberOfEmployees
            resPhysicalAddress = items.resAddress
            resLivingSince = items.resLivingSince
            resAccomodation = items.resAccommodationStatusName
            resAccomadationStatus = items.resAccommodationStatusId
            kinRelationshipId = items.kinRelationshipId
            kinRelationship = items.kinRelationship
            kinFirstName = items.kinFirstName
            kinLastName = items.kinLastName
            kinPhoneNumber = items.kinPhone
            kinIdentityTypeId = items.kinIdentityTypeId
            kinIdentityType = items.kinIdentityType
            kinIdNumber = items.kinIdentityNumber
            netSalary = items.incomeNetSalary
            grossSalary = items.incomeOwnSalary
            totalSales = items.incomeTotalSales
            profit = items.incomeProfit
            rentalIncome = items.incomeRental
            donation = items.incomeRemittanceOrDonation
            otherIncome = items.otherIncomes
            expenseRentals = items.expenseRentals
            expenseFood = items.expenseFood
            expenseSchoolFees = items.expenseSchoolFees
            expenseTransport = items.expenseTransport
            expenseMedicalAidOrContributions = items.expenseMedicalAidOrContributions
            otherExpenses = items.otherExpenses
            subBranchId = items.areaId.toString()
            subBranch = items.areaName
            assessmentPercentage = items.assessmentPercentage
            customerId = items.customerId.toString()
            Log.e("TAG", "onItemSelectedsss: ${items.assessmentPercentage}")
        }
        Log.i("TAG", "onItemSelectedasss: ${Gson().toJson(assessEntity.assessmentPercentage)}")
        GlobalScope.launch(Dispatchers.IO) {
            viewmodel.insertAssessmentData(assessEntity, custDoc, coll, gua, borr, houseHold)
            Log.i("TAG", "onItemSelectedper: ${Gson().toJson(assessEntity.assessmentPercentage)}")
        }
        findNavController().navigate(R.id.action_incompleteAssesmentFragment_to_assessCustomerDetailsFragment)
    }


}