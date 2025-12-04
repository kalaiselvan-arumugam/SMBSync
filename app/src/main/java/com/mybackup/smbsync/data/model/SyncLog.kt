package com.mybackup.smbsync.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sync operation log entity
 */
@Entity(
    tableName = "sync_logs",
    foreignKeys = [
        ForeignKey(
            entity = SyncConfiguration::class,
            parentColumns = ["id"],
            childColumns = ["configId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("configId"), Index("timestamp")]
)
data class SyncLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val configId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val status: SyncStatus,
    val filesCopied: Int = 0,
    val filesDeleted: Int = 0,
    val filesSkipped: Int = 0,
    val filesFailed: Int = 0,
    val bytesTransferred: Long = 0,
    val durationMs: Long = 0,
    val errorMessage: String? = null,
    val detailedLog: String? = null // JSON array of file operations
)

/**
 * Sync log with configuration name (for display purposes)
 */
data class SyncLogWithName(
    val id: Long = 0,
    val configId: Long,
    val configName: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val status: SyncStatus,
    val filesCopied: Int = 0,
    val filesDeleted: Int = 0,
    val filesSkipped: Int = 0,
    val filesFailed: Int = 0,
    val bytesTransferred: Long = 0,
    val durationMs: Long = 0,
    val errorMessage: String? = null,
    val detailedLog: String? = null
)

enum class SyncStatus {
    SUCCESS,
    FAILED,
    PARTIAL,
    CANCELLED
}
