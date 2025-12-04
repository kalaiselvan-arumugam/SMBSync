package com.mybackup.smbsync.ui.sync

import androidx.compose.animation.core.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.data.model.SyncConfiguration
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import kotlinx.coroutines.flow.SharingStarted
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import java.util.Calendar
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.mybackup.smbsync.data.repository.SyncLogRepository
import com.mybackup.smbsync.data.model.SyncLog
import com.mybackup.smbsync.data.model.SyncStatus
import javax.inject.Inject

@HiltViewModel
class SyncConfigListViewModel @Inject constructor(
    private val repository: SyncConfigurationRepository,
    private val serverRepo: com.mybackup.smbsync.data.repository.SmbServerRepository,
    private val syncStatusManager: com.mybackup.smbsync.domain.service.SyncStatusManager,
    private val workManager: androidx.work.WorkManager,
    private val logRepository: SyncLogRepository
) : ViewModel() {
    val configs = repository.getAllConfigurations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val servers = serverRepo.getAllServers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val runningTasks = syncStatusManager.runningTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun deleteConfig(config: SyncConfiguration) {
        viewModelScope.launch {
            repository.deleteConfiguration(config)
        }
    }

    fun toggleEnabled(config: SyncConfiguration, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabled(config.id, enabled)
        }
    }
    
    fun runSync(config: SyncConfiguration) {
        val request = androidx.work.OneTimeWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>()
            .setInputData(androidx.work.workDataOf("configId" to config.id))
            .build()
        workManager.enqueue(request)
    }

    fun stopSync(configId: Long) {
        viewModelScope.launch {
            // Cancel WorkManager work
            workManager.cancelUniqueWork("sync_$configId")
            
            // Update status
            syncStatusManager.setTaskRunning(configId, false)
            
            // Create cancelled log entry
            val log = SyncLog(
                configId = configId,
                status = SyncStatus.CANCELLED,
                errorMessage = "Stopped by user"
            )
            logRepository.insertLog(log)
            
            // Reschedule if enabled
            val config = repository.getConfigurationById(configId)
            if (config != null && config.enabled) {
                rescheduleSync(config)
            }
        }
    }
    
    private fun rescheduleSync(config: SyncConfiguration) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                when (config.networkPreference) {
                    com.mybackup.smbsync.data.model.NetworkPreference.WIFI_ONLY -> NetworkType.UNMETERED
                    com.mybackup.smbsync.data.model.NetworkPreference.WIFI_OR_MOBILE -> NetworkType.CONNECTED
                }
            )
            .setRequiresCharging(config.batteryRequirement == com.mybackup.smbsync.data.model.BatteryRequirement.CHARGING)
            .setRequiresBatteryNotLow(
                config.batteryRequirement == com.mybackup.smbsync.data.model.BatteryRequirement.NOT_LOW ||
                config.batteryRequirement == com.mybackup.smbsync.data.model.BatteryRequirement.CHARGING
            )
            .build()
            
        if (config.intervalMinutes == 1440) { // Daily
            scheduleDailySync(config, constraints)
        } else {
            schedulePeriodicSync(config, constraints)
        }
    }
    
    private fun scheduleDailySync(config: SyncConfiguration, constraints: Constraints) {
        val dailyTime = config.dailyTime ?: return
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            val parts = dailyTime.split(":")
            if (parts.size == 2) {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                
                // If target time has passed today, schedule for tomorrow
                // Since we just stopped it, we definitely want the NEXT run, which is likely tomorrow 
                // (or today if it hasn't run yet, but if it was running, it means it was running NOW)
                // Safest is to schedule for the next occurrence.
                if (before(now) || timeInMillis < now.timeInMillis + 60000) { // Add buffer
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
        
        val initialDelay = target.timeInMillis - now.timeInMillis
        
        val request = OneTimeWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(
                "configId" to config.id,
                "isDailySync" to true,
                "dailyTime" to dailyTime
            ))
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("sync_${config.id}")
            .addTag("daily_sync")
            .build()

        workManager.enqueueUniqueWork(
            "sync_${config.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
    
    private fun schedulePeriodicSync(config: SyncConfiguration, constraints: Constraints) {
        val interval = config.intervalMinutes?.toLong() ?: 15L
        val request = PeriodicWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>(
            interval, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(workDataOf("configId" to config.id))
            .addTag("sync_${config.id}")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "sync_${config.id}",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
    
    fun getNextRunTime(configId: Long): String? {
        try {
            android.util.Log.d("NextRunTime", "=== Getting next run time for config $configId ===")
            val workInfos = workManager.getWorkInfosForUniqueWork("sync_$configId").get()
            android.util.Log.d("NextRunTime", "Found ${workInfos.size} work infos")
            
            if (workInfos.isNotEmpty()) {
                workInfos.forEachIndexed { index, info ->
                    android.util.Log.d("NextRunTime", "WorkInfo[$index]: state=${info.state}, nextScheduleTime=${info.nextScheduleTimeMillis}")
                }
                
                val workInfo = workInfos.firstOrNull { 
                    it.state == androidx.work.WorkInfo.State.ENQUEUED 
                }
                
                if (workInfo != null) {
                    android.util.Log.d("NextRunTime", "Found ENQUEUED work")
                    val nextRunTime = workInfo.nextScheduleTimeMillis
                    android.util.Log.d("NextRunTime", "nextScheduleTimeMillis = $nextRunTime")
                    
                    if (nextRunTime > 0) {
                        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault())
                        calendar.timeInMillis = nextRunTime
                        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        format.timeZone = java.util.TimeZone.getDefault()
                        val formattedTime = format.format(calendar.time)
                        android.util.Log.d("NextRunTime", "Formatted time: $formattedTime (timezone: ${java.util.TimeZone.getDefault().id})")
                        return formattedTime
                    } else {
                        android.util.Log.d("NextRunTime", "nextScheduleTimeMillis is 0 or negative, using fallback")
                    }
                } else {
                    android.util.Log.d("NextRunTime", "No ENQUEUED work found")
                }
            } else {
                android.util.Log.d("NextRunTime", "No work infos found for sync_$configId")
            }
        } catch (e: Exception) {
            android.util.Log.e("NextRunTime", "Error getting next run time", e)
        }
        android.util.Log.d("NextRunTime", "Returning null, will use fallback calculation")
        return null
    }
    fun ensureSchedulesAreRunning() {
        viewModelScope.launch {
            try {
                val allConfigs = repository.getAllConfigurations().first()
                allConfigs.forEach { config ->
                    if (config.enabled) {
                        val workInfos = workManager.getWorkInfosForUniqueWork("sync_${config.id}").get()
                        val isScheduled = workInfos.any { 
                            it.state == androidx.work.WorkInfo.State.ENQUEUED || 
                            it.state == androidx.work.WorkInfo.State.RUNNING 
                        }
                        
                        if (!isScheduled) {
                            android.util.Log.d("SyncViewModel", "Rescheduling missing task: ${config.name}")
                            rescheduleSync(config)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncViewModel", "Error ensuring schedules", e)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConfigListScreen(
    onAddConfig: () -> Unit,
    onEditConfig: (Long) -> Unit,
    viewModel: SyncConfigListViewModel = hiltViewModel()
) {
    val configs by viewModel.configs.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val runningTasks by viewModel.runningTasks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ensureSchedulesAreRunning()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (servers.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddConfig,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { padding ->
        if (configs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sync tasks configured.\nTap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(configs) { config ->
                    SyncConfigItem(
                        config = config,
                        isRunning = runningTasks.contains(config.id),
                        viewModel = viewModel,
                        onEdit = { onEditConfig(config.id) },
                        onDelete = { viewModel.deleteConfig(config) },
                        onToggleEnabled = { enabled -> viewModel.toggleEnabled(config, enabled) },
                        onRun = { viewModel.runSync(config) },
                        onStop = { viewModel.stopSync(config.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConfigItem(
    config: SyncConfiguration,
    isRunning: Boolean,
    viewModel: SyncConfigListViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onRun: () -> Unit,
    onStop: () -> Unit
) {
    var showStopDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Stop Sync?") },
            text = { Text("Are you sure you want to stop the running sync task '${config.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStop()
                        showStopDialog = false
                        Toast.makeText(context, "Sync stopped", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Stop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    com.mybackup.smbsync.ui.components.ModernCard(
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon with background (matching ServerItem style)
            val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                ),
                label = "rotation"
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (config.enabled) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    androidx.compose.ui.graphics.Color(0xFF424242),
                modifier = Modifier
                    .size(56.dp)
                    .clickable(enabled = isRunning) {
                        if (isRunning) {
                            showStopDialog = true
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = if (config.enabled) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            androidx.compose.ui.graphics.Color(0xFFAAAAAA),
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                rotationZ = if (isRunning) angle else 0f
                            }
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Sync Mode Tag
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = config.syncMode.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    // Schedule Interval Tag
                    if (config.enabled) {
                        val scheduleText = if (config.intervalMinutes == 1440) {
                            "Daily"
                        } else {
                            "Every ${config.intervalMinutes}m"
                        }
                        
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = scheduleText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        // Next Run Time Tag from WorkManager
                        val nextRunText = viewModel.getNextRunTime(config.id) ?: calculateNextRunTime(config)
                        
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = nextRunText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Paths
                val localName = config.localPath.split("/").lastOrNull { it.isNotEmpty() } ?: config.localPath
                val remoteName = config.remotePath.split("/").lastOrNull { it.isNotEmpty() } ?: config.remotePath
                
                Text(
                    text = "$localName ➔ $remoteName",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = config.enabled,
                onCheckedChange = onToggleEnabled,
                modifier = Modifier.scale(0.8f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

private fun calculateNextRunTime(config: SyncConfiguration): String {
    if (!config.enabled) return "Disabled"
    
    val calendar = java.util.Calendar.getInstance()
    
    if (config.intervalMinutes == 1440) { // Daily
        val dailyTime = config.dailyTime ?: return "Daily"
        val parts = dailyTime.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toIntOrNull() ?: return dailyTime
            val minute = parts[1].toIntOrNull() ?: return dailyTime
            
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hour)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
            }
            
            if (target.before(calendar)) {
                target.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            format.timeZone = java.util.TimeZone.getDefault()
            return format.format(target.time)
        }
        return dailyTime
    } else {
        // For periodic tasks, calculate next run time from now
        val interval = config.intervalMinutes ?: return "Not scheduled"
        calendar.add(java.util.Calendar.MINUTE, interval)
        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        format.timeZone = java.util.TimeZone.getDefault()
        return format.format(calendar.time)
    }
}

// Extension to scale switch
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)
