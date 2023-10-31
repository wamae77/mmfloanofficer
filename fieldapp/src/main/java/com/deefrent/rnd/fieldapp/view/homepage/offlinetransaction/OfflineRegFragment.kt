package com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.OfflineAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOfflineRegBinding
import com.deefrent.rnd.fieldapp.dtos.OnboardCustomerDTO
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.CusomerDetailsEntityWithList
import com.deefrent.rnd.fieldapp.room.entities.CustomerDetailsEntity
import com.deefrent.rnd.fieldapp.room.entities.CustomerDocsEntity
import com.deefrent.rnd.fieldapp.room.repos.CustomerDetailsRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.CustomerEntityCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.OnboardCustomerViewModel
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject


class OfflineRegFragment : BaseDaggerFragment(), CustomerEntityCallBack {
    private lateinit var binding: FragmentOfflineRegBinding
    private lateinit var repository: CustomerDetailsRepository
    private lateinit var customerDetailEntity: CustomerDetailsEntity
    private var customerId = ""
    private var customerPhone = ""

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }
    private lateinit var offlineAdapter: OfflineAdapter
    private var arrayList: ArrayList<CusomerDetailsEntityWithList> = arrayListOf()
    private lateinit var directory: String

//    @Inject
//    lateinit var viewModel: FingerPrintViewModel

    //private var uploadedDocsNames: ArrayList<String> = arrayListOf()
    // private lateinit var documentName:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfflineRegBinding.inflate(layoutInflater)
        directory = Constants.IMAGES_DIR
        offlineAdapter = OfflineAdapter(arrayList, this, this@OfflineRegFragment)
        binding.rvOffile.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvOffile.adapter = offlineAdapter
        viewmodel.customerCompeteList.observe(viewLifecycleOwner) {
            arrayList.clear()
            arrayList.addAll(it)
            offlineAdapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                binding.svLocalAssessment.isRefreshing = false
                binding.noNewRequest.makeVisible()
                binding.rvOffile.makeGone()
            } else {
                binding.svLocalAssessment.isRefreshing = false
                binding.noNewRequest.makeGone()
                binding.rvOffile.makeVisible()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.svLocalAssessment.setOnRefreshListener {
            getLocalCustomerDetails()
        }
    }

    fun deleteOfflineTransaction(listItems: CusomerDetailsEntityWithList, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete ${listItems.customerDetails.firstName.capitalizeWords}'s Record?")
        builder.setMessage("You are about to delete the offline transaction. Please note that this action cannot be undone.Do you wish to continue?")

        builder.setPositiveButton("YES") { _, _ ->
            GlobalScope.launch(Dispatchers.IO) {
                listItems.customerDocs.forEach { uploadedDoc ->
                    Log.d("TAG", "uploadedDoc: $uploadedDoc")
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteCustomerD(listItems.customerDetails)
                }
            }
            //viewmodel.removeRegDataAtPos(position)
            arrayList.removeAt(position)
            offlineAdapter.notifyItemRemoved(position)
            offlineAdapter.notifyDataSetChanged()
            /**notify adapter more than one items has been changed*/
            // incompleteAdapter.notifyItemRangeChanged(pos,incompleteAdapter.itemCount)
            if (arrayList.isEmpty()) {
                binding.noNewRequest.makeVisible()
                binding.rvOffile.makeGone()
            } else {
                binding.noNewRequest.makeGone()
                binding.rvOffile.makeVisible()
            }
        }

        builder.setNegativeButton("NO") { _, _ ->

        }

        builder.show()
    }

    override fun onItemSelected(pos: Int, items: CusomerDetailsEntityWithList) {
        //performApiRequestEnrollWithMultipleImages(items)
        performSyncingOfCusomerDetailsEntityWithList(
            items,
            ""
        )
    }

    private fun performSyncingOfCusomerDetailsEntityWithList(
        items: CusomerDetailsEntityWithList,
        fingerprint_reg_id: String
    ) {
        customerDetailEntity = items.customerDetails
        Log.e("TAG", "initializeUI: ${Gson().toJson(items.customerDetails)}")
        Log.e("TAG", "onItemSelected:Gso${Gson().toJson(items.customerDocs)} ")
        Log.e("TAG", "onItemSelected:Gso${Gson().toJson(items.guarantors)} ")
        if (!items.customerDetails.isProcessed) {
            val guarantorList = items.guarantors.map { guarantorModel ->
                OnboardCustomerDTO.Guarantor(
                    guarantorModel.idNumber,
                    guarantorModel.name,
                    guarantorModel.phone,
                    guarantorModel.relationshipId,
                    guarantorModel.residenceAddress,
                    guarantorModel.guarantorGeneratedUID
                )
            }
            val collateralList = items.collateral.map {
                OnboardCustomerDTO.Collateral(
                    it.assetTypeId,
                    it.estimateValue,
                    it.model,
                    it.name,
                    it.serialNumber,
                    it.collateralGeneratedUID
                )
            }
            val borrowingList = items.otherBorrowing.map { borrowModel ->
                OnboardCustomerDTO.OtherBorrowing(
                    borrowModel.institutionName,
                    borrowModel.amount,
                    borrowModel.totalAmountPaidToDate,
                    borrowModel.status,
                    borrowModel.monthlyInstallmentPaid
                )
            }
            val household = items.householdMember.map { memb ->
                OnboardCustomerDTO.HouseholdMember(
                    memb.fullName,
                    memb.incomeOrFeesPaid,
                    "",
                    memb.natureOfActivity,
                    memb.occupationId,
                    memb.relationshipId
                )
            }
            val onboardCustomerDTO = OnboardCustomerDTO(
                items.customerDetails.subBranchId,
                items.customerDetails.completion,
                items.customerDetails.bsDistrictId,
                items.customerDetails.alias,
                items.customerDetails.customerNumber,
                items.customerDetails.bsEconomicFactorId,
                items.customerDetails.bsEstablishmentTypeId,
                items.customerDetails.bsNameOfIndustry,
                items.customerDetails.bsNumberOfEmployees,
                items.customerDetails.bsPhoneNumber,
                items.customerDetails.bsPhysicalAddress,
                items.customerDetails.bsTypeOfBusinessId,
                items.customerDetails.bsVillageId,
                items.customerDetails.bsYearsInBusiness,
                collateralList,
                items.customerDetails.dob,
                items.customerDetails.educationLevelId,
                items.customerDetails.email,
                items.customerDetails.employmentStatusId,
                items.customerDetails.firstName,
                items.customerDetails.genderId,
                guarantorList,
                items.customerDetails.howClientKnewMmfId,
                items.customerDetails.kinFirstName,
                items.customerDetails.kinIdNumber,
                items.customerDetails.kinIdentityTypeId,
                items.customerDetails.kinLastName,
                items.customerDetails.kinPhoneNumber,
                items.customerDetails.kinRelationshipId,
                items.customerDetails.lastName,
                items.customerDetails.nationalIdentity,
                items.customerDetails.numberOfChildren,
                items.customerDetails.numberOfDependants,
                borrowingList,
                items.customerDetails.phone,
                items.customerDetails.resAccommodationStatusId,
                items.customerDetails.resLivingSince,
                items.customerDetails.resPhysicalAddress,
                items.customerDetails.spouseName,
                items.customerDetails.spousePhone,
                items.customerDetails.food,
                items.customerDetails.medicalAidOrContributions,
                items.customerDetails.rentalsExpenses,
                items.customerDetails.schoolFees,
                items.customerDetails.transport,
                items.customerDetails.otherExpenses,
                items.customerDetails.netSalary,
                items.customerDetails.grossSalary,
                items.customerDetails.totalIncome,
                items.customerDetails.profit,
                items.customerDetails.rIncome,
                items.customerDetails.donation,
                items.customerDetails.otherIncome,
                household
            )
            customerId = items.customerDetails.customerId
            customerPhone = items.customerDetails.phone
            viewmodel.onBoardCustomerFirst(onboardCustomerDTO)
        } else {
            initiateDocumentsUpload(items.customerDocs)
            Log.e("TAG", "onItemSelected:Gs11o${Gson().toJson(items.customerDocs)} ")
            /* if (items.customerDocs.isNotEmpty()) {

             }else{
                 items.customerDocs.forEach { uploadedDoc ->
                     deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                     viewmodel.deleteCustomerD(items.customerDetails)
                 }
             }*/
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        initiateDocumentsUpload(items.customerDocs)
                        viewmodel.stopObserving()
                    }

                    0 -> {
                        viewmodel.stopObserving()
                        onInfoDialog(viewmodel.statusMessage.value)
                    }

                    else -> {
                        viewmodel.stopObserving()
                    }
                }
            }
        }
        viewmodel.responseOnStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {

                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Please wait as we submit the data..."

                    }

                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }

                    GeneralResponseStatus.ERROR -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.responseGStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Uploading documents..."
                    }

                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }

                    GeneralResponseStatus.ERROR -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusDocCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        items.customerDocs.forEach { uploadedDoc ->
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteCustomerD(items.customerDetails)
                        }
                        findNavController().navigate(R.id.onboardCustomerSuccessFragment)

                        viewmodel.stopObserving()
                    }

                    0 -> {
                        items.customerDocs.forEach { uploadedDoc ->
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteCustomerD(items.customerDetails)
                        }
                        toastyErrors("Uploading the documents failed...\nKindly update the documents on customer 360...")
                        findNavController().navigate(R.id.dashboardFragment)
                        viewmodel.stopObserving()
                    }

                    else -> {
                        items.customerDocs.forEach { uploadedDoc ->
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteCustomerD(items.customerDetails)
                        }
                        toastyErrors("Uploading the documents failed...\nKindly update the documents on customer 360...")
                        findNavController().navigate(R.id.dashboardFragment)
                        viewmodel.stopObserving()

                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val customerDetailsDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).customerDetailsDao()
        repository = CustomerDetailsRepository(customerDetailsDao)
    }

    private fun getLocalCustomerDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            val cList = repository.getCompleteOfflineCustomerDetails(true)
            withContext(Dispatchers.Main) {
                viewmodel.customerCompeteList.postValue(cList)
                Log.e("TAG", "initializeUIl: ${Gson().toJson(cList)}")
            }

        }
    }

    private fun initiateDocumentsUpload(customerDocsList: List<CustomerDocsEntity>) {
        /**run within the lifecycle of the view, if its destoryed it stop uploading */
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            var count = 0

            /**incase of an error occured while uplading image to stop uploading*/
            val filteredList = customerDocsList.filter { customerDocsEntity ->
                customerDocsEntity.docPath.isNotEmpty() && !Constants.pattern.containsMatchIn(
                    customerDocsEntity.docPath
                )
            }
            if (filteredList.isNotEmpty()) {
                val lastIndex = filteredList.size.minus(1)
                while (count < filteredList.size) {
                    val customerDocsEntity = filteredList[count]
                    if (customerDocsEntity.docPath.isNotEmpty() && !Constants.pattern.containsMatchIn(
                            customerDocsEntity.docPath
                        )
                    ) {
                        val contextWrapper = ContextWrapper(requireContext())
                        // return a directory in internal storage
                        val directory =
                            contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
                        val location = "${directory.absolutePath}/${customerDocsEntity.docPath}"
                        if (location.isNotEmpty()) {
                            val compressedImages =
                                Compressor.compress(requireContext(), convertPathToFile(location))
                            val file = MultipartBody.Part.createFormData(
                                "file",
                                compressedImages.name, RequestBody.create(
                                    "multipart/form-data".toMediaTypeOrNull(),
                                    compressedImages
                                )
                            )
                            val customerID = RequestBody.create(
                                MultipartBody.FORM,
                                customerId
                            )
                            val docTypeCode =
                                RequestBody.create(MultipartBody.FORM, customerDocsEntity.docCode)
                            val channelGeneratedCode =
                                RequestBody.create(
                                    MultipartBody.FORM,
                                    customerDocsEntity.docGeneratedUID
                                )
                            Log.d("TAG", "initiateDocumentsUpload:${customerDocsEntity.docCode} ")

                            val success = viewmodel.uploadCustomerDocs(
                                customerID, docTypeCode,
                                channelGeneratedCode, file, count == lastIndex
                            )
                            if (!success) break
                        }
                    }
                    count++

                }
            } else {
                customerDocsList.forEach { uploadedDoc ->
                    Log.d("TAG", "uploadedDoc: $uploadedDoc")
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteCustomerD(customerDetailEntity)
                }
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.onboardCustomerSuccessFragment)
                    viewmodel.stopObserving()
                }
            }
        }

    }


//    private fun performApiRequestEnrollWithMultipleImages(items: CusomerDetailsEntityWithList) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.enrollCustomerWithMultipleImages(
//                idNumber = items.customerDetails.customerId.toString(),
//                finger_index = "1",
//                hand_type = "1",
//            ).collect {
//                when (it) {
//                    is ResourceNetworkFlow.Error -> {
//                        binding.progressbar.mainPBar.makeGone()
//                        showOneButtonDialog(
//                            title = "ERROR",
//                            description = "${it.error}",
//                            image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
//                        )
//                    }
//
//                    is ResourceNetworkFlow.Loading -> {
//                        binding.progressbar.mainPBar.makeVisible()
//                    }
//
//                    is ResourceNetworkFlow.Success -> {
//                        binding.progressbar.mainPBar.makeGone()
//                        if (it.data?.status == 200) {
//                            performSyncingOfCusomerDetailsEntityWithList(
//                                items,
//                                it.data?.data?.userUid.toString()
//                            )
//                            lifecycleScope.launch {
//                                viewModel.deleteByPhoneNumber(customerPhone)
//                            }
//
//                        } else {
//                            Log.e("", "ESLE RESPONSE: ${it.data?.message.toString()}")
//                        }
//                    }
//
//                    else -> {
//                        Log.e("", "else RESPONSE:")
//                    }
//                }
//            }
//        }
//    }
}