package com.deefrent.rnd.fieldapp.di.injectables

import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.di.ViewModelKey
import com.deefrent.rnd.fieldapp.MainActivity
import com.deefrent.rnd.fieldapp.ui.dashboard.DashboardViewModel
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.LoginFingerPrintCaptureActivity
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.EnrollFingerPrintCaptureActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FieldAppActivityModule {

    /**
     * Main Activity
     */
    ///////////////////////////////////////////////////////////////////////////////////
    @ContributesAndroidInjector(modules = [TourismMainActivityModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [TourismMainActivityModule::class])
    abstract fun contributeFingerPrintCaptureActivity(): EnrollFingerPrintCaptureActivity

    @ContributesAndroidInjector(modules = [TourismMainActivityModule::class])
    abstract fun contributeLoginFingerPrintCaptureActivity(): LoginFingerPrintCaptureActivity

    @Module
    abstract class TourismMainActivityModule {
        @Binds
        @IntoMap
        @ViewModelKey(DashboardViewModel::class)
        abstract fun bindLoginViewModel(viewModel: DashboardViewModel): ViewModel
    }

    ///////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * Slider
//     */
//    ///////////////////////////////////////////////////////////////////////////////////
//    @ContributesAndroidInjector(modules = [SmeSliderActivityModule::class])
//    abstract fun contributeSliderActivity(): SliderActivity
//
//    @Module
//    abstract class SmeSliderActivityModule {
//
//        @Binds
//        @IntoMap
//        @ViewModelKey(CountryViewModel::class)
//        abstract fun bindCountryViewModel(viewModel: CountryViewModel): ViewModel
//    }

    ///////////////////////////////////////////////////////////////////////////////////

    //LIST ALL OTHER ACTIVITIES IN THIS MODULE
}
