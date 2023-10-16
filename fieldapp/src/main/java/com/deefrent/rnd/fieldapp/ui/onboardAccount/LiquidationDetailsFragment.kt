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
import com.deefrent.rnd.fieldapp.databinding.FragmentLiquidationDetailsBinding
import com.deefrent.rnd.fieldapp.models.bankBranches.BankBranch
import com.deefrent.rnd.fieldapp.models.banks.Bank
import com.deefrent.rnd.fieldapp.models.liquidationTypes.LiquidationType
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LiquidationDetailsFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private var _binding: FragmentLiquidationDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataViewModel: DataViewModel
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
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentLiquidationDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                onboardMerchantSharedViewModel.setLiquidationRate((binding.etLiquidationRate.text.toString()).toInt())
                onboardMerchantSharedViewModel.setBankCode(binding.etBankCode.text.toString())
                onboardMerchantSharedViewModel.setAccountName(binding.etAccountName.text.toString())
                onboardMerchantSharedViewModel.setAccountNumber(binding.etAccountNumber.text.toString())
                saveDataLocally(
                    (binding.etLiquidationRate.text.toString()).toInt(),
                    binding.etBankCode.text.toString(), binding.etAccountName.text.toString(),
                    binding.etAccountNumber.text.toString()
                )
            }
        }
        getLiquidationTypes()
        getBanks()
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        onboardMerchantSharedViewModel.apply {
                            liquidationRate.observe(viewLifecycleOwner,
                                { liquidationRate ->
                                    binding.etLiquidationRate.setText(liquidationRate.toString())
                                })
                            accountName.observe(viewLifecycleOwner,
                                { accountName ->
                                    binding.etAccountName.setText(accountName)
                                })
                            accountNumber.observe(viewLifecycleOwner,
                                { accountNumber ->
                                    binding.etAccountNumber.setText(accountNumber)
                                })
                        }
                    }
                })
        }
    }

    private fun saveDataLocally(
        LiquidationRate: Int, BankCode: String, AccountName: String,
        AccountNumber: String
    ) {
        var RoomDBId = 0
        var LiquidationTypeId = 0
        var BranchCode = ""
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            liquidationTypeId.observe(viewLifecycleOwner,
                { liquidationTypeId ->
                    LiquidationTypeId = liquidationTypeId
                })
            branchCode.observe(viewLifecycleOwner,
                { branchCode ->
                    BranchCode = branchCode
                })
            lastStep.observe(viewLifecycleOwner,
                { lastStep ->
                    Log.d("TAG", "saveDataLocally: $lastStep1")
                    lastStep1 = if (IsFromIncompleteDialog) {
                        lastStep
                    } else {
                        lastStep2
                    }
                })
        }
        onboardMerchantSharedViewModel.setLastStep(this::class.java.simpleName)
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.updateLiquidationDetails(
            LiquidationTypeId,
            LiquidationRate,
            BankCode,
            BranchCode,
            AccountName,
            AccountNumber,
            lastStep1,
            RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun getLiquidationTypes() {
        dataViewModel.fetchLiquidationTypes()
            .observe(viewLifecycleOwner) { fetchLiquidationTypesResponse ->
                if (fetchLiquidationTypesResponse != null) {
                    val liquidationTypes: List<LiquidationType> =
                        fetchLiquidationTypesResponse.liquidationTypeData.liquidationType
                    populateLiquidationTypes(liquidationTypes)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateLiquidationTypes(liquidationTypesList: List<LiquidationType>) {
        //populate liquidation types dropdown
        val liquidationTypesAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                liquidationTypesList
            )
        binding.acLiquidationType.keyListener = null
        binding.acLiquidationType.setOnItemClickListener { parent, _, position, _ ->
            val selected: LiquidationType = parent.adapter.getItem(position) as LiquidationType
            onboardMerchantSharedViewModel.setLiquidationTypeId(selected.id)
        }
        if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                liquidationTypeId.observe(viewLifecycleOwner,
                    { liquidationTypeId ->
                        for (liquidationTypes in liquidationTypesList) {
                            if (liquidationTypes.id == liquidationTypeId) {
                                binding.acLiquidationType.setText(liquidationTypes.liquidationType)
                                binding.acLiquidationType.setAdapter(liquidationTypesAdapter)
                            }
                        }
                    })
            }
        } else {
            binding.acLiquidationType.setAdapter(liquidationTypesAdapter)
        }
    }

    private fun getBanks() {
        dataViewModel.fetchBanks()
            .observe(viewLifecycleOwner) { fetchBanksResponse ->
                if (fetchBanksResponse != null) {
                    val banks: List<Bank> = fetchBanksResponse.banksData.banks
                    populateBanks(banks)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateBanks(banksList: List<Bank>) {
        val bankNamesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, banksList)
        binding.acBankName.keyListener = null
        binding.acBankName.setOnItemClickListener { parent, _, position, _ ->
            val selected: Bank = parent.adapter.getItem(position) as Bank
            binding.etBankCode.setText(selected.bankCode)
            getBankBranches(selected.bankCode)
        }
        if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                bankCode.observe(viewLifecycleOwner,
                    { bankCode ->
                        for (banks in banksList) {
                            if (banks.bankCode == bankCode) {
                                getBankBranches(banks.bankCode)
                                binding.acBankName.setText(banks.bankName)
                                binding.etBankCode.setText(banks.bankCode)
                                binding.acBankName.setAdapter(bankNamesAdapter)
                            }
                        }
                    })
            }
        } else {
            binding.acBankName.setAdapter(bankNamesAdapter)
        }
    }

    private fun getBankBranches(bankCode: String) {
        Constants.callDialog2("Fetching bank branches...", requireContext())
        dataViewModel.fetchBankBranches(bankCode)
            .observe(viewLifecycleOwner) { fetchBankBranchesResponse ->
                if (fetchBankBranchesResponse != null) {
                    Constants.cancelDialog()
                    val bankBranchesList: List<BankBranch> =
                        fetchBankBranchesResponse.bankBranchesData.bankBranches
                    populateBankBranches(bankBranchesList)
                } else {
                    Constants.cancelDialog()
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateBankBranches(bankBranchesList: List<BankBranch>) {
        val branchesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, bankBranchesList)
        binding.acBranchName.keyListener = null
        binding.acBranchName.setOnItemClickListener { parent, _, position, _ ->
            val selected: BankBranch = parent.adapter.getItem(position) as BankBranch
            onboardMerchantSharedViewModel.setBranchCode(selected.branchCode)
        }
        if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                branchCode.observe(viewLifecycleOwner,
                    { branchCode ->
                        for (branches in bankBranchesList) {
                            if (branches.branchCode == branchCode) {
                                binding.acBranchName.setText(branches.branchName)
                                binding.acBranchName.setAdapter(branchesAdapter)
                            }
                        }
                    })
            }
        } else {
            binding.acBranchName.setAdapter(branchesAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.acLiquidationType.text.toString().isNullOrEmpty() ||
            binding.etLiquidationRate.text.toString().isNullOrEmpty() ||
            binding.acBankName.text.toString().isNullOrEmpty() ||
            binding.etBankCode.text.toString().isNullOrEmpty() ||
            binding.acBranchName.text.toString().isNullOrEmpty() ||
            binding.etAccountName.text.toString().isNullOrEmpty() ||
            binding.etAccountNumber.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else if (binding.etLiquidationRate.text.toString()
                .toInt() > 100 || binding.etLiquidationRate.text.toString().toInt() < 1
        ) {
            isValid = false
            Toasty.error(
                requireContext(),
                "Liquidation rate must be between 1% and 100 %",
                Toasty.LENGTH_LONG
            )
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LiquidationDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}