package com.deefrent.rnd.fieldapp.ui.incompleteOnBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.adapters.IncompleteListItemAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentIncompleteOnBoardingBinding
import com.deefrent.rnd.fieldapp.models.incompleteOnBoarding.IncompleteProcessListItem
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.ui.onboardAccount.OnboardAccountSharedViewModel
import com.deefrent.rnd.fieldapp.ui.onboardAccount.OnboardMerchantSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class IncompleteOnBoardingFragment : Fragment() {
    //
    private var _binding: FragmentIncompleteOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val incompleteItemsList = ArrayList<IncompleteProcessListItem>()
    private lateinit var incompleteListAdapter: IncompleteListItemAdapter
    private lateinit var individualAccountDetailsList: ArrayList<IndividualAccountDetails>
    private lateinit var merchantAgentDetailsList: ArrayList<MerchantAgentDetails>
    private lateinit var roomDBViewModel: RoomDBViewModel
    private lateinit var fieldAppDatabase: FieldAppDatabase
    private val onboardAccountSharedViewModel: OnboardAccountSharedViewModel by activityViewModels()
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncompleteOnBoardingBinding.inflate(inflater, container, false)
        val view = binding.root

        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        individualAccountDetailsList = ArrayList()
        merchantAgentDetailsList = ArrayList()
        incompleteListAdapter = IncompleteListItemAdapter(
            incompleteItemsList,
            requireContext(),
            this@IncompleteOnBoardingFragment
        )
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvIncompleteOnBoarding.layoutManager = linearLayoutManager
        binding.rvIncompleteOnBoarding.adapter = incompleteListAdapter
        populateRecyclerView()
        return view
    }

    fun deleteIncompleteCustomer(RoomDBId: Int, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Record?")
        builder.setMessage("You are about to delete the OnBoarding Process. Do you wish to continue?")

        builder.setPositiveButton("YES") { _, _ ->
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(roomDBViewModel.deleteCustomerRecord(RoomDBId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    incompleteItemsList.removeAt(position)
                    incompleteListAdapter.notifyItemRemoved(position)
                    incompleteListAdapter.notifyItemRangeChanged(
                        position,
                        incompleteListAdapter.itemCount
                    )
                    compositeDisposable.dispose()
                })
        }

        builder.setNegativeButton("NO") { _, _ ->
        }
        builder.show()
    }

    fun deleteIncompleteMerchantAgent(RoomDBId: Int, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Record?")
        builder.setMessage("You are about to delete the OnBoarding Process. Do you wish to continue?")

        builder.setPositiveButton("YES") { _, _ ->
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(roomDBViewModel.deleteMerchantAgentRecord(RoomDBId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    incompleteItemsList.removeAt(position)
                    incompleteListAdapter.notifyItemRemoved(position)
                    incompleteListAdapter.notifyItemRangeChanged(
                        position,
                        incompleteListAdapter.itemCount
                    )
                    compositeDisposable.dispose()
                })
        }

        builder.setNegativeButton("NO") { _, _ ->
        }
        builder.show()
    }

    fun fetchCustomerDetails(RoomDBId: Int) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.fetchIncompleteIndividualAccount(RoomDBId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { incompleteIndividualAccount ->
                loginSessionSharedViewModel.setIsFromIncompleteDialog(true)
                when (incompleteIndividualAccount.lastStep) {
                    "IndividualAccountDetails" -> {
                        onboardAccountSharedViewModel.apply {
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setPersonalAccountTypeId(incompleteIndividualAccount.personalAccountTypeId)
                            setPhoneNo(incompleteIndividualAccount.phoneNo)
                            setKCBBranchId(incompleteIndividualAccount.kcbBranchId)
                            setRoomDBId(incompleteIndividualAccount.id)
                            setIdNumber(incompleteIndividualAccount.idNumber)
                            setIdType(incompleteIndividualAccount.idType)
                            setSurname(incompleteIndividualAccount.surname)
                            setFirstName(incompleteIndividualAccount.firstName)
                            setLastName(incompleteIndividualAccount.lastName)
                            setDob(incompleteIndividualAccount.dob)
                            setGender(incompleteIndividualAccount.gender)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                        }

                    }
                    "CaptureIDPhotoFragment" -> {
                        onboardAccountSharedViewModel.apply {
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setPersonalAccountTypeId(incompleteIndividualAccount.personalAccountTypeId)
                            setPhoneNo(incompleteIndividualAccount.phoneNo)
                            setKCBBranchId(incompleteIndividualAccount.kcbBranchId)
                            setRoomDBId(incompleteIndividualAccount.id)
                            setIdNumber(incompleteIndividualAccount.idNumber)
                            setIdType(incompleteIndividualAccount.idType)
                            setSurname(incompleteIndividualAccount.surname)
                            setFirstName(incompleteIndividualAccount.firstName)
                            setLastName(incompleteIndividualAccount.lastName)
                            setDob(incompleteIndividualAccount.dob)
                            setGender(incompleteIndividualAccount.gender)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                            setFrontIdPath(incompleteIndividualAccount.frontIdPath)
                            setBackIdPath(incompleteIndividualAccount.backIdPath)
                        }
                    }
                    "CaptureCustomerPhotoFragment" -> {
                        onboardAccountSharedViewModel.apply {
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setPersonalAccountTypeId(incompleteIndividualAccount.personalAccountTypeId)
                            setPhoneNo(incompleteIndividualAccount.phoneNo)
                            setKCBBranchId(incompleteIndividualAccount.kcbBranchId)
                            setRoomDBId(incompleteIndividualAccount.id)
                            setIdNumber(incompleteIndividualAccount.idNumber)
                            setIdType(incompleteIndividualAccount.idType)
                            setSurname(incompleteIndividualAccount.surname)
                            setFirstName(incompleteIndividualAccount.firstName)
                            setLastName(incompleteIndividualAccount.lastName)
                            setDob(incompleteIndividualAccount.dob)
                            setGender(incompleteIndividualAccount.gender)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                            setFrontIdPath(incompleteIndividualAccount.frontIdPath)
                            setBackIdPath(incompleteIndividualAccount.backIdPath)
                            setPassportPhotoPath(incompleteIndividualAccount.passportPhotoPath)
                        }
                    }
                }
                compositeDisposable.dispose()
            })
    }

    fun fetchMerchantAgentDetails(RoomDBId: Int) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.fetchIncompleteMerchantAgentAccount(RoomDBId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { incompleteIndividualAccount ->
                loginSessionSharedViewModel.setIsFromIncompleteDialog(true)
                when (incompleteIndividualAccount.lastStep) {
                    "MerchantAgentDetails" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                        }
                    }
                    "LiquidationDetailsFragment" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLiquidationTypeId(incompleteIndividualAccount.liquidationTypeId)
                            setLiquidationRate(incompleteIndividualAccount.liquidationRate)
                            setBankCode(incompleteIndividualAccount.bankCode!!)
                            setBranchCode(incompleteIndividualAccount.branchCode!!)
                            setAccountName(incompleteIndividualAccount.accountName!!)
                            setAccountNumber(incompleteIndividualAccount.accountNumber!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                        }
                    }
                    "PhysicalAddressFragment" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLiquidationTypeId(incompleteIndividualAccount.liquidationTypeId)
                            setLiquidationRate(incompleteIndividualAccount.liquidationRate)
                            setBankCode(incompleteIndividualAccount.bankCode!!)
                            setBranchCode(incompleteIndividualAccount.branchCode!!)
                            setAccountName(incompleteIndividualAccount.accountName!!)
                            setAccountNumber(incompleteIndividualAccount.accountNumber!!)
                            setTownName(incompleteIndividualAccount.townName!!)
                            setStreetName(incompleteIndividualAccount.streetName!!)
                            setBuildingName(incompleteIndividualAccount.buldingName!!)
                            setRoomNumber(incompleteIndividualAccount.roomNo!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                        }
                    }
                    "AgentPersonalDetailsFragment" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLiquidationTypeId(incompleteIndividualAccount.liquidationTypeId)
                            setLiquidationRate(incompleteIndividualAccount.liquidationRate)
                            setBankCode(incompleteIndividualAccount.bankCode!!)
                            setBranchCode(incompleteIndividualAccount.branchCode!!)
                            setAccountName(incompleteIndividualAccount.accountName!!)
                            setAccountNumber(incompleteIndividualAccount.accountNumber!!)
                            setTownName(incompleteIndividualAccount.townName!!)
                            setStreetName(incompleteIndividualAccount.streetName!!)
                            setBuildingName(incompleteIndividualAccount.buldingName!!)
                            setRoomNumber(incompleteIndividualAccount.roomNo!!)
                            setMerchantIDNumber(incompleteIndividualAccount.merchantIDNumber!!)
                            setIdType(incompleteIndividualAccount.idType)
                            setMerchantSurname(incompleteIndividualAccount.merchantSurname!!)
                            setMerchantFirstName(incompleteIndividualAccount.merchantFirstName!!)
                            setMerchantLastName(incompleteIndividualAccount.merchantLastName!!)
                            setDob(incompleteIndividualAccount.dob!!)
                            setMerchantGender(incompleteIndividualAccount.merchantGender!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                        }
                    }
                    "AgentIDPhotoFragment" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLiquidationTypeId(incompleteIndividualAccount.liquidationTypeId)
                            setLiquidationRate(incompleteIndividualAccount.liquidationRate)
                            setBankCode(incompleteIndividualAccount.bankCode!!)
                            setBranchCode(incompleteIndividualAccount.branchCode!!)
                            setAccountName(incompleteIndividualAccount.accountName!!)
                            setAccountNumber(incompleteIndividualAccount.accountNumber!!)
                            setTownName(incompleteIndividualAccount.townName!!)
                            setStreetName(incompleteIndividualAccount.streetName!!)
                            setBuildingName(incompleteIndividualAccount.buldingName!!)
                            setRoomNumber(incompleteIndividualAccount.roomNo!!)
                            setMerchantIDNumber(incompleteIndividualAccount.merchantIDNumber!!)
                            setIdType(incompleteIndividualAccount.idType)
                            setMerchantSurname(incompleteIndividualAccount.merchantSurname!!)
                            setMerchantFirstName(incompleteIndividualAccount.merchantFirstName!!)
                            setMerchantLastName(incompleteIndividualAccount.merchantLastName!!)
                            setDob(incompleteIndividualAccount.dob!!)
                            setMerchantGender(incompleteIndividualAccount.merchantGender!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                            setFrontIdPath(incompleteIndividualAccount.frontIdPath!!)
                            setBackIdPath(incompleteIndividualAccount.backIdPath!!)
                        }
                    }
                    "AgentPersonalImagesFragment" -> {
                        onboardMerchantSharedViewModel.apply {
                            setRoomDBId(incompleteIndividualAccount.id)
                            setUserAccountTypeId(incompleteIndividualAccount.userAccountTypeId)
                            setMerchAgentAccountTypeId(incompleteIndividualAccount.merchAgentAccountTypeId)
                            setUserType(incompleteIndividualAccount.userType!!)
                            setBusinessName(incompleteIndividualAccount.businessName!!)
                            setMobileNumber(incompleteIndividualAccount.businessMobileNumber!!)
                            setEmail(incompleteIndividualAccount.businessEmail)
                            setBusinessTypeId(incompleteIndividualAccount.businessTypeId)
                            setBusinessNature(incompleteIndividualAccount.businessNature!!)
                            setLiquidationTypeId(incompleteIndividualAccount.liquidationTypeId)
                            setLiquidationRate(incompleteIndividualAccount.liquidationRate)
                            setBankCode(incompleteIndividualAccount.bankCode!!)
                            setBranchCode(incompleteIndividualAccount.branchCode!!)
                            setAccountName(incompleteIndividualAccount.accountName!!)
                            setAccountNumber(incompleteIndividualAccount.accountNumber!!)
                            setTownName(incompleteIndividualAccount.townName!!)
                            setStreetName(incompleteIndividualAccount.streetName!!)
                            setBuildingName(incompleteIndividualAccount.buldingName!!)
                            setRoomNumber(incompleteIndividualAccount.roomNo!!)
                            setMerchantIDNumber(incompleteIndividualAccount.merchantIDNumber!!)
                            setIdType(incompleteIndividualAccount.idType)
                            setMerchantSurname(incompleteIndividualAccount.merchantSurname!!)
                            setMerchantFirstName(incompleteIndividualAccount.merchantFirstName!!)
                            setMerchantLastName(incompleteIndividualAccount.merchantLastName!!)
                            setDob(incompleteIndividualAccount.dob!!)
                            setMerchantGender(incompleteIndividualAccount.merchantGender!!)
                            setLastStep(incompleteIndividualAccount.lastStep!!)
                            setFrontIdPath(incompleteIndividualAccount.frontIdPath!!)
                            setBackIdPath(incompleteIndividualAccount.backIdPath!!)
                            setCustomerPhotoPath(incompleteIndividualAccount.customerPhotoPath!!)
                            setSignatureDocPath(incompleteIndividualAccount.signatureDocPath!!)
                            setGoodConductPath(incompleteIndividualAccount.goodConductPath!!)
                            setFieldApplicationFormPath(incompleteIndividualAccount.fieldApplicationFormPath!!)
                        }
                    }
                }
                compositeDisposable.dispose()
            })
    }

    private fun populateRecyclerView() {
        incompleteItemsList.clear()
        val compositeDisposable = CompositeDisposable()
        val compositeDisposable2 = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.fetchIncompleteIndividualAccounts()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { incompleteIndividualAccounts ->
                //activityWatchlistBinding.setIsLoading(false)
                individualAccountDetailsList.addAll(incompleteIndividualAccounts)
                if (individualAccountDetailsList.size > 0) {
                    for (incompleteIndividual in individualAccountDetailsList) {
                        val incompleteItem = IncompleteProcessListItem(
                            incompleteIndividual.userType!!,
                            incompleteIndividual.date!!, incompleteIndividual.id
                        )
                        incompleteItemsList.add(incompleteItem)
                    }
                    incompleteListAdapter.notifyDataSetChanged()
                }
                compositeDisposable.dispose()
            })
        compositeDisposable2.add(roomDBViewModel.fetchIncompleteMerchantAgentAccounts()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { incompleteMerchantAgentAccounts ->
                //activityWatchlistBinding.setIsLoading(false)
                merchantAgentDetailsList.addAll(incompleteMerchantAgentAccounts)
                if (merchantAgentDetailsList.size > 0) {
                    for (incompleteMerchantAgent in merchantAgentDetailsList) {
                        val incompleteItem = IncompleteProcessListItem(
                            incompleteMerchantAgent.userType!!,
                            incompleteMerchantAgent.date!!, incompleteMerchantAgent.id
                        )
                        incompleteItemsList.add(incompleteItem)
                    }
                    incompleteListAdapter.notifyDataSetChanged()
                }
                compositeDisposable2.dispose()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IncompleteOnBoardingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}