package com.mybackup.smbsync.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagerDebugScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var workInfos by remember { mutableStateOf<List<WorkInfo>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    fun refreshWorkInfo() {
        scope.launch {
            try {
                // Get all work - WorkManager doesn't support partial tag matching
                // So we'll get all work and filter by our tags
                val allWork = mutableListOf<WorkInfo>()
                
                // Try to get work by common tag patterns
                for (i in 1..100) { // Check up to 100 configs
                    try {
                        val workList = workManager.getWorkInfosForUniqueWork("sync_$i").get()
                        allWork.addAll(workList)
                    } catch (e: Exception) {
                        // Config doesn't exist, continue
                    }
                }
                
                // Also get test workers
                try {
                    val testWork = workManager.getWorkInfosByTag("test_worker").get()
                    allWork.addAll(testWork)
                } catch (e: Exception) {
                    android.util.Log.e("WorkManagerDebug", "Error getting test work", e)
                }
                
                workInfos = allWork.filter { it.state != WorkInfo.State.CANCELLED }
                android.util.Log.d("WorkManagerDebug", "Found ${allWork.size} scheduled works")
                
                // Also log to help debugging
                workInfos.forEach { info ->
                    android.util.Log.d("WorkManagerDebug", "Work: ${info.id}, State: ${info.state}, Tags: ${info.tags}")
                }
            } catch (e: Exception) {
                android.util.Log.e("WorkManagerDebug", "Error getting work info", e)
            }
        }
    }
    
    LaunchedEffect(Unit) {
        refreshWorkInfo()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scheduler Debug") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { refreshWorkInfo() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Scheduled Work Status",
                style = MaterialTheme.typography.headlineSmall
            )
            
            if (workInfos.isEmpty()) {
                Card {
                    Text(
                        "No scheduled work found",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                workInfos.forEach { workInfo ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Work ID: ${workInfo.id}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            
                            Row {
                                Text("State: ", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    workInfo.state.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when (workInfo.state) {
                                        WorkInfo.State.RUNNING -> MaterialTheme.colorScheme.primary
                                        WorkInfo.State.SUCCEEDED -> MaterialTheme.colorScheme.tertiary
                                        WorkInfo.State.FAILED -> MaterialTheme.colorScheme.error
                                        WorkInfo.State.BLOCKED -> MaterialTheme.colorScheme.error
                                        WorkInfo.State.CANCELLED -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                            
                            Text(
                                "Tags: ${workInfo.tags.joinToString()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            Text(
                                "Run attempt: ${workInfo.runAttemptCount}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            if (workInfo.state == WorkInfo.State.BLOCKED) {
                                Text(
                                    "⚠️ Work is BLOCKED - constraints not met",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            if (workInfo.stopReason != WorkInfo.STOP_REASON_NOT_STOPPED) {
                                Text(
                                    "Stop reason: ${workInfo.stopReason}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    // Schedule a test worker to run in 1 minute
                    val testRequest = androidx.work.OneTimeWorkRequestBuilder<com.mybackup.smbsync.domain.service.TestWorker>()
                        .setInitialDelay(1, java.util.concurrent.TimeUnit.MINUTES)
                        .addTag("test_worker")
                        .build()
                    
                    workManager.enqueue(testRequest)
                    android.util.Log.d("WorkManagerDebug", "Test worker scheduled for 1 minute from now")
                    
                    // Refresh list to show the new worker (it might take a moment to appear)
                    scope.launch {
                        kotlinx.coroutines.delay(500)
                        refreshWorkInfo()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Schedule Test Worker (1 min)")
            }
            
            Text(
                "This will schedule a test worker to run in 1 minute. You should see a notification when it executes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Tips for Debugging:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("• ENQUEUED: Work is scheduled and waiting")
                    Text("• RUNNING: Work is currently executing")
                    Text("• BLOCKED: Constraints not met (check WiFi/network)")
                    Text("• SUCCEEDED: Work completed successfully")
                    Text("• FAILED: Work failed to execute")
                }
            }
        }
    }
}
