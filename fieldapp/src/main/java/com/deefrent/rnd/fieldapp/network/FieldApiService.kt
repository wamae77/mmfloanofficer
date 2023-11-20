package com.deefrent.rnd.fieldapp.network

import android.util.Base64
import android.util.Log
import com.deefrent.rnd.common.data.request.UpdateFingerprintRegIdRequets
import com.deefrent.rnd.common.network.FieldInstance.retrofit
import com.deefrent.rnd.fieldapp.data.OccupationResponse
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentDTO
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentPreviewDTO
import com.deefrent.rnd.fieldapp.dtos.customer.GetWalletAccountsDTO
import com.deefrent.rnd.fieldapp.dtos.lookUp.IDLookUpDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.*
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.*
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.request.XaraniIdCheckRequest
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.response.XaraniIdCheckResponse
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.network.models.LoginResponse
import com.deefrent.rnd.fieldapp.responses.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import request.FuneralCashPlanSubscribeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**retrofit interface, where to define all our fun*/
interface FieldApiService {
    @POST("auth/verify-user")
    fun verifyUser(@Body verifyUserDTO: VerifyUserDTO): Deferred<GeneralPostResponse>

    @POST("auth/change-password")
    fun changePinAsync(@Body changePinDTO: ChangePinDTO): Deferred<ChangePinResponse>

    @POST("auth/check-if-registered")
    fun accountLookUpAsync(@Body accountLookUpDTO: AccountLookUpDTO): Deferred<AccountLookUpResponse>

    @POST("client/create/basic/initial")
    fun registerUserAsync(@Body personalDetailsDTO: PersonalDetailsDTO): Deferred<RegistrationResponse>

    @POST("user/teller-balance")
    fun getTellerAccountBalAsync(): Deferred<TellerAccountResponse>

    @POST("auth/verify-code")
    fun verifyOTPAsync(@Body verifyOtpDTO: VerifyOtpDTO): Deferred<Response<OTPResponse>>

    @POST("auth/resend-otp")
    fun resendOtpAsync(@Body resetPassDTO: ResetPassDTO): Deferred<Response<GeneralPostResponse>>

    @POST("auth/validate-default-pin")
    fun defaultPinAsync(@Body defaultPinDTO: DefaultPinDTO): Deferred<Response<GeneralPostResponse>>

    @POST("auth/change-default-pin")
    fun newPinAsync(@Body newPinDTO: NewPinDTO): Deferred<Response<GeneralPostResponse>>

    @POST("auth/login")
    fun loginAsync(@Body loginDTO: LoginDTO): Deferred<LoginResponse>

    @POST("auth/logout")
    fun logoutAsync(): Deferred<LogoutResponse>

    @POST("auth/get-security-questions")
    fun loadSecurityQuizAsync(): Deferred<SetSecurityQuizResponse>

    @POST("auth/set-security-answers")
    fun saveSecurityQuizAsync(@Body setSecQuizDTO: SetSecQuizDTO): Deferred<GeneralPostResponse>

    @POST("auth/verify-security-question")
    fun verifySecurityQuizAsync(@Body verifySecQuizDTO: VerifySecQuizDTO): Deferred<GeneralPostResponse>

    /**onboard customer*/
    @POST("auth/check-if-registered")
    fun customerAccLookUpAsync(@Body customerLookUpDTO: CustomerLookUpDTO): Deferred<CustomerLookUpResponse>

    @POST("client-reg-data-list/dropdown-items")
    fun getDropdownItemsAsync(): Deferred<DropdownItemResponse>

    @POST("client-reg-data-list/occupation-types")
    fun getOccupationItemsAsync(): Deferred<OccupationResponse>

    @POST("client-reg-data-list/villages")
    fun getVillagesAsync(): Deferred<VillagesResponse>

    @POST("client-reg-data-list/employment-status")
    fun getJobAsync(): Deferred<EmploymentResponse>

    @POST("client/onboard-customer")
    fun onboardCustomerAsync(@Body onboardCustomerDTO: OnboardCustomerDTO): Deferred<CustomerOnboardingResponse>

    @POST("client/find-customer-by-id-number")
    fun customerIDLookupAsync(@Body idLookUpDTO: CustomerIDLookUpDTO): Deferred<CustomerIDLookupResponse>

    @POST("client/find-customer-by-name")
    fun customerNameLookupAsync(@Body nameLookupDTO: NameLookupDTO): Deferred<LookUpByNameResponse>

    @POST("loan/repayment-schedule")
    fun getRepaymentScheduleAsync(@Body repaymentScheduleDTO: RepaymentScheduleDTO): Deferred<GetRepaymentSchedule>

    @POST("loan/statement")
    fun getLoanMiniStatementAsync(@Body repaymentScheduleDTO: RepaymentScheduleDTO): Deferred<GetLoanStatementResponse>

    @POST("client/find-customer-by-id-number")
    fun loanLookupAsync(@Body loanLookUpDTO: LoanLookUpDTO): Deferred<LoanLookupResponse>

    @POST("client/find-customer-by-name")
    fun loanLookupByNameAsync(@Body nameLookupDTO: NameLookupDTO): Deferred<LoanLookupByNameResponse>

    @POST("client/find-customer-by-id-number")
    fun idLookupAsync(@Body idLookUpDTO: IDLookUpDTO): Deferred<LookUpByIDResponse>

    @POST("client/find-customer-by-name")
    fun nameLookupAsync(@Body nameLookupDTO: NameLookupDTO): Deferred<LookUpByNameResponse>

    @POST("client/update-expense")
    fun addExpenses(@Body expensesDTO: ExpensesDTO): Deferred<GeneralPostResponse>

    @POST("loan/payment-frequency")
    fun getFrequency(): Deferred<FrequencyResponse>

    @POST("client/add-household-member")
    fun addMember(@Body incomeDTO: AddHouseHoldMember): Deferred<GeneralPostResponse>

    @POST("client/update-household-member")
    fun updateMember(@Body updateHouseholdMembersDTO: UpdateHouseholdMembersDTO): Deferred<GeneralPostResponse>

    @POST("client/incomplete-registrations")
    fun getIncompleteRegistrationAsync(): Deferred<IncompleteRegResponse>

    @POST("user/targets")
    fun getTargetsAsync(): Deferred<GetSalesTargetResponse>

    @POST("client/incomplete-assessment")
    fun getIncompleteAssessmentAsync(): Deferred<AssessmentResponse>

    @POST("client/full-details")
    fun getCustomerFullAsync(@Body idLookUpDTO: CustomerIDLookUpDTO): Deferred<FullCustomerDetailsResponse>

    @POST("loan/application-preview")
    fun loanRequestAsync(@Body loanRequestDTO: LoanRequestDTO): Deferred<GeneralPreviewResponse>

    @POST("loan/application-commit")
    fun loanCommitAsync(@Body formIDDTO: FormIDDTO): Deferred<GeneralCommitResponse>

    @POST("loan/repayment-preview")
    fun loanRepayPreviewAsync(@Body payLoanDTO: PayLoanDTO): Deferred<GeneralPreviewResponse>

    @POST("loan/repayment-preview-pg")
    fun loanRepayPreviewMpesa(@Body payLoanDTOMpesa: PayLoanDTOMpesa):Deferred<GeneralPreviewResponse>

    @POST("loan/repayment-commit-pg")
    fun loanRepayCommitAsyncMpesa(@Body formIDDTO: FormIDDTO): Deferred<GeneralCommitResponse>

    @POST("loan/repayment-commit")
    fun loanRepayCommitAsync(@Body formIDDTO: FormIDDTO): Deferred<GeneralCommitResponse>

    @POST("loan/active-loans")
    fun getPendingAsync(@Body pendingDisbDTO: pendingDisbDTO): Deferred<PendingDisburseResponse>

    @POST("client/cash-to-customer")
    fun cashOutLoansCommitAsync(@Body cashOutDTO: CashOutDTO): Deferred<GeneralCommitResponse>

    @POST("client/preview-cash-to-customer-charge")
    fun cashOutLoansAsync(@Body disburseLoanPreviewDTO: DisburseLoanPreviewDTO): Deferred<DisburseLoanPreview>

    @POST("loan/preview-disbursement-charge")
    fun disburseLoanAsync(@Body disburseLoanPreviewDTO: DisburseLoanPreviewDTO): Deferred<DisburseLoanPreview>

    @POST("loan/update-details")
    fun updateLoanAsync(@Body updateLoanDTO: UpdateLoanDTO): Deferred<GeneralPostResponse>

    @POST("loan/disburse")
    fun disburseLoanCommitAsync(@Body disburseLoanDTO: DisburseLoanDTO): Deferred<GeneralCommitResponse>

    @POST("client/update-basic-info")
    fun updateBasicInfoAsync(@Body updateBasicInfoDTO: UpdateBasicInfoDTO): Deferred<GeneralPostResponse>

    @POST("client/update-basic-info")
    fun updateAdditionalnfoAsync(@Body updateAdditionalInfoDTO: UpdateAdditionalInfoDTO): Deferred<GeneralPostResponse>

    @POST("client/update-basic-info")
    fun updateBusinessDetailsAsync(@Body updateBusinessDetailsDTO: UpdateBusinessDetailsDTO): Deferred<GeneralPostResponse>

    @POST("client/update-basic-info")
    fun updateBusinessAddressAsync(@Body updateBusinessAddressDTO: UpdateBusinessAddressDTO): Deferred<GeneralPostResponse>

    @POST("client/update-guarantor")
    fun updateGuarantorAsync(@Body updateGuarantorsDTO: UpdateGuarantorsDTO): Deferred<GeneralPostResponse>

    @POST("client/update-basic-info")
    fun updateResidentialnfoAsync(@Body updateResidentialInfoDTO: UpdateResidentialInfoDTO): Deferred<GeneralPostResponse>

    @POST("client/update-collateral")
    fun updateCollateralAsync(@Body updateCollateralDTO: UpdateCollateralDTO): Deferred<GeneralPostResponse>

    @POST("client/remove-collateral")
    fun deleteCollateralAsync(@Body deleteCollateralDTO: DeleteCollateralDTO): Deferred<GeneralPostResponse>

    @POST("client/add-collateral")
    fun addCollateralAsync(@Body addCollateralDTO: AddCollateralDTO): Deferred<GeneralPostResponse>

    @POST("client/update-other-borrowing")
    fun updateBorrowingAsync(@Body updateBorrowingsDTO: UpdateBorrowingsDTO): Deferred<GeneralPostResponse>

    @POST("client/add-other-borrowing")
    fun addBorrowingAsync(@Body addBorrowingDTO: AddBorrowingDTO): Deferred<GeneralPostResponse>

    @POST("client/remove-other-borrowing")
    fun deleteBorrowingAsync(@Body deleteBorrowingDTO: DeleteBorrowingDTO): Deferred<GeneralPostResponse>

    @POST("client/update-basic-info")
    fun updateNOkInfoAsync(@Body updateNokDTO: UpdateNokDTO): Deferred<GeneralPostResponse>

    @POST("client/remove-guarantor")
    fun removeGuarantorAsync(@Body removeGuarantorDTO: RemoveGuarantorDTO): Deferred<GeneralPostResponse>

    @POST("client/household-member")
    fun removeMembeAsync(@Body deleteMemberDTO: DeleteMemberDTO): Deferred<GeneralPostResponse>

    @POST("client/add-guarantor")
    fun addGuarantorAsync(@Body addGuarantorsDTO: AddGuarantorsDTO): Deferred<GeneralPostResponse>

    @POST("client/assess-customer")
    fun assessCustomerAsync(@Body assessCustomerDTO: AssessCustomerDTO): Deferred<GeneralPostResponse>

    @POST("client/analyse-credit-score")
    fun checkCreditScoreAsync(@Body checkCreditScoreDTO: CheckCreditScoreDTO): Deferred<GetCreditScoreResponse>

    @POST("client/documents")
    fun getDocumentAsync(@Body docDTO: DocDTO): Deferred<DocumentResponse>

    @POST("client-reg-data-list/document-types")
    fun getDocumentTypesAsync(): Deferred<GetDocumentTypesResponse>

    @POST("client/find-billers")
    fun getBillersAsync(): Deferred<BillersResponse>

    @POST("saving-account/index")
    fun getCustomerWalletAccountsAsync(@Body getWalletAccountsDTO: GetWalletAccountsDTO): Deferred<GetWalletAccountsResponse>

    @POST("client/bill-transaction-preview")
    fun getBillPaymentPreviewAsync(@Body billPaymentPreviewDTO: BillPaymentPreviewDTO): Deferred<GetBillPaymentPreviewResponse>

    @POST("client/post-transaction")
    fun postBillPaymentAsync(@Body billPaymentDTO: BillPaymentDTO): Deferred<PostBillPaymentResponse>

    @POST("user/teller-account-statement")
    fun getTellerAccountBalances(): Deferred<TellerAccountStatmentResponse>

    @POST("user/diary-list")
    fun getMyDiary(): Deferred<DiaryResponse>

    @POST("user/mileage-list")
    fun getMileage(): Deferred<GetMileageResponse>

    @POST("user/add-diary")
    fun addMyDiary(@Body addDiaryDTO: AddDiaryDTO): Deferred<GeneralPostResponse>

    @POST("user/update-diary")
    fun updateMyDiary(@Body updateDiaryDTO: UpdateDiaryDTO): Deferred<GeneralPostResponse>

    @POST("user/add-mileage")
    fun addMileage(@Body addMileageDTO: AddMileageDTO): Deferred<GeneralPostResponse>

    @POST("user/update-mileage")
    fun updateMileage(@Body updateMileageDTO: UpdateMileageDTO): Deferred<GeneralPostResponse>

    @Multipart
    @POST("client/create/basic/final")
    fun uploadIdPhotoAsync(
        @Part vararg body: MultipartBody.Part,
        @Part("form_id") formId: RequestBody,
    ): Deferred<UserRegistrationResponse>

    //upload customer documents
    @Multipart
    @POST("client/upload-document")
    fun uploadDocumentAsync(
        @Part("customerId") customerId: RequestBody,
        @Part("docTypeCode") docTypeCode: RequestBody,
        @Part("channelGeneratedCode") channelGeneratedCode: RequestBody,
        @Part file: MultipartBody.Part
    ): Deferred<UploadDocumentResponse>

    @Multipart
    @POST("client/upload-document")
    fun uploadDocument2Async(
        @Part("customerId") customerId: RequestBody,
        @Part("docTypeCode") docTypeCode: RequestBody,
        @Part file: MultipartBody.Part
    ): Deferred<UploadDocumentResponse>

    @Multipart
    @POST("client/upload-document")
    fun uploadCollateralDocumentAsync(
        @Part("customerId") customerId: RequestBody,
        @Part("docTypeCode") docTypeCode: RequestBody,
        @Part("channelGeneratedCode") channelGeneratedCode: RequestBody,
        @Part file: MultipartBody.Part
    ): Deferred<UploadDocumentResponse>

    @Multipart
    @POST("client/upload-document")
    fun uploadGuarantorDocumentAsync(
        @Part("customerId") customerId: RequestBody,
        @Part("docTypeCode") docTypeCode: RequestBody,
        @Part("channelGeneratedCode") channelGeneratedCode: RequestBody,
        @Part file: MultipartBody.Part
    ): Deferred<UploadDocumentResponse>

    @POST("client/update-basic-info")
    fun updateBasicInfoAsync(
        @Part("id_number") id_number: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender_id") gender_id: RequestBody,
        @Part("spouse_name") spouse_name: RequestBody,
        @Part("spouse_name") spouse_phone: RequestBody,
        @Part file: MultipartBody.Part
    ): Deferred<GeneralPostResponse>

    @Multipart
    @POST("client/update-income")
    fun addIncome(
        @Part("id_number") id_number: RequestBody,
        @Part("household_income_net_salary") household_income_net_salary: RequestBody,
        @Part("household_income_own_salary") household_income_own_salary: RequestBody,
        @Part("household_income_total_sales") household_income_total_sales: RequestBody,
        @Part("household_income_profit") household_income_profit: RequestBody,
        @Part("household_income_rental_income") household_income_rental_income: RequestBody,
        @Part("household_income_remittance_donations") household_income_remittance_donations: RequestBody,
        @Part("household_income_other") household_income_other: RequestBody
    ): Deferred<GeneralPostResponse>


    /**
     * FUNERAL CASH PLAN API ENDPOINTS
     */
    @POST("client/find-customer-by-id-number")
    suspend fun findCustomerByIdNumber(
        @Body findCustomerByIdNumberRequest: FindCustomerByIdNumberRequest
    ): FindCustomerByIdNumberResponse

    @POST("client/find-customer-by-name")
    suspend fun findCustomerByName(@Body findCustomerByNameRequest: FindCustomerByNameRequest): FindCustomerByNameResponse

    @POST("client/cash-plan-packages")
    suspend fun getCashPlanPackages(
        @Body cashPlanPackagesRequest: CashPlanPackagesRequest,
    ): CashPlanPackagesResponse


    @POST("client/cash-plan-get-payable-amount")
    suspend fun getPayableAmount(@Body getPayableAmountRequest: GetPayableAmountRequest): GetPayableAmountResponse

    @POST("client/cash-plan-subscribe")
    suspend fun cashPlanSubscribe(@Body funeralCashPlanSubscribeRequest: FuneralCashPlanSubscribeRequest): SubscribeResponse

    @POST("saving-account/index")
    suspend fun getSavingAcc(@Body savingAccDTO: SavingAccDTO): SavingAccountsResponse

    @POST("client/cash-plan-subscriptions")
    suspend fun cashPlanSubscriptions(
        @Body cashPlanPackagesRequest: CashPlanPackagesRequest
    ): CashPlanSubscriptionsPoliciesResponse

    @POST("client/cash-plan-subscriptions")
    suspend fun cashPlanSubscriptionPay(
        @Body cashPlanSubscriptionPayRequest: CashPlanSubscriptionPayRequest
    ): CommonResponse

    @POST("auth/verify-user")
    suspend fun verifyUserIsMe(@Body verifyUserDTO: VerifyUserDTO): CommonResponse


    @POST("client/verify-id-number")
    suspend fun xaraniIdCheck(@Body xaraniIdCheckRequest: XaraniIdCheckRequest): XaraniIdCheckResponse


    @Multipart
    @POST("Services/create_service_Services_create_service_post")
    suspend fun enrollUser(
        @Header("Authorization") authorization: String = BASIC,
        @Header("service_name") service_name: String = "the_thumb",
        @Part("phone") phone: RequestBody,
        @Part("finger_index") finger_index: RequestBody,
        @Part("hand_type") hand_type: RequestBody,
        @Part image: MultipartBody.Part? = null,
    ): CommonResponse


    @POST("client/update-fingerprint-reg-id")
    suspend fun updateFingerprintRegId(
        @Body updateFingerprintRegIdRequets: UpdateFingerprintRegIdRequets
    ): CommonResponse

}


private var CREDENTIALS: String = "e3230f27dabbb85520230603-094936f03d" + ":" + "b0bf545d85908dbae3230f27dabbb85520230603-094936f03dab3e20230603-094936"
// create Base64 encodet string
val BASIC = "Basic " + Base64.encodeToString(CREDENTIALS.toByteArray(), Base64.NO_WRAP)
/**object that we will later use in all our fun to make the api calls*/
object FieldAgentApi {
    val retrofitService: FieldApiService by lazy {
        retrofit.create(FieldApiService::class.java).apply {
            Log.d("TAG", "BASE URL: ${retrofit.baseUrl()}")
        }
    }
}