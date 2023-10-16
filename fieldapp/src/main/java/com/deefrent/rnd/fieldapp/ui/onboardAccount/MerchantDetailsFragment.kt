package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentMerchantDetailsBinding
import com.deefrent.rnd.fieldapp.models.businessTypes.BusinessType
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails
import com.deefrent.rnd.fieldapp.utils.isEmailValid
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import es.dmoral.toasty.Toasty
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MerchantDetailsFragment : Fragment() {
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private var _binding: FragmentMerchantDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataViewModel: DataViewModel
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //how to get app context from within a DFM
        //val ctx = BaseApp().applicationContext
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentMerchantDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                onboardMerchantSharedViewModel.setBusinessName(binding.etBusinessName.text.toString())
                onboardMerchantSharedViewModel.setMobileNumber(binding.etBusinessMobileNumber.text.toString())
                onboardMerchantSharedViewModel.setEmail(binding.etBusinessEmail.text.toString())
                onboardMerchantSharedViewModel.setBusinessNature(binding.etBusinessNature.text.toString())
                saveDataLocally(
                    binding.etBusinessName.text.toString(),
                    binding.etBusinessMobileNumber.text.toString(),
                    binding.etBusinessEmail.text.toString(),
                    binding.etBusinessNature.text.toString()
                )
            }
        }
        observeSharedViewModel()
        getBusinessTypes()
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        onboardMerchantSharedViewModel.apply {
                            businessName.observe(viewLifecycleOwner,
                                { businessName ->
                                    binding.etBusinessName.setText(businessName)
                                })
                            mobileNumber.observe(viewLifecycleOwner,
                                { mobileNumber ->
                                    binding.etBusinessMobileNumber.setText(mobileNumber)
                                })
                            email.observe(viewLifecycleOwner,
                                { email ->
                                    binding.etBusinessEmail.setText(email)
                                })
                            businessNature.observe(viewLifecycleOwner,
                                { businessNature ->
                                    binding.etBusinessNature.setText(businessNature)
                                })
                        }
                    }
                })
        }
    }

    private fun saveDataLocally(
        BusinessName: String,
        BusinessMobileNumber: String,
        BusinessEmail: String,
        BusinessNature: String
    ) {
        var RoomDBId = 0
        var UserType = ""
        var MerchantAccountTypeId = 0
        var BusinessTypeId = 0
        var UserAccountTypeId = 0
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        onboardMerchantSharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    UserType = userType
                })
            merchAgentAccountTypeId.observe(viewLifecycleOwner,
                { merchantAccountTypeId ->
                    MerchantAccountTypeId = merchantAccountTypeId
                })
            businessTypeId.observe(viewLifecycleOwner,
                { businessTypeId ->
                    BusinessTypeId = businessTypeId
                })
            userAccountTypeId.observe(viewLifecycleOwner,
                { userAccountTypeId ->
                    UserAccountTypeId = userAccountTypeId
                })
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
        }
        val merchantAgentDetails = MerchantAgentDetails()
        merchantAgentDetails.apply {
            date = currentDate
            userType = UserType
            merchAgentAccountTypeId = MerchantAccountTypeId
            businessName = BusinessName
            businessNature = BusinessNature
            businessMobileNumber = BusinessMobileNumber
            businessEmail = BusinessEmail
            businessTypeId = BusinessTypeId
            userAccountTypeId = UserAccountTypeId
            lastStep = this::class.java.simpleName
            complete = false
        }
        onboardMerchantSharedViewModel.setLastStep(this::class.java.simpleName)
        val fieldAppDatabase = FieldAppDatabase.getFieldAppDatabase(requireContext())
        if (IsFromIncompleteDialog) {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(roomDBViewModel.updateMerchantDetails(
                UserType, UserAccountTypeId, MerchantAccountTypeId, BusinessName,
                BusinessMobileNumber, BusinessEmail, BusinessTypeId, BusinessNature, RoomDBId
            )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    compositeDisposable.dispose()
                })
        } else {
            fieldAppDatabase!!.merchantAgentDetailsDao().addMerchantAgentDetail(merchantAgentDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Long?> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        Log.d("room", "onError: ${e.localizedMessage}")
                    }

                    override fun onSuccess(t: Long) {
                        val roomMerchantDetailId = t.toInt()
                        Log.d("room", "onSuccess: $roomMerchantDetailId")
                        onboardMerchantSharedViewModel.setRoomDBId(roomMerchantDetailId)
                    }
                })
        }
    }

    private fun observeSharedViewModel() {
        onboardMerchantSharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType Details"
                })
        }
    }

    private fun getBusinessTypes() {
        dataViewModel.fetchBusinessTypes()
            .observe(viewLifecycleOwner) { fetchBusinessTypesResponse ->
                if (fetchBusinessTypesResponse != null) {
                    val businessTypes: List<BusinessType> =
                        fetchBusinessTypesResponse.businessTypesData.businessTypes
                    populateBusinessTypes(businessTypes)
                } else {
                    Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
                }
            }
    }

    private fun populateBusinessTypes(businessTypesList: List<BusinessType>) {
        val businessTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, businessTypesList)
        binding.acBusinessType.keyListener = null
        binding.acBusinessType.setOnItemClickListener { parent, _, position, _ ->
            val selected: BusinessType = parent.adapter.getItem(position) as BusinessType
            onboardMerchantSharedViewModel.setBusinessTypeId(selected.id)
        }
        if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                businessTypeId.observe(viewLifecycleOwner,
                    { businessTypeId ->
                        for (businessTypes in businessTypesList) {
                            if (businessTypes.id == businessTypeId) {
                                binding.acBusinessType.setText(businessTypes.businessTypeName)
                                binding.acBusinessType.setAdapter(businessTypesAdapter)
                            }
                        }
                        //val selected: BusinessType = binding.acBusinessType.adapter.getItem(businessTypeId) as BusinessType
                    })
            }
        } else {
            binding.acBusinessType.setAdapter(businessTypesAdapter)
        }
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.etBusinessName.text.toString().isNullOrEmpty() ||
            binding.etBusinessMobileNumber.text.toString().isNullOrEmpty() ||
            binding.etBusinessEmail.text.toString().isNullOrEmpty() ||
            binding.acBusinessType.text.toString().isNullOrEmpty() ||
            binding.etBusinessNature.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        }else if(!binding.etBusinessEmail.text.toString().isEmailValid()){
            isValid = false
            Toasty.error(requireContext(), "Please enter a valid email address", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MerchantDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}