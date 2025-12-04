package com.mybackup.smbsync.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles encryption and decryption of sensitive data using Android Keystore
 */
@Singleton
class CredentialEncryption @Inject constructor() {
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "MyBackupCredentialKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }
        
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    /**
     * Encrypt a string value
     * @return Base64 encoded string in format: IV:EncryptedData
     */
    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        
        return "$ivBase64:$encryptedBase64"
    }

    /**
     * Decrypt an encrypted string
     * @param encrypted Base64 encoded string in format: IV:EncryptedData
     */
    fun decrypt(encrypted: String): String {
        val parts = encrypted.split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid encrypted data format")
        }
        
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
