package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentOnboardAccountBinding
import com.deefrent.rnd.fieldapp.models.kcbBranches.KcbBranch
import com.deefrent.rnd.fieldapp.models.merchantAgentTypes.MerchantAgent
import com.deefrent.rnd.fieldapp.models.personalAccountTypes.PersonalAccountType
import com.deefrent.rnd.fieldapp.models.userTypes.UserType
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.utils.autoPlayAdvertisement
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import es.dmoral.toasty.Toasty

class OnboardAccountFragment : Fragment() {
    private var _binding: FragmentOnboardAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataViewModel: DataViewModel
    private val sharedViewModel: OnboardAccountSharedViewModel by activityViewModels()
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

    /*@Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
     private val onboardMerchantSharedViewModel by lazy{
         ViewModelProvider(requireActivity(),  viewModelFactory)
             .get(OnboardMerchantSharedViewModel::class.java)
     }*/
    private var merchantTypes: MutableList<MerchantAgent> = mutableListOf()
    private var agentTypes: MutableList<MerchantAgent> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentOnboardAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()

        merchantTypes.clear()
        agentTypes.clear()
        getUserTypes()
        getAccountTypes()
        getKcbBranches()
        getMerchantAgentTypes()
        initButtonClickListeners()
        initDropDowns()
        loadSliders()
        return view
    }

    private fun loadSliders() {
        val fragAd =
            FragmentAddViewpg(
                requireContext()
            )
        binding.pager.adapter = fragAd
        binding.tabDots.setupWithViewPager(binding.pager, true)
        fragAd.notifyDataSetChanged()
        autoPlayAdvertisement(binding.pager)
    }

    private fun observeLoginSharedViewModel() {
        Log.d("TAG", "observeLoginSharedViewModel:")
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        Log.d("TAG", "observeLoginSharedViewModel: $IsFromIncompleteDialog")
                    }
                })
        }
    }

    private fun getMerchantAgentTypes() {
        dataViewModel.fetchMerchantAgentTypes()
            .observe(viewLifecycleOwner) { fetchMerchantAgentTypesResponse ->
                if (fetchMerchantAgentTypesResponse != null) {
                    val merchantAgentTypes: List<MerchantAgent> =
                        fetchMerchantAgentTypesResponse.merchantAgentTypeData.merchant_agent
                    separateMerchantAgentTypes(merchantAgentTypes)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun separateMerchantAgentTypes(merchantAgentTypes: List<MerchantAgent>) {
        for (merchantAgentType in merchantAgentTypes) {
            when (merchantAgentType.userAccountType.UserAccountTypeName) {
                "Merchant" -> {
                    merchantTypes.add(merchantAgentType)
                }
                "Agent" -> {
                    agentTypes.add(merchantAgentType)
                }
            }
        }
    }

    private fun populateMerchantTypes() {
        val merchantTypesAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                merchantTypes
            )
        binding.acMerchantAgentType.setAdapter(merchantTypesAdapter)
        binding.acMerchantAgentType.setOnItemClickListener { parent, _, position, _ ->
            val selected: MerchantAgent = parent.adapter.getItem(position) as MerchantAgent
            onboardMerchantSharedViewModel.setMerchAgentAccountTypeId(selected.id)
        }
    }

    private fun populateAgentTypes() {
        val agentTypesAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                agentTypes
            )
        binding.acMerchantAgentType.setAdapter(agentTypesAdapter)
        binding.acMerchantAgentType.setOnItemClickListener { parent, _, position, _ ->
            val selected: MerchantAgent = parent.adapter.getItem(position) as MerchantAgent
            onboardMerchantSharedViewModel.setMerchAgentAccountTypeId(selected.id)
        }
    }

    private fun getUserTypes() {
        dataViewModel.fetchUserTypes().observe(viewLifecycleOwner) { fetchUserTypesResponse ->
            if (fetchUserTypesResponse != null) {
                val userTypes: List<UserType> = fetchUserTypesResponse.userTypeData.userType
                populateUserTypes(userTypes)
            } else {
                Toasty.error(
                    requireContext(),
                    "An error occurred. Please try again",
                    Toasty.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getAccountTypes() {
        dataViewModel.fetchAccountTypes().observe(viewLifecycleOwner) { fetchAccountTypesResponse ->
            if (fetchAccountTypesResponse != null) {
                val personalAccountTypes: List<PersonalAccountType> =
                    fetchAccountTypesResponse.personalAccountTypeData.personalAccountType
                populateAccountTypes(personalAccountTypes)
            } else {
                Toasty.error(
                    requireContext(),
                    "An error occurred. Please try again",
                    Toasty.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getKcbBranches() {
        dataViewModel.fetchKcbBranches().observe(viewLifecycleOwner) { fetchKcbBranchesResponse ->
            if (fetchKcbBranchesResponse != null) {
                val kcbBranches: List<KcbBranch> =
                    fetchKcbBranchesResponse.kcbBranchesData.kcbBranches
                populateKcbBranches(kcbBranches)
            } else {
                Toasty.error(
                    requireContext(),
                    "An error occurred. Please try again",
                    Toasty.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun populateKcbBranches(kcbBranchList: List<KcbBranch>) {
        val branchesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, kcbBranchList)
        binding.acBranchName.setAdapter(branchesAdapter)
        binding.acBranchName.keyListener = null
        binding.acBranchName.setOnItemClickListener { parent, _, position, _ ->
            val selected: KcbBranch = parent.adapter.getItem(position) as KcbBranch
            sharedViewModel.setKCBBranchId(selected.id)
        }
    }

    private fun populateAccountTypes(accountTypeList: List<PersonalAccountType>) {
        //populate what you need autocomplete
        val whatYouNeedAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, accountTypeList)
        binding.acSelectWhatYouNeed.setAdapter(whatYouNeedAdapter)
        binding.acSelectWhatYouNeed.keyListener = null
        binding.acSelectWhatYouNeed.setOnItemClickListener { parent, _, position, _ ->
            val selected: PersonalAccountType =
                parent.adapter.getItem(position) as PersonalAccountType
            sharedViewModel.setPersonalAccountTypeId(selected.id)
        }
    }

    private fun populateUserTypes(userTypeList: List<UserType>) {
        //populate user type autocomplete
        val userTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userTypeList)
        binding.acUserTypes.setAdapter(userTypesAdapter)
        binding.acUserTypes.keyListener = null
        binding.acMerchantAgentType.keyListener = null
        binding.acUserTypes.setOnItemClickListener { parent, _, position, _ ->
            val selected: UserType = parent.adapter.getItem(position) as UserType
            sharedViewModel.setUserAccountTypeId(selected.id)
            sharedViewModel.setUserType(selected.UserAccountTypeName)
            onboardMerchantSharedViewModel.setUserAccountTypeId(selected.id)
            onboardMerchantSharedViewModel.setUserType(selected.UserAccountTypeName)
            // You can get the label or item that the user clicked:
            when (binding.acUserTypes.text.toString()) {
                "Individual" -> {
                    binding.tiReferenceType.visibility = View.GONE
                    binding.tiMerchantAgentType.visibility = View.GONE
                    binding.tiWhatYouNeed.visibility = View.VISIBLE
                    binding.tiPhoneNumber.visibility = View.VISIBLE
                    binding.tiBranchName.visibility = View.VISIBLE
                }
                "Merchant" -> {
                    populateMerchantTypes()
                    binding.tiReferenceType.visibility = View.GONE
                    binding.tiMerchantAgentType.visibility = View.VISIBLE
                    binding.tiMerchantAgentType.hint = "Merchant Type"
                    binding.tiWhatYouNeed.visibility = View.GONE
                    binding.tiPhoneNumber.visibility = View.GONE
                    binding.tiBranchName.visibility = View.GONE
                }
                "Agent" -> {
                    populateAgentTypes()
                    binding.tiReferenceType.visibility = View.GONE
                    binding.tiMerchantAgentType.visibility = View.VISIBLE
                    binding.tiMerchantAgentType.hint = "Agent Type"
                    binding.tiWhatYouNeed.visibility = View.GONE
                    binding.tiPhoneNumber.visibility = View.GONE
                    binding.tiBranchName.visibility = View.GONE
                }
            }
        }
        /*if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                branchCode.observe(viewLifecycleOwner,
                    { branchCode ->
                        for (branches in bankBranchesList) {
                            if (branches.branchCode == branchCode) {
                                binding.acBranchName.setText(branches.branchName)
                                binding.acUserTypes.setAdapter(userTypesAdapter)
                            }
                        }
                    })
            }
        } else {
            binding.acUserTypes.setAdapter(userTypesAdapter)
        }*/
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        when (binding.acUserTypes.text.toString().isEmpty()) {
            true -> {
                isValid = false
                Toasty.error(
                    requireContext(), "Please fill in all the details",
                    Toasty.LENGTH_LONG
                ).show()
            }
            else -> isValid = true

        }
        return isValid
    }

    private fun initButtonClickListeners() {
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                when (binding.acUserTypes.text.toString()) {
                    "Individual" -> {
                        if (binding.etPhoneNumber.text.toString().isEmpty() ||
                            binding.acSelectWhatYouNeed.text.toString().isEmpty() ||
                            binding.acBranchName.text.toString().isEmpty()
                        ) {
                            Toasty.error(
                                requireContext(), "Please fill in all the details",
                                Toasty.LENGTH_LONG
                            ).show()
                        } else {
                            sharedViewModel.setPhoneNo(binding.etPhoneNumber.text.toString())
                        }
                    }
                    "Agent" -> {
                        if (binding.acMerchantAgentType.text.toString().isEmpty()) {
                            Toasty.error(
                                requireContext(), "Please fill in all the details",
                                Toasty.LENGTH_LONG
                            ).show()
                        } else {
                        }
                    }
                    "Merchant" -> {
                        if (binding.acMerchantAgentType.text.toString().isEmpty()) {
                            Toasty.error(
                                requireContext(), "Please fill in all the details",
                                Toasty.LENGTH_LONG
                            ).show()
                        } else {
                        }
                    }
                }
            }
        }
    }

    private fun initDropDowns() {
        //populate merchant agent autocomplete
        binding.acUserTypes.keyListener = null
        binding.acSelectWhatYouNeed.keyListener = null
        binding.acBranchName.keyListener = null

        //populate reference types autocomplete
        val referenceTypes = resources.getStringArray(R.array.reference_types_agent)
        val referenceTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, referenceTypes)
        binding.acReferenceType.setAdapter(referenceTypesAdapter)
        binding.acReferenceType.keyListener = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnboardAccountFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}