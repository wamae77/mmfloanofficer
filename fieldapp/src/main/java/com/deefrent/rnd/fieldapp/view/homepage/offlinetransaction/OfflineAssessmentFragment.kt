package com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.AssessmentOfflineAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentOfflineAssessmentBinding
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.dtos.Collateral
import com.deefrent.rnd.fieldapp.dtos.Guarantor
import com.deefrent.rnd.fieldapp.dtos.OtherBorrowing
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.repos.AssessCustomerRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessmentEntityCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.ArrayList

class OfflineAssessmentFragment : Fragment(), AssessmentEntityCallBack {
    private lateinit var binding: FragmentOfflineAssessmentBinding
    private lateinit var repository: AssessCustomerRepository
    private lateinit var assessCustomerEntity: AssessCustomerEntity
    private var customerId = ""

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private lateinit var offlineAdapter: AssessmentOfflineAdapter
    private var arrayList: ArrayList<AssessCustomerEntityWithList> = arrayListOf()
    private lateinit var directory: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfflineAssessmentBinding.inflate(layoutInflater)
        directory = Constants.IMAGES_DIR
        offlineAdapter = AssessmentOfflineAdapter(arrayList, this, this@OfflineAssessmentFragment)
        binding.rvOfflineAss.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvOfflineAss.adapter = offlineAdapter
        viewmodel.customerCompeteOfflineWithList.observe(viewLifecycleOwner) {
            arrayList.clear()
            arrayList.addAll(it)
            offlineAdapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                binding.svAssessment.isRefreshing = false
                binding.noNewRequest.makeVisible()
                binding.rvOfflineAss.makeGone()
            } else {
                binding.svAssessment.isRefreshing = false
                binding.noNewRequest.makeGone()
                binding.rvOfflineAss.makeVisible()
            }
        }
        return binding.root
    }

    fun deleteOfflineTransaction(listItems: AssessCustomerEntityWithList, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete ${listItems.assessCustomerEntity.firstName.capitalizeWords}'s Record?")
        builder.setMessage("You are about to delete the offline transaction. Please note that this action cannot be undone.Do you wish to continue?")

        builder.setPositiveButton("YES") { _, _ ->
            GlobalScope.launch(Dispatchers.IO) {
                listItems.customerDocs.forEach { uploadedDoc ->
                    Log.d("TAG", "uploadedDoc: $uploadedDoc")
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteAssessedCustomer(listItems.assessCustomerEntity)
                }
            }
            //viewmodel.removeAssessesDataAtPos(position)
            arrayList.removeAt(position)
            offlineAdapter.notifyItemRemoved(position)
            offlineAdapter.notifyDataSetChanged()
            /**notify adapter more than one items has been changed*/
            // incompleteAdapter.notifyItemRangeChanged(pos,incompleteAdapter.itemCount)
            if (arrayList.isEmpty()) {
                binding.noNewRequest.makeVisible()
                binding.rvOfflineAss.makeGone()
            } else {
                binding.noNewRequest.makeGone()
                binding.rvOfflineAss.makeVisible()
            }
        }

        builder.setNegativeButton("NO") { _, _ ->

        }

        builder.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.svAssessment.setOnRefreshListener {
            getLocalCustomerDetails()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val customerDetailsDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).assessCustomerDao()
        repository = AssessCustomerRepository(customerDetailsDao)
    }

    private fun getLocalCustomerDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            val cList = repository.getOfflineAssessed(true)
            withContext(Dispatchers.Main) {
                viewmodel.customerCompeteOfflineWithList.postValue(cList)
                Log.e("TAG", "initializeUIl: ${Gson().toJson(cList)}")
            }

        }
    }

    private fun initiateDocumentsUpload(customerDocsList: List<AssessCustomerDocsEntity>) {
        /**run within the lifecycle of the view, if its destoryed it stop uploading */
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            var count = 0
            val filteredList = customerDocsList.filter { assessCustomerDocsEntity ->
                assessCustomerDocsEntity.docPath.isNotEmpty() && !Constants.pattern.containsMatchIn(
                    assessCustomerDocsEntity.docPath
                )
            }
            if (filteredList.isNotEmpty()) {
                val lastIndex = filteredList.size.minus(1)
                /**incase of an error occured while uplading image to stop uploading*/
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
                        val convertedFile = convertPathToFile(location)
                        val compressedImages = Compressor.compress(requireContext(), convertedFile)
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

                    count++

                }
                com.deefrent.rnd.common.utils.Constants.deleteCacheImageFromInternalStorage(
                    requireContext(),
                    "compressor"
                )
            } else {
                customerDocsList.forEach { uploadedDoc ->
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteAssessedCustomer(assessCustomerEntity)
                }
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.assessSuccessFragment)
                    viewmodel.stopObserving()
                }

            }

        }
    }

    override fun onItemSelected(pos: Int, listItems: AssessCustomerEntityWithList) {
        assessCustomerEntity = listItems.assessCustomerEntity
        Log.e("TAG", "initializeUI: ${Gson().toJson(listItems.assessCustomerEntity)}")
        Log.e(
            "TAG",
            "initializeUI: ${Gson().toJson(listItems.assessCustomerEntity.assessmentRemarks)}"
        )
        Log.e("TAG", "onItemSelected:Gso${Gson().toJson(listItems.customerDocs)} ")
        val collaterals = listItems.assessCollateral.map { collateralInfo ->
            Collateral(
                collateralInfo.assetTypeId,
                collateralInfo.estimateValue,
                collateralInfo.model,
                collateralInfo.name,
                collateralInfo.serialNumber,
                collateralInfo.collateralGeneratedUID
            )
        }
        val guarantors = listItems.assessGua.map { guarantorInfo ->
            Guarantor(
                guarantorInfo.residenceAddress, guarantorInfo.generatedUID,
                guarantorInfo.idNumber,
                guarantorInfo.name,
                guarantorInfo.phone, guarantorInfo.relationship
            )
        }
        val otherBorrowings = listItems.assessBorrow.map { otherBorrowings ->
            OtherBorrowing(
                otherBorrowings.institutionName,
                otherBorrowings.amount,
                otherBorrowings.totalAmountPaidToDate,
                otherBorrowings.statusId.toString(),
                otherBorrowings.monthlyInstallmentPaid
            )
        }
        val householdMembers = listItems.householdMember.map { householdMembers ->
            HouseholdMember(
                householdMembers.fullName,
                householdMembers.incomeOrFeesPaid,
                householdMembers.natureOfActivity,
                householdMembers.occupationId,
                householdMembers.relationshipId
            )
        }
        if (!listItems.assessCustomerEntity.isProcessed) {
            val assessCustomerDTO = AssessCustomerDTO(
                listItems.assessCustomerEntity.subBranchId,
                listItems.assessCustomerEntity.alsoKnownAs,
                listItems.assessCustomerEntity.assessmentRemarks,
                listItems.assessCustomerEntity.businessDistrictId,
                listItems.assessCustomerEntity.economicFactorId,
                listItems.assessCustomerEntity.establishmentTypeId,
                listItems.assessCustomerEntity.nameOfIndustry,
                listItems.assessCustomerEntity.numberOfEmployees,
                listItems.assessCustomerEntity.businessPhone,
                listItems.assessCustomerEntity.businessPhysicalAddress,
                listItems.assessCustomerEntity.businessTypeId,
                listItems.assessCustomerEntity.businessVillageId,
                listItems.assessCustomerEntity.yearsInBusiness,
                collaterals,
                listItems.assessCustomerEntity.customerNumber,
                listItems.assessCustomerEntity.dob,
                listItems.assessCustomerEntity.educationLevelId,
                listItems.assessCustomerEntity.emailAddress,
                listItems.assessCustomerEntity.empStatusId,
                listItems.assessCustomerEntity.expenseFood,
                listItems.assessCustomerEntity.expenseMedicalAidOrContributions,
                listItems.assessCustomerEntity.expenseRentals,
                listItems.assessCustomerEntity.expenseSchoolFees,
                listItems.assessCustomerEntity.expenseTransport,
                listItems.assessCustomerEntity.firstName,
                listItems.assessCustomerEntity.genderId,
                guarantors,
                householdMembers,
                listItems.assessCustomerEntity.identifierId,
                listItems.assessCustomerEntity.netSalary,
                listItems.assessCustomerEntity.grossSalary,
                listItems.assessCustomerEntity.profit,
                listItems.assessCustomerEntity.donation,
                listItems.assessCustomerEntity.rentalIncome,
                listItems.assessCustomerEntity.totalSales,
                listItems.assessCustomerEntity.kinFirstName,
                listItems.assessCustomerEntity.kinIdNumber,
                listItems.assessCustomerEntity.kinIdentityTypeId,
                listItems.assessCustomerEntity.kinLastName,
                listItems.assessCustomerEntity.kinPhoneNumber,
                listItems.assessCustomerEntity.kinRelationshipId,
                listItems.assessCustomerEntity.lastName,
                listItems.assessCustomerEntity.idNumber,
                listItems.assessCustomerEntity.numberOfChildren,
                listItems.assessCustomerEntity.numberOfDependants,
                listItems.assessCustomerEntity.otherExpenses,
                listItems.assessCustomerEntity.otherIncome,
                listItems.assessCustomerEntity.phone,
                listItems.assessCustomerEntity.resAccomadationStatus,
                listItems.assessCustomerEntity.resLivingSince,
                listItems.assessCustomerEntity.resPhysicalAddress,
                listItems.assessCustomerEntity.spouseName,
                listItems.assessCustomerEntity.spousePhone,
                otherBorrowings
            )
            customerId = listItems.assessCustomerEntity.customerId
            viewmodel.assessCustomer(assessCustomerDTO)
        } else {
            if (listItems.customerDocs.isNotEmpty()) {
                initiateDocumentsUpload(listItems.customerDocs)
            }
        }
        viewmodel.responseXStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Uploading documents..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                    else -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        if (listItems.customerDocs.isEmpty()) {
                            findNavController().navigate(R.id.assessSuccessFragment)
                        } else {
                            initiateDocumentsUpload(listItems.customerDocs)
                        }
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        viewmodel.stopObserving()
                        onInfoDialog(viewmodel.statusMessage.value)
                    }
                    else -> {
                        viewmodel.stopObserving()
                        // showErrorDialog()
                    }
                }
            }
        }
        viewmodel.responseAssessStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {

                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.tvWait.text = "Please wait as we submit the data..."
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                    else -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusDocCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        listItems.customerDocs.forEach { uploadedDoc ->
                            Log.d("TAG", "uploadedDoc: $uploadedDoc")
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteAssessedCustomer(listItems.assessCustomerEntity)
                        }
                        findNavController().navigate(R.id.assessSuccessFragment)
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        listItems.customerDocs.forEach { uploadedDoc ->
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteAssessedCustomer(listItems.assessCustomerEntity)
                        }
                        toastyErrors("Uploading the documents failed...\nKindly update the documents on customer 360...")
                        findNavController().navigate(R.id.dashboardFragment)
                    }
                    else -> {
                        listItems.customerDocs.forEach { uploadedDoc ->
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteAssessedCustomer(listItems.assessCustomerEntity)
                        }
                        toastyErrors("Uploading the documents failed...\nKindly update the documents on customer 360...")
                        findNavController().navigate(R.id.dashboardFragment)
                    }
                }
            }
        }
    }
}