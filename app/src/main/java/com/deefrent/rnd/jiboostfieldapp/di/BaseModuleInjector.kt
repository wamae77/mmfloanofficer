package com.deefrent.rnd.jiboostfieldapp.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import dagger.android.DispatchingAndroidInjector

interface BaseModuleInjector {

    fun inject(app: BaseApp)

    fun activityInjector(): DispatchingAndroidInjector<Activity>

    fun fragmentInjector(): DispatchingAndroidInjector<Fragment>

}
