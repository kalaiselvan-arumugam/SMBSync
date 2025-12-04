package com.mybackup.smbsync.data.local

import androidx.room.*
import com.mybackup.smbsync.data.model.SmbServer
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SMB Server operations
 */
@Dao
interface SmbServerDao {
    @Query("SELECT * FROM smb_servers ORDER BY name ASC")
    fun getAllServers(): Flow<List<SmbServer>>

    @Query("SELECT * FROM smb_servers WHERE id = :id")
    suspend fun getServerById(id: Long): SmbServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: SmbServer): Long

    @Update
    suspend fun updateServer(server: SmbServer)

    @Delete
    suspend fun deleteServer(server: SmbServer)

    @Query("UPDATE smb_servers SET lastConnectedAt = :timestamp WHERE id = :id")
    suspend fun updateLastConnected(id: Long, timestamp: Long)
}
