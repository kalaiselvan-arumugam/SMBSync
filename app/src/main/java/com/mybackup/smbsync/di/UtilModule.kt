package com.mybackup.smbsync.di

import android.content.Context
import com.mybackup.smbsync.util.BatteryOptimizationHelper
import com.mybackup.smbsync.util.NetworkUtils
import com.mybackup.smbsync.util.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideBatteryOptimizationHelper(@ApplicationContext context: Context): BatteryOptimizationHelper {
        return BatteryOptimizationHelper(context)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }
}
