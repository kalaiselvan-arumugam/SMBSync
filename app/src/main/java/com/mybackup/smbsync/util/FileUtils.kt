package com.mybackup.smbsync.util

import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * File utility functions
 */
object FileUtils {

    /**
     * Generate versioned filename with timestamp
     * Format: filename_{YYYYMMDD_HHmmss}.ext
     */
    fun generateVersionedFilename(originalPath: String): String {
        val file = File(originalPath)
        val nameWithoutExt = file.nameWithoutExtension
        val extension = file.extension
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        
        val versionedName = if (extension.isNotEmpty()) {
            "${nameWithoutExt}_${timestamp}.$extension"
        } else {
            "${nameWithoutExt}_${timestamp}"
        }
        
        return File(file.parent, versionedName).absolutePath
    }

    /**
     * Calculate MD5 hash of a file
     */
    fun calculateMD5(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Format file size in human-readable format
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "%.2f KB".format(kb)
        val mb = kb / 1024.0
        if (mb < 1024) return "%.2f MB".format(mb)
        val gb = mb / 1024.0
        return "%.2f GB".format(gb)
    }

    /**
     * Format transfer speed
     */
    fun formatSpeed(bytesPerSecond: Long): String {
        return "${formatFileSize(bytesPerSecond)}/s"
    }

    /**
     * Check if file is hidden
     */
    fun isHidden(file: File): Boolean {
        return file.isHidden || file.name.startsWith(".")
    }

    /**
     * Get file extension
     */
    fun getExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        return if (lastDot > 0 && lastDot < filename.length - 1) {
            filename.substring(lastDot + 1).lowercase()
        } else {
            ""
        }
    }

    /**
     * Check if file matches extension filter
     */
    fun matchesExtensionFilter(filename: String, extensions: List<String>): Boolean {
        if (extensions.isEmpty()) return true
        val ext = getExtension(filename)
        return extensions.any { it.lowercase() == ext }
    }

    /**
     * Check if file is a media file (for Archive mode)
     */
    fun isMediaFile(filename: String): Boolean {
        val ext = getExtension(filename)
        val photoExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic")
        val videoExtensions = listOf("mp4", "mov", "avi", "mkv", "webm", "3gp")
        return ext in photoExtensions || ext in videoExtensions
    }

    /**
     * Check if file is a photo
     */
    fun isPhoto(filename: String): Boolean {
        val ext = getExtension(filename)
        val photoExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic")
        return ext in photoExtensions
    }

    /**
     * Check if file is a video
     */
    fun isVideo(filename: String): Boolean {
        val ext = getExtension(filename)
        val videoExtensions = listOf("mp4", "mov", "avi", "mkv", "webm", "3gp")
        return ext in videoExtensions
    }
}
