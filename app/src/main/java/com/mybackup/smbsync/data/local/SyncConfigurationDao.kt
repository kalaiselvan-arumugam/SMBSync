package com.mybackup.smbsync.data.local

import androidx.room.*
import com.mybackup.smbsync.data.model.SyncConfiguration
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Sync Configuration operations
 */
@Dao
interface SyncConfigurationDao {
    @Query("SELECT * FROM sync_configurations ORDER BY name ASC")
    fun getAllConfigurations(): Flow<List<SyncConfiguration>>

    @Query("SELECT * FROM sync_configurations WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledConfigurations(): Flow<List<SyncConfiguration>>

    @Query("SELECT * FROM sync_configurations WHERE id = :id")
    suspend fun getConfigurationById(id: Long): SyncConfiguration?

    @Query("SELECT * FROM sync_configurations WHERE serverId = :serverId")
    fun getConfigurationsByServer(serverId: Long): Flow<List<SyncConfiguration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguration(config: SyncConfiguration): Long

    @Update
    suspend fun updateConfiguration(config: SyncConfiguration)

    @Delete
    suspend fun deleteConfiguration(config: SyncConfiguration)

    @Query("UPDATE sync_configurations SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE sync_configurations SET lastSyncAt = :timestamp WHERE id = :id")
    suspend fun updateLastSync(id: Long, timestamp: Long)
}
