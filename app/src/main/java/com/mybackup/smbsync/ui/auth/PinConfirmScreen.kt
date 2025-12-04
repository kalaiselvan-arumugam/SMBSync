package com.mybackup.smbsync.ui.auth

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybackup.smbsync.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinConfirmViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    var pin by mutableStateOf("")
        private set
    
    var error by mutableStateOf<String?>(null)
        private set

    fun onPinDigit(digit: String, onConfirmed: () -> Unit) {
        if (pin.length < 4) {
            pin += digit
            error = null
            
            if (pin.length == 4) {
                viewModelScope.launch {
                    val storedHash = preferencesManager.pinHash.first()
                    if (storedHash != null && preferencesManager.verifyPin(pin, storedHash)) {
                        preferencesManager.clearPin()
                        onConfirmed()
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
fun PinConfirmScreen(
    onPinConfirmed: () -> Unit,
    onBack: () -> Unit,
    viewModel: PinConfirmViewModel = hiltViewModel()
) {
    // Reuse PinEntryContent from AppLockScreen
    // Note: PinEntryContent needs to be accessible. Since it's in AppLockScreen.kt, 
    // and if it's not public, we might need to move it or make it public.
    // Assuming I made it public in the previous step (default visibility is public in Kotlin).
    
    // We don't support biometric for PIN removal confirmation, usually.
    // Or we could, but user asked for "dialpad like page".
    
    PinEntryContent(
        title = "Confirm PIN to Remove",
        pin = viewModel.pin,
        error = viewModel.error,
        onPinDigit = { digit -> viewModel.onPinDigit(digit, onPinConfirmed) },
        onBackspace = { viewModel.onBackspace() }
    )
}
