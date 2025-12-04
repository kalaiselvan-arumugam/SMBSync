package com.mybackup.smbsync.data.repository

import com.mybackup.smbsync.data.local.SyncConfigurationDao
import com.mybackup.smbsync.data.model.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Sync Configuration operations
 */
@Singleton
class SyncConfigurationRepository @Inject constructor(
    private val syncConfigurationDao: SyncConfigurationDao
) {
    fun getAllConfigurations(): Flow<List<SyncConfiguration>> =
        syncConfigurationDao.getAllConfigurations()

    fun getEnabledConfigurations(): Flow<List<SyncConfiguration>> =
        syncConfigurationDao.getEnabledConfigurations()

    suspend fun getConfigurationById(id: Long): SyncConfiguration? =
        syncConfigurationDao.getConfigurationById(id)

    fun getConfigurationsByServer(serverId: Long): Flow<List<SyncConfiguration>> =
        syncConfigurationDao.getConfigurationsByServer(serverId)

    suspend fun insertConfiguration(config: SyncConfiguration): Long =
        syncConfigurationDao.insertConfiguration(config)

    suspend fun updateConfiguration(config: SyncConfiguration) =
        syncConfigurationDao.updateConfiguration(config)

    suspend fun deleteConfiguration(config: SyncConfiguration) =
        syncConfigurationDao.deleteConfiguration(config)

    suspend fun setEnabled(id: Long, enabled: Boolean) =
        syncConfigurationDao.setEnabled(id, enabled)

    suspend fun updateLastSync(id: Long, timestamp: Long) =
        syncConfigurationDao.updateLastSync(id, timestamp)
}
