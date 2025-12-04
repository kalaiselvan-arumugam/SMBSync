package com.mybackup.smbsync.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

/**
 * Helper for managing battery optimization settings
 */
class BatteryOptimizationHelper(private val context: Context) {

    /**
     * Check if app is whitelisted from battery optimization
     */
    fun isIgnoringBatteryOptimizations(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
        return true
    }

    /**
     * Request battery optimization exclusion
     */
    fun requestIgnoreBatteryOptimizations(): Intent {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:${context.packageName}")
        }
        return intent
    }

    /**
     * Get manufacturer-specific battery settings intent
     */
    fun getManufacturerBatteryIntent(): Intent? {
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        return when {
            manufacturer.contains("xiaomi") -> {
                Intent().apply {
                    action = "miui.intent.action.APP_PERM_EDITOR"
                    putExtra("extra_pkgname", context.packageName)
                }
            }
            manufacturer.contains("huawei") -> {
                Intent().apply {
                    action = "huawei.intent.action.HSM_PROTECTED_APPS"
                }
            }
            manufacturer.contains("oppo") -> {
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            manufacturer.contains("vivo") -> {
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            manufacturer.contains("oneplus") -> {
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            else -> null
        }
    }

    /**
     * Get manufacturer name for display
     */
    fun getManufacturerName(): String {
        return Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
    }

    /**
     * Check if device is from a manufacturer with aggressive battery management
     */
    fun hasAggressiveBatteryManagement(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("xiaomi") ||
                manufacturer.contains("huawei") ||
                manufacturer.contains("oppo") ||
                manufacturer.contains("vivo") ||
                manufacturer.contains("oneplus")
    }
}
