package com.mybackup.smbsync

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class with Hilt initialization
 */
@HiltAndroidApp
class MyBackupApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() {
            android.util.Log.d("MyBackupApplication", "Configuring WorkManager")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "sync_channel",
                "Sync Operations",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for ongoing sync operations"
            }

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
