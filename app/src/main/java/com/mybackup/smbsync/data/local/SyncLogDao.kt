package com.mybackup.smbsync.data.local

import androidx.room.*
import com.mybackup.smbsync.data.model.SyncLog
import com.mybackup.smbsync.data.model.SyncLogWithName
import com.mybackup.smbsync.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Sync Log operations
 */
@Dao
interface SyncLogDao {
    @Query("""
        SELECT sync_logs.*, sync_configurations.name as configName 
        FROM sync_logs 
        LEFT JOIN sync_configurations ON sync_logs.configId = sync_configurations.id 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun getRecentLogsWithNames(limit: Int = 100): Flow<List<SyncLogWithName>>
    
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<SyncLog>>

    @Query("SELECT * FROM sync_logs WHERE configId = :configId ORDER BY timestamp DESC")
    fun getLogsByConfig(configId: Long): Flow<List<SyncLog>>

    @Query("SELECT * FROM sync_logs WHERE status = :status ORDER BY timestamp DESC")
    fun getLogsByStatus(status: SyncStatus): Flow<List<SyncLog>>

    @Query("SELECT * FROM sync_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getLogsByDateRange(startTime: Long, endTime: Long): Flow<List<SyncLog>>

    @Query("SELECT * FROM sync_logs WHERE id = :id")
    suspend fun getLogById(id: Long): SyncLog?

    @Insert
    suspend fun insertLog(log: SyncLog): Long

    @Query("DELETE FROM sync_logs WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOldLogs(beforeTimestamp: Long)

    @Query("DELETE FROM sync_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT COUNT(*) FROM sync_logs")
    suspend fun getLogCount(): Int
}
