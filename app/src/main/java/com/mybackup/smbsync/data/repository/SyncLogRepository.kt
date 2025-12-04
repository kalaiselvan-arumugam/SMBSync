package com.mybackup.smbsync.data.repository

import com.mybackup.smbsync.data.local.SyncLogDao
import com.mybackup.smbsync.data.model.SyncLog
import com.mybackup.smbsync.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Sync Log operations
 */
@Singleton
class SyncLogRepository @Inject constructor(
    private val syncLogDao: SyncLogDao
) {
    fun getRecentLogsWithNames(limit: Int = 100): Flow<List<com.mybackup.smbsync.data.model.SyncLogWithName>> =
        syncLogDao.getRecentLogsWithNames(limit)
    
    fun getRecentLogs(limit: Int = 100): Flow<List<SyncLog>> =
        syncLogDao.getRecentLogs(limit)

    fun getLogsByConfig(configId: Long): Flow<List<SyncLog>> =
        syncLogDao.getLogsByConfig(configId)

    fun getLogsByStatus(status: SyncStatus): Flow<List<SyncLog>> =
        syncLogDao.getLogsByStatus(status)

    fun getLogsByDateRange(startTime: Long, endTime: Long): Flow<List<SyncLog>> =
        syncLogDao.getLogsByDateRange(startTime, endTime)

    suspend fun getLogById(id: Long): SyncLog? = syncLogDao.getLogById(id)

    suspend fun insertLog(log: SyncLog): Long = syncLogDao.insertLog(log)

    suspend fun deleteOldLogs(beforeTimestamp: Long) =
        syncLogDao.deleteOldLogs(beforeTimestamp)

    suspend fun deleteAllLogs() = syncLogDao.deleteAllLogs()

    suspend fun getLogCount(): Int = syncLogDao.getLogCount()
}
