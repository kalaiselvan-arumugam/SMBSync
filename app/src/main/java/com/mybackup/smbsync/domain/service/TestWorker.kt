package com.mybackup.smbsync.domain.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Test worker to verify WorkManager is functioning
 */
@HiltWorker
class TestWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        android.util.Log.d("TestWorker", "=== TEST WORKER EXECUTED ===")
        android.util.Log.d("TestWorker", "Current time: ${java.util.Date()}")
        android.util.Log.d("TestWorker", "Run attempt: $runAttemptCount")
        android.util.Log.d("TestWorker", "=== TEST WORKER FINISHED ===")
        
        // Show a notification to confirm it ran
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, "sync_channel")
            .setContentTitle("Test Worker Executed")
            .setContentText("WorkManager is working! Time: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .build()
        
        notificationManager.notify(999, notification)
        
        return Result.success()
    }
}
