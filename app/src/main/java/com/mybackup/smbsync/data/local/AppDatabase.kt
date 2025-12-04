package com.mybackup.smbsync.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mybackup.smbsync.data.model.SmbServer
import com.mybackup.smbsync.data.model.SyncConfiguration
import com.mybackup.smbsync.data.model.SyncLog

/**
 * Main Room database for MyBackup
 */
@Database(
    entities = [
        SmbServer::class,
        SyncConfiguration::class,
        SyncLog::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smbServerDao(): SmbServerDao
    abstract fun syncConfigurationDao(): SyncConfigurationDao
    abstract fun syncLogDao(): SyncLogDao
}
