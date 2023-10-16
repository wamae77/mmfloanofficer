package com.deefrent.rnd.fieldapp.di

import android.app.Activity
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.deefrent.rnd.fieldapp.di.injectables.FieldAppOtherModules
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.jiboostfieldapp.di.BaseModuleInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

@Keep
class FieldAppInjector : BaseModuleInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun inject(app: BaseApp) {
        DaggerFieldAppComponent.builder()
            .appComponent(app.appComponent)
            .fieldAppOtherModules(FieldAppOtherModules(app.applicationContext))
            .build()
            .inject(this)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return activityInjector
    }

    override fun fragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return fragmentInjector
    }
}
