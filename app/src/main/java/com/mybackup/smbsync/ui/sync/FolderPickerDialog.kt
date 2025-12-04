package com.mybackup.smbsync.ui.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderPickerDialog(
    initialPath: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    onListFolders: suspend (String) -> List<String>,
    title: String
) {
    var currentPath by remember { mutableStateOf(initialPath.ifEmpty { if (title.contains("Remote")) "/" else android.os.Environment.getExternalStorageDirectory().absolutePath }) }
    var folders by remember { mutableStateOf(emptyList<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(currentPath) {
        isLoading = true
        folders = emptyList() // Clear list while loading to prevent selection of old items
        folders = onListFolders(currentPath)
        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        IconButton(onClick = { onSelect(currentPath) }) {
                            Icon(
                                Icons.Default.Check, 
                                contentDescription = "Select",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentPath,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    if (currentPath != "/" && currentPath != "/storage/emulated/0") {
                        item {
                            ListItem(
                                headlineContent = { Text("..", fontWeight = FontWeight.Bold) },
                                leadingContent = { 
                                    Icon(
                                        Icons.Default.Folder, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    ) 
                                },
                                modifier = Modifier.clickable {
                                    val parent = File(currentPath).parent
                                    if (parent != null) currentPath = parent
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                )
                            )
                        }
                    }
                    
                    items(folders) { folder ->
                        ListItem(
                            headlineContent = { Text(folder) },
                            leadingContent = { 
                                Icon(
                                    Icons.Default.Folder, 
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            modifier = Modifier.clickable {
                                currentPath = if (currentPath.endsWith("/")) "$currentPath$folder" else "$currentPath/$folder"
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}
