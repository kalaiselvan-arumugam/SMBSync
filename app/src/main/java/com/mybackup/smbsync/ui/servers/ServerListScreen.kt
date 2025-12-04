package com.mybackup.smbsync.ui.servers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.data.model.SmbServer
import com.mybackup.smbsync.data.repository.SmbServerRepository
import com.mybackup.smbsync.ui.components.EmptyState
import com.mybackup.smbsync.ui.components.ModernCard
import com.mybackup.smbsync.ui.components.SectionHeader
import com.mybackup.smbsync.ui.components.StatusChip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ServerListViewModel @Inject constructor(
    private val repository: SmbServerRepository,
    private val discoveryService: com.mybackup.smbsync.data.remote.SmbDiscoveryService
) : ViewModel() {
    val servers = repository.getAllServers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _discoveredServers = kotlinx.coroutines.flow.MutableStateFlow<List<SmbServer>>(emptyList())
    val discoveredServers = _discoveredServers.asStateFlow()
    
    var isScanning by mutableStateOf(false)
        private set

    fun deleteServer(server: SmbServer) {
        viewModelScope.launch {
            repository.deleteServer(server)
        }
    }
    
    fun scanForServers() {
        viewModelScope.launch {
            isScanning = true
            _discoveredServers.value = emptyList()
            try {
                discoveryService.discoverServers().collect { server ->
                    _discoveredServers.value += server
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isScanning = false
            }
        }
    }
    

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    onAddServer: () -> Unit,
    onEditServer: (Long, String?, Int?, String?) -> Unit,
    viewModel: ServerListViewModel = hiltViewModel()
) {
    val servers by viewModel.servers.collectAsState()
    val discoveredServersState by viewModel.discoveredServers.collectAsState()
    var serverToDelete by remember { mutableStateOf<SmbServer?>(null) }

    if (serverToDelete != null) {
        AlertDialog(
            onDismissRequest = { serverToDelete = null },
            title = { Text("Delete Server") },
            text = { Text("Are you sure you want to delete '${serverToDelete?.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        serverToDelete?.let { viewModel.deleteServer(it) }
                        serverToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { serverToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "SMB Servers",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    // Only show scan button if there are configured servers
                    if (servers.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.scanForServers() },
                            enabled = !viewModel.isScanning
                        ) {
                            Text("Scan")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddServer,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Server")
            }
        }
    ) { padding ->
        if (servers.isEmpty() && !viewModel.isScanning && discoveredServersState.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Computer,
                title = "No Servers",
                message = "Add a server manually or scan your local network to discover SMB servers",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                action = {
                    Button(
                        onClick = { viewModel.scanForServers() },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Network")
                    }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Scanning indicator
                if (viewModel.isScanning) {
                    item {
                        ModernCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Column {
                                    Text(
                                        "Scanning network...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Looking for SMB servers",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Discovered servers section
                val discovered = discoveredServersState
                if (discovered.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Discovered Servers",
                            action = {
                                StatusChip(
                                    label = "${discovered.size} found",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        )
                    }
                    items(discovered) { server ->
                        ServerItem(
                            server = server,
                            onEdit = { 
                                // Pass details for new server
                                onEditServer(-1L, server.address, server.port, server.protocol.name)
                            },
                            onDelete = { },
                            isDiscovered = true
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Configured servers section
                if (servers.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "My Servers",
                            action = {
                                StatusChip(
                                    label = "${servers.size} configured",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                    items(servers) { server ->
                        ServerItem(
                            server = server,
                            onEdit = { onEditServer(server.id, null, null, null) },
                            onDelete = { serverToDelete = server }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerItem(
    server: SmbServer,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDiscovered: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    ModernCard(
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Server icon with background
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDiscovered) 
                    MaterialTheme.colorScheme.tertiaryContainer 
                else 
                    MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = if (isDiscovered) 
                            MaterialTheme.colorScheme.onTertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Server info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = server.name.ifBlank { server.address },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (isDiscovered) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Tap to add",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "${server.address}:${server.port}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (!isDiscovered) {
                    StatusChip(
                        label = server.protocol.name,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Actions
            if (!isDiscovered) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.ChevronRight, 
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
