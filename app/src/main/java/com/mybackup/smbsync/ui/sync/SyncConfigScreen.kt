package com.mybackup.smbsync.ui.sync

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.data.model.NetworkPreference
import com.mybackup.smbsync.data.model.SyncConfiguration
import com.mybackup.smbsync.data.model.SyncMode
import com.mybackup.smbsync.data.repository.SmbServerRepository
import com.mybackup.smbsync.data.repository.SyncConfigurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SyncConfigViewModel @Inject constructor(
    private val syncRepo: SyncConfigurationRepository,
    private val serverRepo: SmbServerRepository,
    private val credentialEncryption: com.mybackup.smbsync.util.CredentialEncryption,
    private val workManager: androidx.work.WorkManager
) : ViewModel() {
    val servers = serverRepo.getAllServers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var name by mutableStateOf("")
    var serverId by mutableStateOf<Long?>(null)
    var localPath by mutableStateOf("")
    var remotePath by mutableStateOf("")
    var syncMode by mutableStateOf(SyncMode.SYNC)
    var useChecksum by mutableStateOf(false)
    
    // Advanced options (simplified for now)
    var wifiOnly by mutableStateOf(true)
    var intervalMinutes by mutableStateOf(60L)
    var dailyTime by mutableStateOf("00:00")

    private var existingConfigId: Long? = null

    fun loadConfig(configId: Long) {
        viewModelScope.launch {
            syncRepo.getConfigurationById(configId)?.let { config ->
                existingConfigId = config.id
                name = config.name
                serverId = config.serverId
                localPath = config.localPath
                remotePath = config.remotePath
                syncMode = config.syncMode
                useChecksum = config.useChecksum
                wifiOnly = config.networkPreference == NetworkPreference.WIFI_ONLY
                intervalMinutes = config.intervalMinutes?.toLong() ?: 60L
                isScheduleEnabled = config.enabled
                dailyTime = config.dailyTime ?: "00:00"
            }
        }
    }

    var isScheduleEnabled by mutableStateOf(true)

    val isValid: Boolean
        get() = name.isNotBlank() && 
                (serverId?.let { it > 0 } ?: false) && 
                localPath.isNotBlank() && 
                remotePath.isNotBlank()

    val intervalOptions = listOf(
        30L to "Every 30 Minutes",
        60L to "Every 1 Hour",
        360L to "Every 6 Hours",
        720L to "Every 12 Hours",
        1440L to "Daily"
    )

    var showDuplicateError by mutableStateOf(false)
    var duplicateTaskName by mutableStateOf("")

    fun saveConfig(onSaved: () -> Unit) {
        viewModelScope.launch {
            // Check for duplicates
            val allConfigs = syncRepo.getAllConfigurations().first()
            val duplicate = allConfigs.find { 
                it.id != existingConfigId && 
                it.serverId == serverId && 
                it.localPath == localPath && 
                it.remotePath == remotePath 
            }

            if (duplicate != null) {
                duplicateTaskName = duplicate.name
                showDuplicateError = true
                return@launch
            }

            try {
                android.util.Log.d("SyncConfigViewModel", "Starting saveConfig")
                val configId = saveConfigInternal()
                android.util.Log.d("SyncConfigViewModel", "Config saved with ID: $configId")
                
                if (isScheduleEnabled && intervalMinutes >= 15) {
                    android.util.Log.d("SyncConfigViewModel", "Scheduling sync")
                    scheduleSync(configId)
                    android.util.Log.d("SyncConfigViewModel", "Sync scheduled")
                } else if (!isScheduleEnabled) {
                    android.util.Log.d("SyncConfigViewModel", "Cancelling scheduled work")
                    // Cancel any existing scheduled work
                    workManager.cancelUniqueWork("sync_$configId")
                }
                
                android.util.Log.d("SyncConfigViewModel", "Calling onSaved callback")
                withContext(Dispatchers.Main) {
                    try {
                        onSaved()
                    } catch (e: Exception) {
                        android.util.Log.e("SyncConfigViewModel", "Error in onSaved callback", e)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncConfigViewModel", "Error in saveConfig", e)
                withContext(Dispatchers.Main) {
                    try {
                        onSaved() // Still try to navigate back
                    } catch (e2: Exception) {
                        android.util.Log.e("SyncConfigViewModel", "Error calling onSaved in catch", e2)
                    }
                }
            }
        }
    }

    fun runNow(onSaved: () -> Unit) {
        viewModelScope.launch {
            // Check for duplicates
            val allConfigs = syncRepo.getAllConfigurations().first()
            val duplicate = allConfigs.find { 
                it.id != existingConfigId && 
                it.serverId == serverId && 
                it.localPath == localPath && 
                it.remotePath == remotePath 
            }

            if (duplicate != null) {
                duplicateTaskName = duplicate.name
                showDuplicateError = true
                return@launch
            }

            try {
                android.util.Log.d("SyncConfigViewModel", "Starting runNow")
                val configId = saveConfigInternal()
                android.util.Log.d("SyncConfigViewModel", "Config saved, ID: $configId")
                
                if (configId > 0) {
                    android.util.Log.d("SyncConfigViewModel", "Enqueueing work for config: $configId")
                    // Trigger immediate sync
                    val request = androidx.work.OneTimeWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>()
                        .setInputData(androidx.work.workDataOf("configId" to configId))
                        .build()
                    workManager.enqueue(request)
                    android.util.Log.d("SyncConfigViewModel", "Work enqueued successfully")
                } else {
                    android.util.Log.e("SyncConfigViewModel", "Invalid config ID: $configId")
                }
                
                android.util.Log.d("SyncConfigViewModel", "Calling onSaved callback")
                withContext(Dispatchers.Main) {
                    try {
                        onSaved()
                    } catch (e: Exception) {
                        android.util.Log.e("SyncConfigViewModel", "Error in onSaved callback", e)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncConfigViewModel", "Error in runNow", e)
                withContext(Dispatchers.Main) {
                    try {
                        onSaved()
                    } catch (e2: Exception) {
                        android.util.Log.e("SyncConfigViewModel", "Error calling onSaved in catch", e2)
                    }
                }
            }
        }
    }

    fun deleteConfig(onDeleted: () -> Unit) {
        viewModelScope.launch {
            if (existingConfigId != null) {
                syncRepo.getConfigurationById(existingConfigId!!)?.let { config ->
                    syncRepo.deleteConfiguration(config)
                }
            }
            onDeleted()
        }
    }

    private suspend fun saveConfigInternal(): Long {
        return try {
            val config = SyncConfiguration(
                id = existingConfigId ?: 0,
                name = name.ifBlank { "Sync Task" },
                serverId = serverId ?: return 0,
                localPath = localPath,
                remotePath = remotePath,
                syncMode = syncMode,
                useChecksum = useChecksum,
                networkPreference = if (wifiOnly) NetworkPreference.WIFI_ONLY else NetworkPreference.WIFI_OR_MOBILE,
                intervalMinutes = intervalMinutes.toInt(),
                dailyTime = if (intervalMinutes == 1440L) dailyTime else null,
                enabled = isScheduleEnabled
            )
            
            android.util.Log.d("SyncConfigViewModel", "Saving config: $config")
            
            val result = if (existingConfigId != null) {
                syncRepo.updateConfiguration(config)
                existingConfigId!!
            } else {
                syncRepo.insertConfiguration(config)
            }
            
            android.util.Log.d("SyncConfigViewModel", "Config saved with ID: $result")
            result
        } catch (e: Exception) {
            android.util.Log.e("SyncConfigViewModel", "Error saving config", e)
            0L
        }
    }

    private fun scheduleSync(configId: Long) {
        android.util.Log.d("SyncConfigViewModel", "=== SCHEDULING SYNC ===")
        android.util.Log.d("SyncConfigViewModel", "Config ID: $configId")
        android.util.Log.d("SyncConfigViewModel", "Interval Minutes: $intervalMinutes")
        android.util.Log.d("SyncConfigViewModel", "Daily Time: $dailyTime")
        android.util.Log.d("SyncConfigViewModel", "WiFi Only: $wifiOnly")
        
        // Ensure minimum interval for PeriodicWork
        val actualInterval = if (intervalMinutes < 15) {
            android.util.Log.w("SyncConfigViewModel", "Interval too short ($intervalMinutes min), using minimum 15 minutes")
            15L
        } else {
            intervalMinutes
        }
        
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) androidx.work.NetworkType.UNMETERED 
                else androidx.work.NetworkType.CONNECTED
            )
            .build()

        // For daily sync, use OneTimeWorkRequest with exact timing instead of PeriodicWorkRequest
        // This ensures it runs at the exact time specified
        if (actualInterval == 1440L) {
            scheduleDailySync(configId, constraints)
        } else {
            schedulePeriodicSync(configId, actualInterval, constraints)
        }
    }
    
    private fun scheduleDailySync(configId: Long, constraints: androidx.work.Constraints) {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            val parts = dailyTime.split(":")
            if (parts.size == 2) {
                set(java.util.Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(java.util.Calendar.MINUTE, parts[1].toInt())
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                
                // If target time has passed today, schedule for tomorrow
                if (before(now)) {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
        
        val initialDelay = target.timeInMillis - now.timeInMillis
        
        android.util.Log.d("SyncConfigViewModel", "Scheduling DAILY sync:")
        android.util.Log.d("SyncConfigViewModel", "Current time: ${now.time}")
        android.util.Log.d("SyncConfigViewModel", "Target time: ${target.time}")
        android.util.Log.d("SyncConfigViewModel", "Initial delay (ms): $initialDelay")
        android.util.Log.d("SyncConfigViewModel", "Initial delay (minutes): ${initialDelay / 1000 / 60}")
        
        // Use OneTimeWorkRequest for exact timing
        val request = androidx.work.OneTimeWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>()
            .setConstraints(constraints)
            .setInputData(androidx.work.workDataOf(
                "configId" to configId,
                "isDailySync" to true,  // Flag to reschedule after completion
                "dailyTime" to dailyTime
            ))
            .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .addTag("sync_$configId")
            .addTag("daily_sync")
            .build()

        workManager.enqueueUniqueWork(
            "sync_$configId",
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
        
        android.util.Log.d("SyncConfigViewModel", "Daily sync scheduled successfully")
        
        // Check work status
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            val workInfos = workManager.getWorkInfosForUniqueWork("sync_$configId").get()
            android.util.Log.d("SyncConfigViewModel", "Work status check:")
            workInfos.forEach { workInfo ->
                android.util.Log.d("SyncConfigViewModel", "  State: ${workInfo.state}")
                android.util.Log.d("SyncConfigViewModel", "  Run attempt: ${workInfo.runAttemptCount}")
                android.util.Log.d("SyncConfigViewModel", "  Tags: ${workInfo.tags}")
            }
        }
        
        android.util.Log.d("SyncConfigViewModel", "=== END SCHEDULING ===")
    }
    
    private fun schedulePeriodicSync(configId: Long, intervalMinutes: Long, constraints: androidx.work.Constraints) {
        android.util.Log.d("SyncConfigViewModel", "Scheduling PERIODIC sync every $intervalMinutes minutes")
        
        val request = androidx.work.PeriodicWorkRequestBuilder<com.mybackup.smbsync.domain.service.SyncWorker>(
            intervalMinutes, java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(androidx.work.workDataOf("configId" to configId))
            .setInitialDelay(intervalMinutes, java.util.concurrent.TimeUnit.MINUTES)
            .addTag("sync_$configId")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "sync_$configId",
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
        
        android.util.Log.d("SyncConfigViewModel", "Periodic sync scheduled successfully")
        
        // Check work status
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            val workInfos = workManager.getWorkInfosForUniqueWork("sync_$configId").get()
            android.util.Log.d("SyncConfigViewModel", "Work status check:")
            workInfos.forEach { workInfo ->
                android.util.Log.d("SyncConfigViewModel", "  State: ${workInfo.state}")
                android.util.Log.d("SyncConfigViewModel", "  Run attempt: ${workInfo.runAttemptCount}")
            }
        }
        
        android.util.Log.d("SyncConfigViewModel", "=== END SCHEDULING ===")
    }



    suspend fun listLocalFolders(path: String): List<String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dir = java.io.File(path)
                if (!dir.exists() || !dir.isDirectory) return@withContext emptyList()
                
                dir.listFiles()
                    ?.filter { it.isDirectory && !it.isHidden }
                    ?.map { it.name }
                    ?.sorted()
                    ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun listRemoteFolders(path: String): List<String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val id = serverId ?: return@withContext emptyList()
                val server = serverRepo.getServerById(id) ?: return@withContext emptyList()
                val password = credentialEncryption.decrypt(server.encryptedPassword)
                
                val client = com.mybackup.smbsync.data.remote.SmbClient(
                    address = server.address,
                    port = server.port,
                    username = server.username,
                    password = password,
                    domain = server.domain,
                    protocol = server.protocol.name
                )
                
                val files = client.listFiles(path)
                android.util.Log.d("SyncConfigViewModel", "Listing path: $path, Result: ${files.map { it.name }}")
                
                files.filter { it.isDirectory }
                    .map { it.name }
                    .sorted()
            } catch (e: Exception) {
                android.util.Log.e("SyncConfigViewModel", "Error listing remote folders", e)
                emptyList()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConfigScreen(
    onBack: () -> Unit,
    configId: Long? = null,
    viewModel: SyncConfigViewModel = hiltViewModel()
) {
    LaunchedEffect(configId) {
        if (configId != null && configId != -1L) {
            viewModel.loadConfig(configId)
        }
    }

    val servers by viewModel.servers.collectAsState()
    var showLocalPicker by remember { mutableStateOf(false) }
    var showRemotePicker by remember { mutableStateOf(false) }
    
    var serverExpanded by remember { mutableStateOf(false) }
    var modeExpanded by remember { mutableStateOf(false) }
    var conflictExpanded by remember { mutableStateOf(false) }

    if (showLocalPicker) {
        FolderPickerDialog(
            initialPath = viewModel.localPath,
            onDismiss = { showLocalPicker = false },
            onSelect = { 
                viewModel.localPath = it
                showLocalPicker = false 
            },
            onListFolders = { viewModel.listLocalFolders(it) },
            title = "Select Local Folder"
        )
    }

    if (showRemotePicker) {
        FolderPickerDialog(
            initialPath = viewModel.remotePath,
            onDismiss = { showRemotePicker = false },
            onSelect = { 
                viewModel.remotePath = it
                showRemotePicker = false 
            },
            onListFolders = { viewModel.listRemoteFolders(it) },
            title = "Select Remote Folder"
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showModeWarningDialog by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<SyncMode?>(null) }

    if (showModeWarningDialog && pendingMode != null) {
        AlertDialog(
            onDismissRequest = { 
                showModeWarningDialog = false
                pendingMode = null
            },
            title = { Text("⚠️ Warning") },
            text = { 
                Text(
                    when (pendingMode) {
                        SyncMode.MOVE -> "MOVE mode will delete files from the Source after successful transfer. Are you sure?"
                        SyncMode.MIRROR -> "MIRROR mode will delete files from both Source and Destination to keep them identical. Files deleted on one side will be deleted on the other. Are you sure?"
                        else -> ""
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.syncMode = pendingMode!!
                        showModeWarningDialog = false
                        pendingMode = null
                    }
                ) {
                    Text("Yes, I understand", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showModeWarningDialog = false
                        pendingMode = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this sync task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteConfig(onBack)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )

    }

    if (viewModel.showDuplicateError) {
        AlertDialog(
            onDismissRequest = { viewModel.showDuplicateError = false },
            title = { Text("Duplicate Task") },
            text = { Text("A task named '${viewModel.duplicateTaskName}' already exists with the same server, source, and destination paths.") },
            confirmButton = {
                TextButton(onClick = { viewModel.showDuplicateError = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (configId != null && configId != -1L) "Edit Task" else "New Task",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    if (configId != null && configId != -1L) {
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    if (viewModel.isValid) {
                        viewModel.saveConfig(onBack)
                    }
                },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("Save Task") },
                containerColor = if (viewModel.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (viewModel.isValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                expanded = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // General Section
            com.mybackup.smbsync.ui.components.ModernCard {
                Text(
                    text = "Task Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = serverExpanded,
                    onExpandedChange = { serverExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = servers.find { it.id == viewModel.serverId }?.name ?: "Select Server",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("SMB Server") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = serverExpanded,
                        onDismissRequest = { serverExpanded = false }
                    ) {
                        servers.forEach { server ->
                            DropdownMenuItem(
                                text = { Text(server.name) },
                                onClick = {
                                    viewModel.serverId = server.id
                                    serverExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Paths Section
            com.mybackup.smbsync.ui.components.ModernCard {
                Text(
                    text = "Paths",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.localPath,
                    onValueChange = { viewModel.localPath = it },
                    label = { Text("Local Folder") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showLocalPicker = true }) {
                            Icon(
                                Icons.Default.Folder, 
                                contentDescription = "Select Folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.remotePath,
                    onValueChange = { viewModel.remotePath = it },
                    label = { Text("Remote Folder") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = viewModel.serverId != null,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = { showRemotePicker = true },
                            enabled = viewModel.serverId != null
                        ) {
                            Icon(
                                Icons.Default.Folder, 
                                contentDescription = "Select Folder",
                                tint = if (viewModel.serverId != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }

            // Sync Options Section
            com.mybackup.smbsync.ui.components.ModernCard {
                Text(
                    text = "Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = modeExpanded,
                    onExpandedChange = { modeExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = viewModel.syncMode.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sync Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }
                    ) {
                        SyncMode.values().forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.name) },
                                onClick = {
                                    if (mode == SyncMode.MOVE || mode == SyncMode.MIRROR) {
                                        pendingMode = mode
                                        showModeWarningDialog = true
                                        modeExpanded = false
                                    } else {
                                        viewModel.syncMode = mode
                                        modeExpanded = false
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "High Integrity Check",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Use checksums to verify file content (Slower)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = viewModel.useChecksum,
                        onCheckedChange = { viewModel.useChecksum = it },
                        modifier = Modifier.padding(start = 8.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sync only on WiFi",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Save data by syncing only on WiFi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = viewModel.wifiOnly,
                        onCheckedChange = { viewModel.wifiOnly = it },
                        modifier = Modifier.padding(start = 8.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Schedule Section
            com.mybackup.smbsync.ui.components.ModernCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = viewModel.isScheduleEnabled,
                        onCheckedChange = { viewModel.isScheduleEnabled = it },
                        modifier = Modifier.padding(start = 8.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                
                if (viewModel.isScheduleEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    var intervalExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = intervalExpanded,
                        onExpandedChange = { intervalExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = viewModel.intervalOptions.find { it.first == viewModel.intervalMinutes }?.second ?: "Custom",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sync Interval") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = intervalExpanded,
                            onDismissRequest = { intervalExpanded = false }
                        ) {
                            viewModel.intervalOptions.forEach { (minutes, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.intervalMinutes = minutes
                                        intervalExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (viewModel.intervalMinutes == 1440L) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val calendar = java.util.Calendar.getInstance()
                        val timeParts = viewModel.dailyTime.split(":")
                        val hour = if (timeParts.size == 2) timeParts[0].toInt() else calendar.get(java.util.Calendar.HOUR_OF_DAY)
                        val minute = if (timeParts.size == 2) timeParts[1].toInt() else calendar.get(java.util.Calendar.MINUTE)

                        val timePickerDialog = android.app.TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                viewModel.dailyTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            },
                            hour,
                            minute,
                            true // 24 hour view
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = viewModel.dailyTime,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Daily Sync Time") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { timePickerDialog.show() }
                            )
                        }
                    }
                }
            }

            if (configId != null && configId != -1L) {
                Button(
                    onClick = { viewModel.runNow(onBack) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Now", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            // Bottom spacer for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
