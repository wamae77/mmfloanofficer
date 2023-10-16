package com.deefrent.rnd.jiboostfieldapp.di.injectables

import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.di.ViewModelKey
import com.deefrent.rnd.jiboostfieldapp.ui.main.MainFragment
import com.deefrent.rnd.jiboostfieldapp.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    abstract fun contributeMainFragment(): MainFragment


    @Module
    abstract class MainFragmentModule {
        @Binds
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
    }
}