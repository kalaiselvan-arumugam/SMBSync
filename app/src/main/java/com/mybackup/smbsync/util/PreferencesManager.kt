package com.mybackup.smbsync.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages app preferences using DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mybackup_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_PIN_HASH = stringPreferencesKey("pin_hash")
        private val KEY_APP_LOCKED = booleanPreferencesKey("app_locked")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val pinHash: Flow<String?> = context.dataStore.data.map { it[KEY_PIN_HASH] }
    val appLocked: Flow<Boolean> = context.dataStore.data.map { it[KEY_APP_LOCKED] ?: true }
    val themeMode: Flow<String> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: "system" }
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { it[KEY_FIRST_LAUNCH] ?: true }

    suspend fun setPinHash(hash: String) {
        context.dataStore.edit { it[KEY_PIN_HASH] = hash }
    }

    suspend fun clearPin() {
        context.dataStore.edit { it.remove(KEY_PIN_HASH) }
    }

    suspend fun setAppLocked(locked: Boolean) {
        context.dataStore.edit { it[KEY_APP_LOCKED] = locked }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { it[KEY_FIRST_LAUNCH] = isFirst }
    }

    /**
     * Hash PIN using SHA-256
     */
    fun hashPin(pin: String): String {
        val bytes = pin.toByteArray()
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verify PIN against stored hash
     */
    suspend fun verifyPin(pin: String, storedHash: String): Boolean {
        return hashPin(pin) == storedHash
    }
}
