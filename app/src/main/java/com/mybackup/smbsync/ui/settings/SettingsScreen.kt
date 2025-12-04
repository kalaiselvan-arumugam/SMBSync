package com.mybackup.smbsync.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.ui.components.ModernCard
import com.mybackup.smbsync.ui.components.SectionHeader
import com.mybackup.smbsync.util.BatteryOptimizationHelper
import com.mybackup.smbsync.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val batteryHelper: BatteryOptimizationHelper
) : ViewModel() {
    
    var isDarkTheme by mutableStateOf(false)
        private set
        
    var isPinSet by mutableStateOf(false)
        private set

    var showPinConfirmDialog by mutableStateOf(false)
        private set
        
    var confirmPinError by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            try {
                preferencesManager.themeMode.collect { mode ->
                    isDarkTheme = mode == "dark"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        viewModelScope.launch {
            try {
                preferencesManager.pinHash.collect { hash ->
                    isPinSet = hash != null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateDarkTheme(enabled: Boolean) {
        isDarkTheme = enabled
        viewModelScope.launch {
            preferencesManager.setThemeMode(if (enabled) "dark" else "light")
        }
    }
    
    fun requestPinRemoval() {
        showPinConfirmDialog = true
        confirmPinError = null
    }
    
    fun cancelPinRemoval() {
        showPinConfirmDialog = false
        confirmPinError = null
    }
    
    fun verifyAndRemovePin(pin: String) {
        viewModelScope.launch {
            val storedHash = preferencesManager.pinHash.first()
            if (storedHash != null && preferencesManager.verifyPin(pin, storedHash)) {
                preferencesManager.clearPin()
                showPinConfirmDialog = false
            } else {
                confirmPinError = "Incorrect PIN"
            }
        }
    }

    fun getBatteryOptimizationIntent(): android.content.Intent {
        return batteryHelper.requestIgnoreBatteryOptimizations()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToPinSetup: () -> Unit,
    onNavigateToPinConfirm: () -> Unit,
    onNavigateToDebug: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold
                    ) 
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance
            ModernCard {
                SectionHeader(title = "Appearance")
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    subtitle = "Toggle dark mode",
                    trailing = {
                        Switch(
                            checked = viewModel.isDarkTheme,
                            onCheckedChange = { viewModel.updateDarkTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )
            }

            // Security
            ModernCard {
                SectionHeader(title = "Security")
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "App Lock PIN",
                    subtitle = if (viewModel.isPinSet) "PIN is set" else "Set a PIN to lock the app",
                    trailing = {
                        if (viewModel.isPinSet) {
                            Button(
                                onClick = onNavigateToPinConfirm,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                Text("Remove")
                            }
                        } else {
                            Button(
                                onClick = onNavigateToPinSetup,
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                Text("Set PIN")
                            }
                        }
                    }
                )
            }

            // Performance
            ModernCard {
                SectionHeader(title = "Performance")
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsItem(
                    icon = Icons.Default.BatteryFull,
                    title = "Ignore Battery Optimizations",
                    subtitle = "Required for reliable background syncs",
                    onClick = {
                        try {
                            context.startActivity(viewModel.getBatteryOptimizationIntent())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    showChevron = true
                )
            }

            // Help & Rules
            ModernCard {
                SectionHeader(title = "Help & Rules")
                Spacer(modifier = Modifier.height(8.dp))
                
                var showHelpDialog by remember { mutableStateOf(false) }
                
                if (showHelpDialog) {
                    AlertDialog(
                        onDismissRequest = { showHelpDialog = false },
                        title = { Text("Sync Modes Explained") },
                        text = {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // SYNC Mode
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "🔄 SYNC (Source → Destination)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("• Copies new files from Source to Destination", style = MaterialTheme.typography.bodySmall)
                                    Text("• If file exists but is different, Destination file is renamed (e.g., file_04122025-1800.txt)", style = MaterialTheme.typography.bodySmall)
                                    Text("• Never deletes files from Destination", style = MaterialTheme.typography.bodySmall)
                                }
                                
                                Divider()
                                
                                // MOVE Mode
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "📦 MOVE (Source → Destination)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text("• Same as SYNC mode", style = MaterialTheme.typography.bodySmall)
                                    Text("• ⚠️ Deletes Source files after successful transfer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                    Text("• Use Case: Free up space on source device", style = MaterialTheme.typography.bodySmall)
                                }
                                
                                Divider()
                                
                                // MIRROR Mode
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "🔁 MIRROR (Bi-directional)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text("• Keeps both folders identical", style = MaterialTheme.typography.bodySmall)
                                    Text("• Copies new files to the other side", style = MaterialTheme.typography.bodySmall)
                                    Text("• ⚠️ Propagates deletions (delete on one side = delete on other)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                    Text("• If file changed on both sides, both are renamed", style = MaterialTheme.typography.bodySmall)
                                }
                                
                                Divider()
                                
                                // High Integrity
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "🛡️ High Integrity Check",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("• Uses checksums to verify file content", style = MaterialTheme.typography.bodySmall)
                                    Text("• Slower but guarantees 100% data integrity", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showHelpDialog = false }) {
                                Text("Got it")
                            }
                        }
                    )
                }
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Sync Modes Guide",
                    subtitle = "Learn about SYNC, MOVE, and MIRROR",
                    onClick = { showHelpDialog = true },
                    showChevron = true
                )
            }

            // About & Debug
            ModernCard {
                SectionHeader(title = "About")
                Spacer(modifier = Modifier.height(8.dp))
                
                // Easter Egg State
                var versionClickCount by remember { mutableStateOf(0) }
                var showAuthorDialog by remember { mutableStateOf(false) }

                if (showAuthorDialog) {
                    AlertDialog(
                        onDismissRequest = { showAuthorDialog = false },
                        title = { Text("About Author") },
                        text = { 
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Author : Kalaiselvan Arumugam")
                                Text("Github : https://github.com/kalaiselvan-arumugam/")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showAuthorDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = {
                        versionClickCount++
                        if (versionClickCount >= 5) {
                            showAuthorDialog = true
                            versionClickCount = 0
                        }
                    }
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                
                SettingsItem(
                    icon = Icons.Default.BugReport,
                    title = "Scheduler Debug",
                    subtitle = "View scheduled work status",
                    onClick = onNavigateToDebug,
                    showChevron = true
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showChevron: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
