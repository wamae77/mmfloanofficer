package com.deefrent.rnd.jiboostfieldapp.di

import android.app.Application
import android.content.Context
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.repo.SampleDataRepo
import com.deefrent.rnd.common.repo.SampleRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(app: BaseApp): Context {
        return app
    }

    @Singleton
    @Provides
    fun provideSampleRepository(context: Context): SampleRepository {
        return SampleDataRepo(context)
    }

    @Singleton
    @Provides
    fun provideApp(context: Context): Application {
        return context.applicationContext as Application
    }
}