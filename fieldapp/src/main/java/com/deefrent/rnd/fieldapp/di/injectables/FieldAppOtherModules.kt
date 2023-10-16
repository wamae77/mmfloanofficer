package com.deefrent.rnd.fieldapp.di.injectables

import android.content.Context
import com.deefrent.rnd.common.data.roomdb.MoneyMartAppDatabase
import com.deefrent.rnd.common.network.NetworkInterceptor
import com.deefrent.rnd.common.network.RemoteDataSource
import com.deefrent.rnd.fieldapp.network.FieldApiService
import com.deefrent.rnd.jiboostfieldapp.di.ModuleScope
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.utils.Constants.FINGERPRINT_URL
import com.deefrent.rnd.fieldapp.network.repos.FingerPrintApiService
import dagger.Module
import dagger.Provides

@Module
class FieldAppOtherModules(private val context: Context) {

    @ModuleScope
    @Provides
    fun provideNetworkInterceptor(): NetworkInterceptor {
        return NetworkInterceptor(context)
    }

    @ModuleScope
    @Provides
    fun provideRemoteDataSource(networkInterceptor: NetworkInterceptor): RemoteDataSource {
        return RemoteDataSource(networkInterceptor)
    }

    @ModuleScope
    @Provides
    fun provideApiService(remoteDataSource: RemoteDataSource): FieldApiService {
        return remoteDataSource.buildApi(
            FieldApiService::class.java,
            Constants.BASE_URL
        )
    }

    @ModuleScope
    @Provides
    fun provideFingerPrintApiService(remoteDataSource: RemoteDataSource): FingerPrintApiService {
        return remoteDataSource.buildApiForFingerPrint(
            FingerPrintApiService::class.java,
            FINGERPRINT_URL
        )
    }

    @ModuleScope
    @Provides
    fun provideMoneyMartAppDatabase(): MoneyMartAppDatabase {
        return MoneyMartAppDatabase(context)
    }

    @ModuleScope
    @Provides
    fun provideCommonSharedPreferences(): CommonSharedPreferences {
        return CommonSharedPreferences(context)
    }

    /*

        @ModuleScope
        @Provides
        fun provideAppDatabase(): FarmerAppDatabase {
            return FarmerAppDatabase(context)
        }
        @ModuleScope
        @Provides
        fun provideCommonAppDatabase(): CommonAppDatabase {
            return CommonAppDatabase(context)
        }


        @ModuleScope
        @Provides
        fun provideDataPreferences(): CommonCargillDataPreferences {
            return CommonCargillDataPreferences(context)
        }


        @ModuleScope
        @Provides
        fun provideGoogleSMSReceiver(): SMSGoogleReceiverNoUserConsent {
            return SMSGoogleReceiverNoUserConsent()
        }
    */


}