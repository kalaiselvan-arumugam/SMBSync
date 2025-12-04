package com.mybackup.smbsync.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mybackup.smbsync.ui.history.HistoryScreen
import com.mybackup.smbsync.ui.servers.ServerListScreen
import com.mybackup.smbsync.ui.settings.SettingsScreen
import com.mybackup.smbsync.ui.sync.SyncConfigListScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    onAddServer: () -> Unit,
    onEditServer: (Long, String?, Int?, String?) -> Unit,
    onAddConfig: () -> Unit,
    onEditConfig: (Long) -> Unit,
    onNavigateToPinSetup: () -> Unit,
    onNavigateToPinConfirm: () -> Unit,
    onNavigateToDebug: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Cloud, contentDescription = "Servers") },
                    label = { Text("Servers") },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Sync, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = pagerState.currentPage == 3,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(3)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> ServerListScreen(
                    onAddServer = onAddServer,
                    onEditServer = onEditServer
                )
                1 -> SyncConfigListScreen(
                    onAddConfig = onAddConfig,
                    onEditConfig = onEditConfig
                )
                2 -> HistoryScreen()
                3 -> SettingsScreen(
                    onNavigateToPinSetup = onNavigateToPinSetup,
                    onNavigateToPinConfirm = onNavigateToPinConfirm,
                    onNavigateToDebug = onNavigateToDebug
                )
            }
        }
    }
}
