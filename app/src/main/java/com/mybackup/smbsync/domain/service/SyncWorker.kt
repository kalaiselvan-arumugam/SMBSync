package com.mybackup.smbsync.domain.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import com.mybackup.smbsync.domain.service.SyncStatusManager

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncEngine: SyncEngine,
    private val syncRepo: SyncConfigurationRepository,
    private val syncStatusManager: SyncStatusManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val configId = inputData.getLong("configId", -1L)
        val isDailySync = inputData.getBoolean("isDailySync", false)
        val dailyTime = inputData.getString("dailyTime")
        
        android.util.Log.d("SyncWorker", "=== SYNC WORKER STARTED ===")
        android.util.Log.d("SyncWorker", "Config ID: $configId")
        android.util.Log.d("SyncWorker", "Is Daily Sync: $isDailySync")
        android.util.Log.d("SyncWorker", "Run attempt: $runAttemptCount")
        android.util.Log.d("SyncWorker", "Current time: ${java.util.Date()}")
        
        if (configId != -1L) {
            // Run specific config
            val config = syncRepo.getConfigurationById(configId)
            if (config == null) {
                android.util.Log.e("SyncWorker", "Config not found for ID: $configId")
                return Result.failure()
            }
            
            // Check if enabled
            if (!config.enabled) {
                android.util.Log.d("SyncWorker", "Task ${config.name} is disabled. Skipping.")
                return Result.success()
            }
            
            android.util.Log.d("SyncWorker", "Running sync for: ${config.name}")
            android.util.Log.d("SyncWorker", "Local path: ${config.localPath}")
            android.util.Log.d("SyncWorker", "Remote path: ${config.remotePath}")
            
            
            // Check if worker was stopped before starting
            if (isStopped) {
                android.util.Log.d("SyncWorker", "Worker was stopped before sync started")
                return Result.failure()
            }
            
            val notificationId = configId.toInt()
            setForeground(createForegroundInfo(config.name, "Starting sync...", 0, notificationId))
            
            syncStatusManager.setTaskRunning(configId, true)
            val result = try {
                syncEngine.runSync(config) { status, progress ->
                    // Check if stopped during sync
                    if (isStopped) {
                        android.util.Log.d("SyncWorker", "Worker stopped during sync")
                        throw kotlinx.coroutines.CancellationException("Sync cancelled by user")
                    }
                    // Update notification
                    setForegroundAsync(createForegroundInfo(config.name, status, progress, notificationId))
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                android.util.Log.d("SyncWorker", "Sync cancelled")
                syncStatusManager.setTaskRunning(configId, false)
                return Result.failure()
            } finally {
                syncStatusManager.setTaskRunning(configId, false)
            }
            
            val success = result.filesFailed == 0 && result.errors.isEmpty()
            android.util.Log.d("SyncWorker", "Sync completed. Success: $success, Transferred: ${result.filesTransferred}, Failed: ${result.filesFailed}")
            
            // Show completion notification
            showCompletionNotification(config.name, result, notificationId)
            
            // If this was a daily sync, reschedule for tomorrow
            if (isDailySync && dailyTime != null && config.enabled) {
                rescheduleDailySync(configId, dailyTime, config.networkPreference == com.mybackup.smbsync.data.model.NetworkPreference.WIFI_ONLY)
            }
            
            android.util.Log.d("SyncWorker", "=== SYNC WORKER FINISHED ===")
            return if (success) Result.success() else Result.failure()
        } else {
            // Run all scheduled configs
            // Since getAllConfigurations returns a Flow, we need to collect it or use a suspend function if available.
            // For now, we'll collect the first emission.
            val configs: List<com.mybackup.smbsync.data.model.SyncConfiguration> = try {
                syncRepo.getAllConfigurations().first()
            } catch (e: Exception) {
                emptyList()
            }
            
            var allSuccess = true
            
            for (config in configs) {
                if (config.enabled) {
                    val notificationId = config.id.toInt()
                    setForeground(createForegroundInfo(config.name, "Starting sync...", 0, notificationId))
                    val result = syncEngine.runSync(config) { status, progress ->
                         setForegroundAsync(createForegroundInfo(config.name, status, progress, notificationId))
                    }
                    
                    showCompletionNotification(config.name, result, notificationId)
                    
                    if (result.filesFailed > 0 || result.errors.isNotEmpty()) allSuccess = false
                }
            }
            
            return if (allSuccess) Result.success() else Result.retry()
        }
    }
    
    private fun rescheduleDailySync(configId: Long, dailyTime: String, wifiOnly: Boolean) {
        android.util.Log.d("SyncWorker", "Rescheduling daily sync for tomorrow at $dailyTime")
        
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            val parts = dailyTime.split(":")
            if (parts.size == 2) {
                set(java.util.Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(java.util.Calendar.MINUTE, parts[1].toInt())
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                // Always schedule for tomorrow since we just ran
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val delay = target.timeInMillis - now.timeInMillis
        
        android.util.Log.d("SyncWorker", "Next run scheduled for: ${target.time}")
        android.util.Log.d("SyncWorker", "Delay: ${delay / 1000 / 60} minutes")
        
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) androidx.work.NetworkType.UNMETERED 
                else androidx.work.NetworkType.CONNECTED
            )
            .build()
        
        val request = androidx.work.OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInputData(androidx.work.workDataOf(
                "configId" to configId,
                "isDailySync" to true,
                "dailyTime" to dailyTime
            ))
            .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .addTag("sync_$configId")
            .addTag("daily_sync")
            .build()
        
        androidx.work.WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "sync_$configId",
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
        
        android.util.Log.d("SyncWorker", "Daily sync rescheduled successfully")
    }

    private fun createForegroundInfo(title: String, status: String, progress: Int, notificationId: Int): androidx.work.ForegroundInfo {
        android.util.Log.d("SyncWorker", "Creating foreground notification: $title - $status")
        
        // Check if notifications are enabled
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val areNotificationsEnabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            true
        }
        android.util.Log.d("SyncWorker", "Notifications enabled: $areNotificationsEnabled")
        
        val intent = android.content.Intent(applicationContext, com.mybackup.smbsync.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            applicationContext, 
            0, 
            intent, 
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, "sync_channel")
            .setContentTitle("Syncing: $title")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
        
        android.util.Log.d("SyncWorker", "Foreground notification created with ID: $notificationId")
            
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            androidx.work.ForegroundInfo(
                notificationId, 
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            androidx.work.ForegroundInfo(notificationId, notification)
        }
    }

    private fun showCompletionNotification(title: String, result: SyncEngine.SyncResult, notificationId: Int) {
        android.util.Log.d("SyncWorker", "=== Showing completion notification ===")
        android.util.Log.d("SyncWorker", "Title: $title")
        android.util.Log.d("SyncWorker", "Files transferred: ${result.filesTransferred}, Failed: ${result.filesFailed}")
        
        val isSuccess = result.filesFailed == 0 && result.errors.isEmpty()
        val statusText = if (isSuccess) "✓ Success" else "✗ Failed"
        
        // Build detailed message
        val details = buildString {
            append("Task: $title\n")
            append("Status: $statusText\n")
            append("Files Transferred: ${result.filesTransferred}\n")
            if (result.filesFailed > 0) {
                append("Files Failed: ${result.filesFailed}\n")
            }
            if (result.errors.isNotEmpty()) {
                append("\nErrors:\n")
                result.errors.take(3).forEach { error ->
                    append("• $error\n")
                }
                if (result.errors.size > 3) {
                    append("... and ${result.errors.size - 3} more")
                }
            }
        }

        val intent = android.content.Intent(applicationContext, com.mybackup.smbsync.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            applicationContext, 
            0, 
            intent, 
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, "sync_channel")
            .setContentTitle("$title - $statusText")
            .setContentText("${result.filesTransferred} files transferred" + 
                if (result.filesFailed > 0) ", ${result.filesFailed} failed" else "")
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(details))
            .setSmallIcon(if (isSuccess) android.R.drawable.stat_sys_upload_done else android.R.drawable.stat_notify_error)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        android.util.Log.d("SyncWorker", "Cancelling foreground notification ID: $notificationId")
        // Cancel the foreground notification and show completion notification
        notificationManager.cancel(notificationId)
        
        val completionId = notificationId + 1000
        android.util.Log.d("SyncWorker", "Showing completion notification ID: $completionId")
        notificationManager.notify(completionId, notification)
        android.util.Log.d("SyncWorker", "Completion notification sent")
    }
}
