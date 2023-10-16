package com.deefrent.rnd.fieldapp.di

import com.deefrent.rnd.jiboostfieldapp.di.AppComponent
import com.deefrent.rnd.jiboostfieldapp.di.ModuleScope
import com.deefrent.rnd.jiboostfieldapp.di.injectables.ViewModelModule
import com.deefrent.rnd.fieldapp.di.injectables.FieldAppActivityModule
import com.deefrent.rnd.fieldapp.di.injectables.FieldAppFragmentModule
import com.deefrent.rnd.fieldapp.di.injectables.FieldAppOtherModules
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@ModuleScope
@Component(
    dependencies = [
        AppComponent::class
    ],
    modules = [
        AndroidSupportInjectionModule::class,
        FieldAppActivityModule::class,
        FieldAppFragmentModule::class,
        ViewModelModule::class,
        FieldAppOtherModules::class
    ]
)
interface FieldAppComponent {
    fun inject(injector: FieldAppInjector)
    //fun inject(fragment: LoginFragment)
}