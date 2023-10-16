package com.deefrent.rnd.fieldapp.di.injectables

import androidx.lifecycle.ViewModel
import com.deefrent.rnd.common.auth.CommonAuthFragment
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.LoginWithFingerPrintFragment
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.EnrollFingerPrintFragmentMethod1
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.EnrollFingerPrintFragmentMethod2
import com.deefrent.rnd.jiboostfieldapp.di.ViewModelKey
import com.deefrent.rnd.fieldapp.view.auth.onboarding.AccountLookUpViewModel
import com.deefrent.rnd.fieldapp.view.auth.userlogin.ConfirmNewPinFragment
import com.deefrent.rnd.fieldapp.view.auth.userlogin.CreateNewPinFragment
import com.deefrent.rnd.fieldapp.view.auth.onboarding.PhoneVerificationFragment
import com.deefrent.rnd.fieldapp.view.auth.userlogin.LoginPinFragment
import com.deefrent.rnd.fieldapp.ui.home.HomeFragment
import com.deefrent.rnd.fieldapp.ui.home.HomeViewModel
import com.deefrent.rnd.fieldapp.ui.main.fragments.LoginFragment
import com.deefrent.rnd.fieldapp.view.auth.AuthFingerPrintFragment
import com.deefrent.rnd.fieldapp.view.homepage.DashboardFragment
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.*
import com.deefrent.rnd.fieldapp.view.feedbacks.FeedBackFragment
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.EnrollOneFingerPrintFragment
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.UpdateCustomerFingerPrintFragment
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.*
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies.CollectPremiumsFragment
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies.CustomerPoliciesFuneralBreakDownFragment
import com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan.customerpolicies.CustomerPoliciesFuneralCashPlanFragment
import com.deefrent.rnd.fieldapp.view.homepage.loans.apply.ApplyLoanFragment
import com.deefrent.rnd.fieldapp.view.homepage.loans.disburseloan.DisburseLoanFragment
import com.deefrent.rnd.fieldapp.view.homepage.loans.payloan.LoanPaymentFragment
import com.deefrent.rnd.fieldapp.view.homepage.offlinetransaction.OfflineRegFragment
import com.deefrent.rnd.fieldapp.view.quickreports.QuickReportsFragment
import com.deefrent.rnd.fieldapp.view.success.GeneralSuccessfulFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FieldAppFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginSessionSharedViewModel::class)
    abstract fun bindsSharedViewModel(viewModel: LoginSessionSharedViewModel): ViewModel

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    abstract fun contributeLoginDialogFragment(): LoginFragment

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    abstract fun contributeCommonAuthFragment(): CommonAuthFragment

    @ContributesAndroidInjector(modules = [PhoneVerificationFragmentModule::class])
    abstract fun contributePhoneVerificationFragment(): PhoneVerificationFragment

    @Module
    abstract class PhoneVerificationFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(AccountLookUpViewModel::class)
        abstract fun bindsSharedViewModel(viewModel: AccountLookUpViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [LoginPinFragmentModule::class])
    abstract fun contributeLoginPinFragment(): LoginPinFragment

    @Module
    abstract class LoginPinFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(AccountLookUpViewModel::class)
        abstract fun bindsAccViewModel(viewModel: AccountLookUpViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [ConfirmNewPinFragmentModule::class])
    abstract fun contributeConfirmNewPinFragment(): ConfirmNewPinFragment

    @ContributesAndroidInjector(modules = [CreateNewPinFragmentModule::class])
    abstract fun contributeCreateNewPinFragment(): CreateNewPinFragment

    @Module
    abstract class LoginFragmentModule {
        /*@Binds
        @IntoMap
        @ViewModelKey(LoginViewModel::class)
        abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(HomeViewModel::class)
        abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel*/
    }


    @Module
    abstract class ConfirmNewPinFragmentModule {
        //
    }

    @Module
    abstract class CreateNewPinFragmentModule {
        //
    }

    @ContributesAndroidInjector(modules = [TourismHomeFragmentModule::class])
    abstract fun contributeHomeFragment(): HomeFragment

    @Module
    abstract class TourismHomeFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(HomeViewModel::class)
        abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [CustomerAdditionalDetailsFragmentModule::class])
    abstract fun contributeCustomerAdditionalDetailsFragment(): Step12CustomerAdditionalDetailsFragment

    @Module
    abstract class CustomerAdditionalDetailsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [CustomerCustomerDetailsFragmentModule::class])
    abstract fun contributeCustomerDetailsFragment(): Step2CustomerDetailsFragment

    @Module
    abstract class CustomerCustomerDetailsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [DashboardFragmentModule::class])
    abstract fun contributeDashboardFragment(): DashboardFragment

    @Module
    abstract class DashboardFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [BusinesDetailsFragmentModule::class])
    abstract fun contributeBusinesDetailsFragment(): Step3BusinesDetailsFragment

    @Module
    abstract class BusinesDetailsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [BusinessAddressFragmentModule::class])
    abstract fun contributeBusinessAddressFragment(): Step3BusinessAddressFragment

    @Module
    abstract class BusinessAddressFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [GuarantorsFragmentModule::class])
    abstract fun contributeGuarantorsFragment(): Step6GuarantorsFragment

    @Module
    abstract class GuarantorsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [SummaryFragmentModule::class])
    abstract fun contributeSummaryFragmentt(): SummaryFragment

    @Module
    abstract class SummaryFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [ResidentialDetailsFragmentModule::class])
    abstract fun contributeResidentialDetailsFragment(): Step5ResidentialDetailsFragment

    @Module
    abstract class ResidentialDetailsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [NextOfKinFragmentModule::class])
    abstract fun contributeNextOfKinFragmentFragment(): Step8NextOfKinFragment

    @Module
    abstract class NextOfKinFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [CollateralsFragmentModule::class])
    abstract fun contributeCollateralsFragment(): Step4CollateralsFragment

    @Module
    abstract class CollateralsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [OtherBorrowingsFragmentModule::class])
    abstract fun contributeOtherBorrowingsFragment(): Step7OtherBorrowingsFragment

    @Module
    abstract class OtherBorrowingsFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [CustomerLookupFragmentModule::class])
    abstract fun contributeCustomerLookupFragment(): Step1CustomerLookupFragment

    @ContributesAndroidInjector(modules = [CustomerLookupFragmentModule::class])
    abstract fun contributeQuickReportsFragment(): QuickReportsFragment

    @ContributesAndroidInjector(modules = [CustomerLookupFragmentModule::class])
    abstract fun contributeFeedBackFragment(): FeedBackFragment

    @Module
    abstract class CustomerLookupFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(OnboardCustomerViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: OnboardCustomerViewModel): ViewModel
    }

    /**
     * Funeral Cash Plan
     */
    @Module
    abstract class FuneralCashPlanViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(FuneralCashPlanViewModel::class)
        abstract fun bindOnboardCustomerViewModel(viewModel: FuneralCashPlanViewModel): ViewModel
    }

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep1IdLookUpFuneralCashPlanFragment(): Step1LookUpFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep2DetailsFuneralCashPlanFragment(): Step2DetailsFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeFuneralCashPlanFragment(): Step3PackagesFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep4PackageFuneralCashPlanFragment(): Step4PackageFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep5PackageFuneralCashPlanFragment(): Step5PackageFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep6PackageFuneralCashPlanFragment(): Step6PackageFuneralCashPlanFragment


    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeCustomerPoliciesFuneralCashPlanFragment(): CustomerPoliciesFuneralCashPlanFragment


    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeCustomerPoliciesFuneralBreakDownFragment(): CustomerPoliciesFuneralBreakDownFragment


    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeCollectPremiumsFragment(): CollectPremiumsFragment


    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeStep1CustomerListFuneralCashPlanFragment(): Step1CustomerListFuneralCashPlanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeCaptureFingerPrintFragment(): LoginWithFingerPrintFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contributeEnrollFingerPrintFragment(): EnrollFingerPrintFragmentMethod1

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_EnrollFingerPrintFragmentMethod2(): EnrollFingerPrintFragmentMethod2

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_Step13EnrollFingerPrintFragmentMethod1(): Step13EnrollFingerPrintFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_LoanPaymentFragment(): LoanPaymentFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_ApplyLoanFragment(): ApplyLoanFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_DisburseLoanFragment(): DisburseLoanFragment


    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_OfflineRegFragment(): OfflineRegFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_SuccessfulWalletServicesFragment(): GeneralSuccessfulFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_UpdateCustomerFingerPrintFragment(): UpdateCustomerFingerPrintFragment

    @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_AuthFingerPrintFragment(): AuthFingerPrintFragment
 @ContributesAndroidInjector(modules = [FuneralCashPlanViewModelModule::class])
    abstract fun contribute_EnrollOneFingerPrintFragment(): EnrollOneFingerPrintFragment


}
