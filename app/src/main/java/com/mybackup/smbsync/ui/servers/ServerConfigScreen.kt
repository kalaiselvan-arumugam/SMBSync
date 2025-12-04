package com.mybackup.smbsync.ui.servers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.data.model.SmbProtocol
import com.mybackup.smbsync.data.model.SmbServer
import com.mybackup.smbsync.data.remote.SmbClient
import com.mybackup.smbsync.data.repository.SmbServerRepository
import com.mybackup.smbsync.util.CredentialEncryption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerConfigViewModel @Inject constructor(
    private val repository: SmbServerRepository,
    private val credentialEncryption: CredentialEncryption
) : ViewModel() {
    var name by mutableStateOf("")
    var address by mutableStateOf("")
    var port by mutableStateOf("445")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var domain by mutableStateOf("")
    var protocol by mutableStateOf("SMB2") // Default
    
    var isTestingConnection by mutableStateOf(false)
    private var onTestComplete: ((Boolean, String) -> Unit)? = null

    fun setTestCallback(callback: (Boolean, String) -> Unit) {
        onTestComplete = callback
    }

    // Validation: all required fields must be filled
    val isValid: Boolean
        get() = address.isNotBlank() && 
                username.isNotBlank() && 
                password.isNotBlank() && 
                port.toIntOrNull() != null &&
                protocol.isNotBlank()

    private var existingServerId: Long? = null

    fun init(serverId: Long?, address: String?, port: Int?, protocol: String?) {
        if (serverId != null && serverId != -1L) {
            loadServer(serverId)
        } else {
            // Pre-fill if provided (only if fields are empty to avoid overwriting user edits on recomposition)
            if (this.address.isBlank() && address != null) this.address = address
            if (this.port == "445" && port != null) this.port = port.toString()
            if (this.protocol == "SMB2" && protocol != null) this.protocol = protocol
        }
    }

    fun loadServer(serverId: Long) {
        viewModelScope.launch {
            repository.getServerById(serverId)?.let { server ->
                existingServerId = server.id
                name = server.name
                address = server.address
                port = server.port.toString()
                username = server.username
                password = try {
                    credentialEncryption.decrypt(server.encryptedPassword)
                } catch (e: Exception) {
                    ""
                }
                domain = server.domain ?: ""
                protocol = server.protocol.name
            }
        }
    }

    fun saveServer(onSaved: () -> Unit) {
        viewModelScope.launch {
            val encryptedPassword = credentialEncryption.encrypt(password)
            val server = SmbServer(
                id = existingServerId ?: 0,
                name = name.ifBlank { address },
                address = address,
                port = port.toIntOrNull() ?: 445,
                username = username,
                encryptedPassword = encryptedPassword,
                domain = domain.ifBlank { null },
                protocol = SmbProtocol.valueOf(protocol)
            )
            
            if (existingServerId != null) {
                repository.updateServer(server)
            } else {
                repository.insertServer(server)
            }
            onSaved()
        }
    }

    fun deleteServer(onDeleted: () -> Unit) {
        viewModelScope.launch {
            existingServerId?.let { id ->
                repository.getServerById(id)?.let { server ->
                    repository.deleteServer(server)
                }
            }
            onDeleted()
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            isTestingConnection = true
            
            try {
                // Sanitize inputs but ALLOW slashes in address for share name
                val cleanAddress = address.trim()
                val cleanUsername = username.trim()
                val cleanPassword = password.trim()
                
                val client = SmbClient(
                    address = cleanAddress,
                    port = port.toIntOrNull() ?: 445,
                    username = cleanUsername,
                    password = cleanPassword,
                    domain = domain.ifBlank { null }?.trim(),
                    protocol = protocol
                )
                
                client.testConnection()
                onTestComplete?.invoke(true, "Connection successful!")
            } catch (e: Exception) {
                onTestComplete?.invoke(false, "Error: ${e.message ?: "Unknown error"}")
            } finally {
                isTestingConnection = false
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(
    onBack: () -> Unit,
    serverId: Long? = null,
    initialAddress: String? = null,
    initialPort: Int? = null,
    initialProtocol: String? = null,
    viewModel: ServerConfigViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(serverId, initialAddress, initialPort, initialProtocol) {
        viewModel.init(serverId, initialAddress, initialPort, initialProtocol)
    }

    LaunchedEffect(Unit) {
        viewModel.setTestCallback { success, message ->
            android.widget.Toast.makeText(
                context,
                message,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    var showPassword by remember { mutableStateOf(false) }
    var protocolExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val protocols = listOf("SMB1", "SMB2", "SMB3")
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Server") },
            text = { Text("Are you sure you want to delete this server? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteServer(onBack)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (serverId != null && serverId != -1L) "Edit Server" else "Add Server") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (serverId != null && serverId != -1L) {
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !viewModel.isTestingConnection
                        ) {
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    if (viewModel.isValid && !viewModel.isTestingConnection) {
                        viewModel.saveServer(onBack) 
                    }
                },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("Save Server") },
                containerColor = if (viewModel.isValid && !viewModel.isTestingConnection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (viewModel.isValid && !viewModel.isTestingConnection) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                expanded = !viewModel.isTestingConnection
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
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Server Name (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !viewModel.isTestingConnection
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = viewModel.address,
                    onValueChange = { viewModel.address = it },
                    label = { Text("Address (IP or Hostname)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !viewModel.isTestingConnection
                )
                
                OutlinedTextField(
                    value = viewModel.port,
                    onValueChange = { viewModel.port = it },
                    label = { Text("Port") },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !viewModel.isTestingConnection
                )
            }

            // Protocol Selection
            ExposedDropdownMenuBox(
                expanded = protocolExpanded,
                onExpandedChange = { if (!viewModel.isTestingConnection) protocolExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = viewModel.protocol,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Protocol Version") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = protocolExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !viewModel.isTestingConnection
                )
                ExposedDropdownMenu(
                    expanded = protocolExpanded,
                    onDismissRequest = { protocolExpanded = false }
                ) {
                    protocols.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.protocol = selectionOption
                                protocolExpanded = false
                            }
                        )
                    }
                }
            }

            if (viewModel.protocol == "SMB1") {
                Text(
                    text = "⚠️ SMB1 is insecure and deprecated. Use only if necessary.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = viewModel.domain,
                onValueChange = { viewModel.domain = it },
                label = { Text("Domain (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !viewModel.isTestingConnection
            )

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !viewModel.isTestingConnection
            )

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                enabled = !viewModel.isTestingConnection,
                trailingIcon = if (serverId == null || serverId == -1L) {
                    // Only show visibility toggle when adding new server
                    {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                } else {
                    null
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.testConnection() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isTestingConnection
            ) {
                if (viewModel.isTestingConnection) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Test Connection")
                }
            }


        }
    }
}
