package com.mybackup.smbsync.di

import android.content.Context
import androidx.room.Room
import com.mybackup.smbsync.data.local.AppDatabase
import com.mybackup.smbsync.data.local.SmbServerDao
import com.mybackup.smbsync.data.local.SyncConfigurationDao
import com.mybackup.smbsync.data.local.SyncLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mybackup_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSmbServerDao(database: AppDatabase): SmbServerDao {
        return database.smbServerDao()
    }

    @Provides
    fun provideSyncConfigurationDao(database: AppDatabase): SyncConfigurationDao {
        return database.syncConfigurationDao()
    }

    @Provides
    fun provideSyncLogDao(database: AppDatabase): SyncLogDao {
        return database.syncLogDao()
    }
}
