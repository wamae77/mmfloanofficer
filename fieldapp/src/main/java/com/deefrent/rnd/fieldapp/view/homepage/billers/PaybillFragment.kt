package com.deefrent.rnd.fieldapp.view.homepage.billers

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentPaybillBinding
import com.deefrent.rnd.fieldapp.databinding.PaybillConfirmDialogBinding
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentDTO
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentPreviewDTO
import com.deefrent.rnd.fieldapp.dtos.customer.GetWalletAccountsDTO
import com.deefrent.rnd.fieldapp.models.customer.WalletAccount
import com.deefrent.rnd.fieldapp.network.models.Biller
import com.deefrent.rnd.fieldapp.responses.GetBillPaymentPreviewResponse
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_paybill.*

class PaybillFragment : Fragment() {
    private lateinit var binding: FragmentPaybillBinding
    private lateinit var walletAccountNumber: String
    private lateinit var totalAMount: String
    private lateinit var currency: String
    private lateinit var billerCode: String
    private lateinit var transactionType: String
    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var customerID: String
    private lateinit var customerNames: String
    private lateinit var args: Biller
    lateinit var cardBinding: PaybillConfirmDialogBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(BillersViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaybillBinding.inflate(layoutInflater)
        args = PaybillFragmentArgs.fromBundle(requireArguments()).biller
        billerCode = args.billerCode
        transactionType = args.transactionType
        name = args.name
        viewmodel.billerName = name
        binding.tvTitle.text = String.format(getString(R.string.how_much_amount), "${args.name} ?")
        viewmodel.selectedCustomer.observe(viewLifecycleOwner) { customerInfo ->
            customerID = customerInfo.idNumber
            phoneNumber = customerInfo.phone
            binding.etPhone.setText(phoneNumber)
            customerNames = "${customerInfo.firstName} ${customerInfo.lastName}"
            Log.d("TAG", "onCreateView:PayBill: $customerID")
            if (isNetworkAvailable(requireContext())) {
                getWalletAccounts()
            } else {
                displayNoInternetSnackBar("Cannot fetch wallet accounts. Please connect to internet and retry")
            }
        }
        viewmodel.stopObserving()
        initUI()
        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                getBillPaymentPreview()
            }
        }
        return binding.root
    }

    private fun showConfirmDialog(billPaymentPreviewResponse: GetBillPaymentPreviewResponse) {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        cardBinding = PaybillConfirmDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.apply {
            val charge =
                FormatDigit.formatDigits(billPaymentPreviewResponse.data.charges.toString())
            tvHeading.text = getString(R.string.confirm_bill_payment).uppercase()
            currency = billPaymentPreviewResponse.data.paymentCurrency
            viewmodel.apply {
                paymentCurrency = currency
                customerName = customerNames
                walletName = binding.spAccount.text.toString()
            }
            totalAMount = FormatDigit.formatDigits(binding.etAmount.text.toString())
            tvAmountValue.text = "$currency $totalAMount"
            tvBankValue.text = binding.etAcNumber.text.toString()
            tvPayFromValue.text = binding.spAccount.text.toString()
            tvACNOValue.text = binding.etPhone.text.toString()
            tvCustomerNameValue.text = customerNames
            viewmodel.recipientName.observe(viewLifecycleOwner) {
                tvRecipientNameValue.text = it
            }
            if (args.categoryName.equals("School", true)) {
                tvRecipientName.text = "STUDENT NAME"
                tvBank.text="STUDENT NUMBER"
            }
            if (args.billerCode.equals("NRICHARDS", true) || args.billerCode.equals(
                    "GAIN",
                    true
                )
            ) {
                tvRecipientIDValue.text = etRecipientIDNumber.text.toString()
                tvRecipientIDValue.makeVisible()
                tvRecipientID.makeVisible()
            }
        }

        cardBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnSubmit.setOnClickListener {
            //findNavController().navigate(R.id.pinFragment)
            if (args.categoryName.equals("School", true)) {
                binding.apply {
                    val billPaymentDTO = BillPaymentDTO(
                        customerID,
                        etAmount.text.toString(),
                        args.transactionType,
                        walletAccountNumber,
                        etPhone.text.toString(),
                        etAcNumber.text.toString().uppercase(),
                        args.billerCode,
                        etSemester.text.toString(),
                        "",
                        "",
                        ""
                    )
                    viewmodel.postBillPayment(billPaymentDTO)
                }
            } else if (args.billerCode.equals("NRICHARDS", true) || args.billerCode.equals(
                    "GAIN",
                    true
                )
            ) {
                binding.apply {
                    val billPaymentDTO = BillPaymentDTO(
                        customerID,
                        etAmount.text.toString(),
                        args.transactionType,
                        walletAccountNumber,
                        etPhone.text.toString(),
                        etAcNumber.text.toString().uppercase(),
                        args.billerCode,
                        "",
                        etRecipientFirstName.text.toString(),
                        etRecipientLastName.text.toString(),
                        etRecipientIDNumber.text.toString()
                    )
                    viewmodel.postBillPayment(billPaymentDTO)
                }
            } else {
                binding.apply {
                    val billPaymentDTO = BillPaymentDTO(
                        customerID,
                        etAmount.text.toString(),
                        args.transactionType,
                        walletAccountNumber,
                        etPhone.text.toString(),
                        etAcNumber.text.toString().uppercase(),
                        args.billerCode,
                        "",
                        "",
                        "",
                        ""
                    )
                    viewmodel.postBillPayment(billPaymentDTO)
                }
            }
            dialog.dismiss()
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }

    private fun getBillPaymentPreview() {
        binding.apply {
            progressbar.tvWait.text = "Please wait..."
            viewmodel.getBillPaymentPreview(
                BillPaymentPreviewDTO(
                    etAmount.text.toString(), walletAccountNumber,
                    etAcNumber.text.toString(), args.billerCode
                )
            )
        }

    }

    private fun populateAccounts(list: List<WalletAccount>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout, list)
        binding.apply {
            spAccount.setAdapter(typeAdapter)
            spAccount.keyListener = null
            spAccount.setOnItemClickListener { parent, _, position, _ ->
                val selected: WalletAccount = parent.adapter.getItem(position) as WalletAccount
                val accNumber = selected.accountNo.replace("(?<=.{2}).(?=.{2})".toRegex(), "*")
                binding.spAccount.setText(
                    "${selected.accountName.capitalizeWords}-$accNumber",
                    false
                )
                walletAccountNumber = selected.accountNo
            }
        }
    }

    private fun initUI() {
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        showProgress(binding.btnSubmit, binding.progressbar.mainPBar)
                    }
                    GeneralResponseStatus.DONE -> {
                        hideProgress(binding.btnSubmit, binding.progressbar.mainPBar)
                    }
                    else -> {
                        hideProgress(binding.btnSubmit, binding.progressbar.mainPBar)
                    }
                }
            }
        }
        /*viewmodel.statusCode.observe(viewLifecycleOwner){
            Log.d("TAG", "getBillPaymentPreview: error ${it.toString()}")
        }*/
        viewmodel._billPaymentPreviewResponse.observe(viewLifecycleOwner) { billPaymentPreviewResponse ->
            Log.d("TAG", "getBillPaymentPreview: ${Gson().toJson(billPaymentPreviewResponse)}")
            if (billPaymentPreviewResponse !== null) {
                if (billPaymentPreviewResponse.status == 1) {
                    showConfirmDialog(billPaymentPreviewResponse)
                } else {
                    onInfoDialog(billPaymentPreviewResponse.message)
                }
            }
        }
        viewmodel._billPaymentResponse.observe(viewLifecycleOwner) { billPaymentResponse ->
            Log.d("TAG", "getBillPaymentPreview: ${Gson().toJson(billPaymentResponse)}")
            if (billPaymentResponse !== null) {
                if (billPaymentResponse.status == 1) {
                    viewmodel.postBillPaymentData.value = billPaymentResponse.data
                    findNavController().navigate(R.id.action_paybillFragment_to_billPaymentSuccessFragment)
                } else {
                    onInfoDialog(billPaymentResponse.message)
                }
            }
        }
        if (args.categoryName.equals("School", true)) {
            binding.apply {
                tiSemester.makeVisible()
                tlAcNo.hint = "Student Number"
            }
        }
        if (args.billerCode.equals("NRICHARDS", true) || args.billerCode.equals("GAIN", true)) {
            binding.apply {
                tiRecipientFirstName.makeVisible()
                tiRecipientLastName.makeVisible()
                tiRecipientIDNumber.makeVisible()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.stopObserving()
        viewmodel._billPaymentResponse.value = null
        viewmodel._billPaymentPreviewResponse.value = null
    }

    private fun isValid(): Boolean {
        var isValid: Boolean
        if (args.categoryName.equals("School", true)) {
            when {
                binding.etAmount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the amount")
                }
                binding.etAcNumber.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the student number")
                }
                binding.etSemester.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the academic semester")
                }
                binding.etPhone.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the phone number")
                }
                binding.spAccount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please select account")
                }
                else -> isValid = true
            }
        } else if (args.billerCode.equals("NRICHARDS", true) || args.billerCode.equals(
                "GAIN",
                true
            )
        ) {
            when {
                binding.etAmount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the amount")
                }
                binding.etAcNumber.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the account number")
                }
                binding.etPhone.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the phone number")
                }
                binding.spAccount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please select account")
                }
                binding.etRecipientFirstName.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the recipient first name")
                }
                binding.etRecipientLastName.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the recipient last name")
                }
                binding.etRecipientIDNumber.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the recipient ID Number")
                }
                else -> isValid = true
            }
        } else {
            when {
                binding.etAmount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the amount")
                }
                binding.etAcNumber.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the account number")
                }
                binding.etPhone.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please fill in the phone number")
                }
                binding.spAccount.text.toString().isEmpty() -> {
                    isValid = false
                    toastyErrors("Please select account")
                }
                else -> isValid = true
            }
        }
        return isValid
    }

    private fun getWalletAccounts() {
        binding.apply {
            progressbar.tvWait.text = "Fetching wallet accounts..."
        }
        val getWalletAccountsDTO = GetWalletAccountsDTO(customerID, 1)
        viewmodel.getCustomerWalletAccounts(getWalletAccountsDTO)
        viewmodel.walletAccountsList.observe(viewLifecycleOwner) { walletAccountsList ->
            if (walletAccountsList.isNotEmpty()) {
                Log.d("TAG", "getWalletAccounts: ${Gson().toJson(walletAccountsList)}")
                populateAccounts(walletAccountsList)
            } else {
                displayInfoSnackBar("No wallet accounts found")
            }
        }
    }

    private fun displayInfoSnackBar(message: String) {
        val snackBar = Snackbar.make(
            binding.container,
            message,
            Snackbar.LENGTH_INDEFINITE
        )

        snackBar.setAction("DISMISS") { // Call your action method here
            snackBar.dismiss()
        }

        snackBar.show()
    }

    private fun displayNoInternetSnackBar(message: String) {
        val snackBar = Snackbar.make(
            binding.container,
            message,
            Snackbar.LENGTH_INDEFINITE
        )

        snackBar.setAction("RETRY") { // Call your action method here
            snackBar.dismiss()
            getWalletAccounts()
        }

        snackBar.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().navigateUp()
                //findNavController().navigate(R.id.action_paybillFragment_to_billersFragment)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaybillFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}