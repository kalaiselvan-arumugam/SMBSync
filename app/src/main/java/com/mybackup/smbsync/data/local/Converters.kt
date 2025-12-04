package com.mybackup.smbsync.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mybackup.smbsync.data.model.*

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSmbProtocol(value: SmbProtocol): String = value.name

    @TypeConverter
    fun toSmbProtocol(value: String): SmbProtocol = SmbProtocol.valueOf(value)

    @TypeConverter
    fun fromSyncMode(value: SyncMode): String = value.name

    @TypeConverter
    fun toSyncMode(value: String): SyncMode = SyncMode.valueOf(value)



    @TypeConverter
    fun fromScheduleType(value: ScheduleType): String = value.name

    @TypeConverter
    fun toScheduleType(value: String): ScheduleType = ScheduleType.valueOf(value)

    @TypeConverter
    fun fromNetworkPreference(value: NetworkPreference): String = value.name

    @TypeConverter
    fun toNetworkPreference(value: String): NetworkPreference = NetworkPreference.valueOf(value)

    @TypeConverter
    fun fromBatteryRequirement(value: BatteryRequirement): String = value.name

    @TypeConverter
    fun toBatteryRequirement(value: String): BatteryRequirement = BatteryRequirement.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
