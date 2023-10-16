package com.deefrent.rnd.jiboostfieldapp.di.injectables

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindsViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    /*@Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory*/
}
