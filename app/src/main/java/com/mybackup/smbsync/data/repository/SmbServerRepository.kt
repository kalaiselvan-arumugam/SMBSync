package com.mybackup.smbsync.data.repository

import com.mybackup.smbsync.data.local.SmbServerDao
import com.mybackup.smbsync.data.model.SmbServer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for SMB Server operations
 */
@Singleton
class SmbServerRepository @Inject constructor(
    private val smbServerDao: SmbServerDao
) {
    fun getAllServers(): Flow<List<SmbServer>> = smbServerDao.getAllServers()

    suspend fun getServerById(id: Long): SmbServer? = smbServerDao.getServerById(id)

    suspend fun insertServer(server: SmbServer): Long = smbServerDao.insertServer(server)

    suspend fun updateServer(server: SmbServer) = smbServerDao.updateServer(server)

    suspend fun deleteServer(server: SmbServer) = smbServerDao.deleteServer(server)

    suspend fun updateLastConnected(id: Long, timestamp: Long) =
        smbServerDao.updateLastConnected(id, timestamp)
}
