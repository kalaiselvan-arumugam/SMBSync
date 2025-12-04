package com.mybackup.smbsync.domain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mybackup.smbsync.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Foreground service for sync operations
 */
@AndroidEntryPoint
class SyncForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "sync_channel"
        const val ACTION_START_SYNC = "com.mybackup.smbsync.START_SYNC"
        const val ACTION_STOP_SYNC = "com.mybackup.smbsync.STOP_SYNC"
        const val EXTRA_CONFIG_ID = "config_id"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SYNC -> {
                val configId = intent.getLongExtra(EXTRA_CONFIG_ID, -1)
                startForegroundService()
                // TODO: Start sync operation
            }
            ACTION_STOP_SYNC -> {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val notification = createNotification(
            title = "Sync in Progress",
            message = "Syncing files...",
            progress = 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(
        title: String,
        message: String,
        progress: Int = -1
    ): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        if (progress >= 0) {
            builder.setProgress(100, progress, false)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "File Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of file synchronization"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
