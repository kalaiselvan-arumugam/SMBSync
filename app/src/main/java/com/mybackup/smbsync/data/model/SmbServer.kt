package com.mybackup.smbsync.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SMB Server configuration entity
 */
@Entity(tableName = "smb_servers")
data class SmbServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val port: Int = 445,
    val protocol: SmbProtocol = SmbProtocol.SMB3,
    val username: String,
    val encryptedPassword: String, // Encrypted using Android Keystore
    val domain: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastConnectedAt: Long? = null
)

enum class SmbProtocol {
    SMB1, SMB2, SMB3
}
