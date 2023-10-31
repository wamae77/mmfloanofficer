package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.AddDependantDialogBinding
import com.deefrent.rnd.fieldapp.databinding.DepositDialogLayoutBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentFuneralCashPlanStep5Binding
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.SavingAccDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FuneralCashPlanPackagesData
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.SavingAccountData
import com.deefrent.rnd.fieldapp.room.entities.RshipTypeEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.adapters.*
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.add_dependant_dialog.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_funeral_cash_plan_step_5.*
import kotlinx.coroutines.launch
import request.Dependant
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Step5PackageFuneralCashPlanFragment :
    BaseMoneyMartBindedFragment<FragmentFuneralCashPlanStep5Binding>(
        FragmentFuneralCashPlanStep5Binding::inflate
    ) {

    @Inject
    lateinit var viewModel: FuneralCashPlanViewModel

    private val sharedViewModel: SharedFuneralCashPlanViewModel by activityViewModels()

    private lateinit var dialog: BottomSheetDialog


    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private lateinit var cardBinding: DepositDialogLayoutBinding
    private lateinit var selectedPackage: FuneralCashPlanPackagesData
    private lateinit var adultDependantContribution: String
    private lateinit var minorContributionAmount: String

    private lateinit var rshipId: String
    private var isAdult = true
    private lateinit var customerData: FindCustomerData


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        binding.btnContinue.setOnClickListener {
//            findNavController().navigate(R.id.action_step5IdLookUpFuneralCashPlanFragment_to_step6PackagesFuneralCashPlanFragment)
//        }
        customerData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.CUSTOMER_INFO),
            FindCustomerData::class.java
        )

        inflateRecyclerView(
            sharedViewModel.dependantsList.filter {
                it.idNumber !== customerData.idNumber
            }
        )

        binding.fabButtom.setOnClickListener {
            displayAddDependantDialog()
        }
        selectedPackage = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.SELECTED_PACKAGE),
            FuneralCashPlanPackagesData::class.java
        )
        /**
         * Set Up Tool bar configurations
         */
        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = selectedPackage.name, //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        adultDependantContribution = FormatDigit.formatDigits(
            selectedPackage.adultDependantContribution.toDouble().toInt().toString()
        )
        minorContributionAmount = FormatDigit.formatDigits(
            selectedPackage.minorContributionAmount.toDouble().toInt().toString()
        )




        /**spinner From impl*/
        //getSavingAccounts()

        val name = "${customerData.firstName.toString()} ${customerData.lastName.toString()}"
        val phone = customerData.phone.toString()
        val idNumber = customerData.idNumber.toString()
        val dob = customerData.dob.toString()

        binding.tvDependantTitle.text = name
        binding.tvContributionAmount.text = "Amount :USD ${adultDependantContribution}"
        binding.btnContinue.setOnClickListener {

            val myselfDependant = Dependant(
                dob = dob,
                name = name,
                relationshipId = 1,
                phone = phone,
                idNumber = idNumber,
                isBeneficiary = 0,
                contributionAmount = adultDependantContribution
            )
            sharedViewModel.addDependantToList(myselfDependant)

            findNavController().navigate(R.id.action_step5IdLookUpFuneralCashPlanFragment_to_step6PackagesFuneralCashPlanFragment)
        }
    }

    private fun getSavingAccounts() {
        lifecycleScope.launch {
            val savingAccDTO = SavingAccDTO()
            savingAccDTO.isTransactional = 1
            savingAccDTO.customerIdNumber = customerData.idNumber.toString()
            viewModel.getSavingAcc(savingAccDTO).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }
                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 1) {
                            val adapter = SavingAccountAdapter(requireContext(), it.data?.data!!)
                            adapter.notifyDataSetChanged()
                            //binding.spFrom.adapter = adapter
                            binding.pbFrom.makeGone()
                            binding.spFrom.setAdapter(
                                ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    it.data?.data!!
                                )
                            )
                            binding.spFrom.setOnItemClickListener { parent, _, position, _ ->
                                val selectedAccount =
                                    parent.adapter.getItem(position) as SavingAccountData
                                Constants.SaveIdFrom = selectedAccount.accountId
                                Constants.SaveACNOFROM = selectedAccount.accountNo
                                Constants.SaveNameFrom = selectedAccount.accountName
                                if (selectedAccount.currentBalance.replace(",", "")
                                        .toDouble() < 1
                                ) {
                                    onInfoDialog(
                                        ""
                                    )
                                }
                            }
                        } else {

                        }
                    }
                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }


    fun populateRship(rship: List<RshipTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        addDependantDialog.acRelationship.setAdapter(typeAdapter)
        addDependantDialog.acRelationship.keyListener = null
        addDependantDialog.acRelationship.setOnItemClickListener { parent, _, position, _ ->
            val selected: RshipTypeEntity = parent.adapter.getItem(position) as RshipTypeEntity
            addDependantDialog.acRelationship.setText(selected.name, false)
            rshipId = selected.id.toString()
        }
    }

    fun displayAddDependantDialog() {
        addDependantDialog = AddDependantDialogBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { rList ->
            populateRship(rList)
        }
        addDependantDialog.apply {
            tiFullName.editText?.addTextChangedListener(CustomTextWatcher(addDependantDialog.tiFullName))
            tiRelationship.editText?.addTextChangedListener(CustomTextWatcher(addDependantDialog.tiRelationship))
            tiPhoneNumber.editText?.addTextChangedListener(CustomTextWatcher(addDependantDialog.tiPhoneNumber))
            tiIDNumber.editText?.addTextChangedListener(CustomTextWatcher(addDependantDialog.tiIDNumber))
            tiDOB.editText?.addTextChangedListener(CustomTextWatcher(addDependantDialog.tiDOB))
        }

        addDependantDialog.apply {
            etDOB.setOnClickListener {
                pickDob()
            }
            btnContinue.setOnClickListener {
                val validMsg = FieldValidators.VALIDINPUT
                val phoneNumber =
                    FieldValidators().formatPhoneNumber(addDependantDialog.etPhone.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                val dependantName = etFullName.text.toString()
                val dependantIdNumber = etIDNumber.text.toString()
                val relationShip = acRelationship.text.toString()
                val dob = etDOB.text.toString()
                val isBeneficiary = if (cbConfirmDependant.isChecked) {
                    1
                } else {
                    0
                }
                if (dependantName.isEmpty()) {
                    tiFullName.error = ""//getString(R.string.enter_full_names)
                } else if (relationShip.isEmpty()) {
                    tiRelationship.error = ""//getString(R.string.select_rship)
                } else if (!validPhone.contentEquals(validMsg)) {
                    tiIDNumber.error = validPhone
                } else if (dob.isEmpty()) {
                    tiDOB.error = "Please select a Date of Birth"
                }// else if (isAdult) {
                else if (dependantIdNumber.length <8) {
                    tiIDNumber.error = "Invalid ID number"
                }
                // }
                else {
                    val dependant = Dependant(
                        dob = dob,
                        name = dependantName,
                        relationshipId = rshipId.toInt(),
                        phone = addDependantDialog.etPhone.text.toString(),
                        idNumber = dependantIdNumber,
                        isBeneficiary = isBeneficiary,
                        contributionAmount = if (isAdult) adultDependantContribution else minorContributionAmount
                    )
                    sharedViewModel.addDependantToList(dependant)

                    binding.rvDependants.addItemDecoration(
                        MarginItemDecoration(
                            resources.getDimension(
                                R.dimen._5dp
                            ).toInt()
                        )
                    )
                    inflateRecyclerView(
                        sharedViewModel.dependantsList.filter {
                            it.idNumber !== customerData.idNumber
                        }
                    )
                    addEditDependantsListAdapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }

        dialog.setContentView(addDependantDialog.root)
        //  dialog.setCancelable(false)
        dialog.show()
    }

    private fun pickDob() {
        val dateListener: DatePickerDialog.OnDateSetListener
        val myCalendar = Calendar.getInstance()
        val currYear = myCalendar[Calendar.YEAR]
        val currMonth = myCalendar[Calendar.MONTH]
        val currDay = myCalendar[Calendar.DAY_OF_MONTH]
        dateListener =
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val preferredFormat = "dd-MM-yyyy"
                val date =
                    SimpleDateFormat(preferredFormat, Locale.US).format(myCalendar.time)
                addDependantDialog.etDOB.setText(date)
                if (currYear - year < 18) isAdult = false else if (currYear == 18) {
                    if (currMonth - monthOfYear < 0) isAdult = false
                    if (currMonth == monthOfYear && currDay - dayOfMonth < 0) isAdult = false
                } else {
                    isAdult = true
                }
                Log.d("TAG", "pickDob: $isAdult")
            }
        val datePickerDialog = DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = myCalendar.timeInMillis
        datePickerDialog.show()
    }

    private fun validateData() {
        if (binding.spFrom.text.toString().isEmpty()) {
            binding.tiFrom.error = "Please select an account"
        } else {
            val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
            cardBinding =
                DepositDialogLayoutBinding.inflate(LayoutInflater.from(context))
            cardBinding.apply {
                tvHeading.text = "confirm_payment"// getString(R.string.confirm_payment)
                tvName.text = "PACKAGE AMOUNT:"
                tvNameValue.text =
                    "USD ${FormatDigit.formatDigits(binding.etAmount.text.toString())}"
                tvBank.text = "PAY FROM:"
                /* val fromId = Constants.SaveACNOFROM.replace(
                     "(?<=.{3}).(?=.{3})".toRegex(),
                     "*"
                 )*/
                tvBankValue.text = ""//"${Constants.SaveNameFrom} - A/C $fromId"
                tvACNO.text = "PACKAGE:"
                /*val fromIdTo = Constants.SaveACNOTo.replace(
                    "(?<=.{3}).(?=.{3})".toRegex(),
                    "*"
                )*/
                tvACNOValue.text = selectedPackage.name
                tvAmount.text = "DEPENDANTS:"
                tvFrom.makeGone()
                tvAmountValue.text = "${sharedViewModel.dependantsList.size}"
                tvFromValur.makeGone()
            }
            cardBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            cardBinding.btnSubmit.setOnClickListener {
                dialog.dismiss()

            }
            dialog.setContentView(cardBinding.root)
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    private val addEditDependantsListAdapter by lazy {
        AddEditDependantsListAdapter(
            addEditDependantsListAdapterCallback
        )
    }

    private fun inflateRecyclerView(itemsRV: List<Dependant>) {
        if (itemsRV.isEmpty()) {
            binding.tvNoDependants.visibility = View.VISIBLE
            binding.rvDependants.visibility = View.GONE
        } else {
            binding.tvNoDependants.visibility = View.GONE
            binding.rvDependants.visibility = View.VISIBLE
            addEditDependantsListAdapter.submitList(itemsRV)
            addEditDependantsListAdapter?.notifyDataSetChanged()
            binding.rvDependants.apply {
                layoutManager = LinearLayoutManager(this.context!!)
                adapter = addEditDependantsListAdapter
                setHasFixedSize(true)
            }
        }

    }

    private val addEditDependantsListAdapterCallback =
        object : AddEditDependantsListAdapterCallback {
            override fun onItemSelected(view: View, item: Dependant) {
                when (view.id) {
                    R.id.ivClear -> {
                        sharedViewModel.removeDependantToList(item)
                        inflateRecyclerView(
                            sharedViewModel.dependantsList.filter {
                                it.idNumber !== customerData.idNumber
                            }
                        )
                    }
                    else -> {

                    }
                }
            }
        }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var addDependantDialog: AddDependantDialogBinding

        /*  @JvmStatic
          fun newInstance() =
              CashPlanApplicationFragment().apply {
                  arguments = Bundle().apply {
                  }
              }*/
    }


}