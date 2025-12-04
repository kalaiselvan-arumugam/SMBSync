package com.mybackup.smbsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mybackup.smbsync.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import android.app.Activity
import com.mybackup.smbsync.ui.theme.MyBackupTheme
import com.mybackup.smbsync.ui.auth.AppLockScreen
import com.mybackup.smbsync.ui.auth.PinSetupScreen
import com.mybackup.smbsync.ui.servers.ServerListScreen
import com.mybackup.smbsync.ui.servers.ServerConfigScreen
import com.mybackup.smbsync.ui.sync.SyncConfigListScreen
import com.mybackup.smbsync.ui.sync.SyncConfigScreen
import com.mybackup.smbsync.ui.history.HistoryScreen
import com.mybackup.smbsync.ui.settings.SettingsScreen
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request All Files Access permission for Android 11+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                try {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = android.net.Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to general settings if specific intent fails
                    val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
            }
        }
        
        setContent {
            // Request notification permission on first launch (Android 13+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val context = LocalContext.current
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    android.util.Log.d("MainActivity", "Notification permission granted: $isGranted")
                }
                
                LaunchedEffect(Unit) {
                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    
                    if (!hasPermission) {
                        android.util.Log.d("MainActivity", "Requesting notification permission")
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            
            MyBackupApp(preferencesManager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBackupApp(preferencesManager: PreferencesManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // PreferencesManager is now passed in
    
    // State to track initial setup and authentication status
    var isFirstLaunch by remember { mutableStateOf(true) }
    var pinHash by remember { mutableStateOf<String?>(null) }
    var isAuthenticated by remember { mutableStateOf(false) }
    var themeMode by remember { mutableStateOf("system") }
    var isPinLoaded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Collect initial setup state
    LaunchedEffect(Unit) {
        preferencesManager.isFirstLaunch.collectLatest {
            isFirstLaunch = it
        }
    }
    
    // Collect pin hash state
    LaunchedEffect(Unit) {
        preferencesManager.pinHash.collectLatest {
            pinHash = it
            isPinLoaded = true
        }
    }

    // Collect theme mode
    LaunchedEffect(Unit) {
        preferencesManager.themeMode.collectLatest {
            themeMode = it
        }
    }

    // Determine dark theme
    val isSystemDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    // Handle initial authentication state
    LaunchedEffect(isPinLoaded, pinHash) {
        if (isPinLoaded) {
            if (pinHash == null) {
                // No PIN set, allow access
                isAuthenticated = true
            }
            // If PIN is set, keep isAuthenticated as false to show lock screen
        }
    }

    MyBackupTheme(darkTheme = isDarkTheme) {
        // Set system bar colors to transparent for edge-to-edge
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                
                // Enable edge-to-edge
                WindowCompat.setDecorFitsSystemWindows(window, false)
                
                // Set transparent colors
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                
                // Set status bar icons color
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
                // Set navigation bar icons color  
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
        
        if (!isAuthenticated) {
            // Show Auth Screens or PIN Setup
            NavHost(
                navController = navController,
                startDestination = "app_lock"
            ) {
                composable("app_lock") {
                    // AppLockScreen needs to be defined to accept preferencesManager
                    AppLockScreen(onUnlock = { isAuthenticated = true })
                }
                composable("pin_setup") {
                    PinSetupScreen(onPinSet = {
                        // After PIN is set, mark setup as complete and authenticate
                        coroutineScope.launch {
                            preferencesManager.setFirstLaunch(false)
                        }
                        isAuthenticated = true
                    })
                }
            }
        } else {
            // Show Main App
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    com.mybackup.smbsync.ui.MainScreen(
                        onAddServer = { navController.navigate("server_config/-1") },
                        onEditServer = { id, address, port, protocol -> 
                            val route = if (id != -1L) {
                                "server_config/$id"
                            } else {
                                val addr = address ?: ""
                                val prt = port ?: -1
                                val proto = protocol ?: ""
                                "server_config/$id?address=$addr&port=$prt&protocol=$proto"
                            }
                            navController.navigate(route) 
                        },
                        onAddConfig = { navController.navigate("sync_config/-1") },
                        onEditConfig = { id -> navController.navigate("sync_config/$id") },
                        onNavigateToPinSetup = { navController.navigate("pin_setup") },
                        onNavigateToPinConfirm = { navController.navigate("pin_confirm") },
                        onNavigateToDebug = { navController.navigate("debug_scheduler") }
                    )
                }
                
                composable(
                    route = "server_config/{serverId}?address={address}&port={port}&protocol={protocol}",
                    arguments = listOf(
                        navArgument("serverId") { type = NavType.LongType },
                        navArgument("address") { type = NavType.StringType; defaultValue = "" },
                        navArgument("port") { type = NavType.IntType; defaultValue = -1 },
                        navArgument("protocol") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val serverId = backStackEntry.arguments?.getLong("serverId") ?: -1L
                    val address = backStackEntry.arguments?.getString("address")?.takeIf { it.isNotEmpty() }
                    val port = backStackEntry.arguments?.getInt("port")?.takeIf { it != -1 }
                    val protocol = backStackEntry.arguments?.getString("protocol")?.takeIf { it.isNotEmpty() }
                    
                    ServerConfigScreen(
                        onBack = { navController.popBackStack() },
                        serverId = serverId,
                        initialAddress = address,
                        initialPort = port,
                        initialProtocol = protocol
                    )
                }
                
                composable(
                    route = "sync_config/{configId}",
                    arguments = listOf(navArgument("configId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val configId = backStackEntry.arguments?.getLong("configId") ?: -1L
                    SyncConfigScreen(
                        onBack = { navController.popBackStack() },
                        configId = configId
                    )
                }
                
                composable("debug_scheduler") {
                    com.mybackup.smbsync.ui.settings.WorkManagerDebugScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                
                composable("pin_setup") {
                    PinSetupScreen(onPinSet = {
                        navController.popBackStack()
                    })
                }
                
                composable("pin_confirm") {
                    com.mybackup.smbsync.ui.auth.PinConfirmScreen(
                        onPinConfirmed = {
                            navController.popBackStack()
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
    }
}
}

@Composable
fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when (title) {
                "Servers" -> Icons.Default.Cloud
                "Tasks" -> Icons.Default.Sync
                "History" -> Icons.Default.History
                else -> Icons.Default.Settings
            },
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coming soon...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
