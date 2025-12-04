package com.mybackup.smbsync.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sync configuration entity
 */
@Entity(
    tableName = "sync_configurations",
    foreignKeys = [
        ForeignKey(
            entity = SmbServer::class,
            parentColumns = ["id"],
            childColumns = ["serverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("serverId")]
)
data class SyncConfiguration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val serverId: Long,
    val localPath: String,
    val remotePath: String,
    val syncMode: SyncMode,
    val enabled: Boolean = true,
    // Advanced comparison options
    val timeTolerance: Int = 0, // seconds: 0, 1, 3, 5, 10
    val ignoreDst: Boolean = false,
    val caseSensitive: Boolean = true,
    val useChecksum: Boolean = false, // High Integrity check
    
    // File filters
    val minFileSize: Long? = null, // bytes
    val maxFileSize: Long? = null, // bytes
    val modifiedAfter: Long? = null, // timestamp
    val modifiedBefore: Long? = null, // timestamp
    val ignoreHiddenFiles: Boolean = true,
    val ignoreZeroByteFiles: Boolean = false,
    val fileExtensionFilter: String? = null, // JSON array of extensions
    
    // Archive mode settings (Legacy - kept for potential future use or migration, but hidden)
    val archiveAgeDays: Int = 30, 
    val archivePhotos: Boolean = true,
    val archiveVideos: Boolean = true,
    
    // Performance settings
    val maxFolderDepth: Int? = null, // null = unlimited
    val bandwidthLimitKbps: Int? = null,
    val parallelTransfers: Int = 1,
    
    // Scheduling
    val scheduleType: ScheduleType = ScheduleType.MANUAL,
    val intervalMinutes: Int? = null, // 30, 60, 360, 720
    val dailyTime: String? = null, // HH:mm format
    val networkPreference: NetworkPreference = NetworkPreference.WIFI_ONLY,
    val batteryRequirement: BatteryRequirement = BatteryRequirement.ANY,
    
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long? = null
)

enum class SyncMode {
    SYNC,      // Source -> Destination (Copy new, Rename conflict, No delete)
    MOVE,      // Source -> Destination (Copy new, Rename conflict, Delete source)
    MIRROR     // Bi-directional (Propagate delete, Rename both on conflict)
}

enum class ScheduleType {
    MANUAL,
    INTERVAL,
    DAILY
}

enum class NetworkPreference {
    WIFI_ONLY,
    WIFI_OR_MOBILE
}

enum class BatteryRequirement {
    ANY,
    NOT_LOW,
    CHARGING
}
