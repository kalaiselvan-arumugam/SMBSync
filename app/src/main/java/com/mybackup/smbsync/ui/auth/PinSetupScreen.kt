package com.mybackup.smbsync.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    var pin by mutableStateOf("")
        private set
    
    var confirmPin by mutableStateOf("")
        private set
    
    var isConfirming by mutableStateOf(false)
        private set
    
    var error by mutableStateOf<String?>(null)
        private set

    fun onPinDigit(digit: String, onPinSet: () -> Unit) {
        if (pin.length < 4) {
            pin += digit
            error = null
            
            if (pin.length == 4) {
                if (isConfirming) {
                    if (pin == confirmPin) {
                        viewModelScope.launch {
                            val hash = preferencesManager.hashPin(pin)
                            preferencesManager.setPinHash(hash)
                            onPinSet()
                        }
                    } else {
                        error = "PINs do not match"
                        pin = ""
                    }
                } else {
                    confirmPin = pin
                    pin = ""
                    isConfirming = true
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
    
    fun reset() {
        pin = ""
        confirmPin = ""
        isConfirming = false
        error = null
    }
}

@Composable
fun PinSetupScreen(
    onPinSet: () -> Unit,
    viewModel: PinViewModel = hiltViewModel()
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
            Text(
                text = if (viewModel.isConfirming) "Confirm PIN" else "Create PIN",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (viewModel.isConfirming) "Re-enter your 4-digit PIN" else "Enter a 4-digit PIN to secure your data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                if (index < viewModel.pin.length) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }

            if (viewModel.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "back")
                )

                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.size(80.dp))
                            } else if (key == "back") {
                                IconButton(
                                    onClick = { viewModel.onBackspace() },
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.onPinDigit(key, onPinSet) },
                                    modifier = Modifier.size(80.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
