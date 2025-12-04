package com.mybackup.smbsync.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    var pin by mutableStateOf("")
        private set
    
    var error by mutableStateOf<String?>(null)
        private set

    fun onPinDigit(digit: String, onUnlock: () -> Unit) {
        if (pin.length < 4) {
            pin += digit
            error = null
            
            if (pin.length == 4) {
                viewModelScope.launch {
                    val storedHash = preferencesManager.pinHash.first()
                    if (storedHash != null && preferencesManager.verifyPin(pin, storedHash)) {
                        onUnlock()
                    } else {
                        error = "Incorrect PIN"
                        pin = ""
                    }
                }
            }
        }
    }

    fun onBackspace() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            error = null
        }
    }
}



@Composable
fun AppLockScreen(
    onUnlock: () -> Unit,
    viewModel: AppLockViewModel = hiltViewModel()
) {
    PinEntryContent(
        title = "Unlock MyBackup",
        pin = viewModel.pin,
        error = viewModel.error,
        onPinDigit = { viewModel.onPinDigit(it, onUnlock) },
        onBackspace = { viewModel.onBackspace() }
    )
}

@Composable
fun PinEntryContent(
    title: String,
    pin: String,
    error: String?,
    onPinDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PIN Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < pin.length) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Number Pad
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rows 1-3
                for (row in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (col in 1..3) {
                            val number = (row * 3 + col).toString()
                            PinButton(
                                text = number,
                                onClick = { onPinDigit(number) }
                            )
                        }
                    }
                }
                
                // Row 4 (0 and backspace)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.size(80.dp))
                    PinButton(
                        text = "0",
                        onClick = { onPinDigit("0") }
                    )
                    IconButton(
                        onClick = onBackspace,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
