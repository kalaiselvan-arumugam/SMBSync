package com.mybackup.smbsync.data.remote

import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ensureActive
import java.io.InputStream
import java.io.OutputStream
import java.util.Properties

/**
 * SMB Client wrapper using JCIFS-NG
 */
class SmbClient(
    private val address: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val domain: String?,
    private val protocol: String // "SMB1", "SMB2", "SMB3"
) {
    private val host: String
    private val sharePath: String

    init {
        // Parse address to separate host and share path
        // Input: "192.168.1.1/samba" -> host="192.168.1.1", sharePath="samba"
        val parts = address.replace("\\", "/").split("/", limit = 2)
        host = parts[0]
        sharePath = if (parts.size > 1) parts[1] else ""
    }

    private val context: CIFSContext by lazy {
        val props = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", when (protocol) {
                "SMB1" -> "SMB1"
                "SMB2" -> "SMB202"
                "SMB3" -> "SMB300"
                else -> "SMB202"
            })
            setProperty("jcifs.smb.client.maxVersion", when (protocol) {
                "SMB1" -> "SMB1"
                "SMB2" -> "SMB210"
                "SMB3" -> "SMB311"
                else -> "SMB311"
            })
            setProperty("jcifs.smb.client.responseTimeout", "30000")
            setProperty("jcifs.smb.client.connTimeout", "10000")
            // Enable signing as seen in user's screenshot
            setProperty("jcifs.smb.client.ipcSigningEnforced", "true")
            
            // Legacy configuration for SMB1/Older Servers
            if (protocol == "SMB1") {
                setProperty("jcifs.smb.client.useExtendedSecurity", "false")
                setProperty("jcifs.smb.lmCompatibility", "0")
                setProperty("jcifs.smb.client.disablePlainTextPasswords", "false")
            }
        }
        
        val config = PropertyConfiguration(props)
        val baseContext = BaseContext(config)
        
        val auth = NtlmPasswordAuthenticator(domain, username, password)
        baseContext.withCredentials(auth)
    }

    /**
     * Test connection to SMB server
     */
    suspend fun testConnection() = withContext(Dispatchers.IO) {
        // If share path is provided, connect to it directly. Otherwise root.
        val url = if (sharePath.isNotEmpty()) {
            "smb://$host:$port/$sharePath/"
        } else {
            "smb://$host:$port/"
        }
        
        val file = SmbFile(url, context)
        
        if (sharePath.isNotEmpty()) {
            // If checking a specific share, just check existence/access
            if (!file.exists()) {
                throw Exception("Share not found or access denied")
            }
        } else {
            // If checking root, try to list shares
            file.listFiles()
        }
        Unit
    }

    /**
     * List files in a directory
     */
    suspend fun listFiles(remotePath: String): List<SmbFileInfo> = withContext(Dispatchers.IO) {
        try {
            var url = buildUrl(remotePath)
            if (!url.endsWith("/")) {
                url += "/"
            }
            android.util.Log.d("SmbClient", "Listing files for URL: $url")
            val dir = SmbFile(url, context)
            
            if (!dir.exists() || !dir.isDirectory) {
                return@withContext emptyList()
            }
            
            dir.listFiles()?.map { file ->
                SmbFileInfo(
                    name = file.name.removeSuffix("/"),
                    path = file.path,
                    isDirectory = file.isDirectory,
                    size = if (file.isFile) file.length() else 0,
                    lastModified = file.lastModified
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Download a file from SMB to local storage
     */
    suspend fun downloadFile(
        remotePath: String,
        outputStream: OutputStream,
        onProgress: ((Long, Long) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            
            if (!file.exists() || !file.isFile) {
                return@withContext false
            }
            
            val totalSize = file.length()
            var bytesRead = 0L
            val buffer = ByteArray(8192)
            
            file.inputStream.use { input ->
                outputStream.use { output ->
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        ensureActive() // Check for cancellation
                        output.write(buffer, 0, read)
                        bytesRead += read
                        onProgress?.invoke(bytesRead, totalSize)
                    }
                }
            }
            true
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            false
        }
    }

    /**
     * Upload a file from local storage to SMB
     */
    suspend fun uploadFile(
        inputStream: InputStream,
        remotePath: String,
        fileSize: Long,
        onProgress: ((Long, Long) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            
            // Create parent directories if needed
            file.parent?.let { parentPath ->
                val parentDir = SmbFile(parentPath, context)
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
            }
            
            var bytesWritten = 0L
            val buffer = ByteArray(8192)
            
            inputStream.use { input ->
                file.outputStream.use { output ->
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        ensureActive() // Check for cancellation
                        output.write(buffer, 0, read)
                        bytesWritten += read
                        onProgress?.invoke(bytesWritten, fileSize)
                    }
                }
            }
            true
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            false
        }
    }

    /**
     * Set the last modified time of a remote file
     */
    suspend fun setLastModified(remotePath: String, time: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            if (file.exists()) {
                file.setLastModified(time)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("SmbClient", "Failed to set last modified: ${e.message}")
            false
        }
    }

    /**
     * Delete a file or directory
     */
    suspend fun delete(remotePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            
            if (file.exists()) {
                if (file.isDirectory) {
                    deleteRecursive(file)
                } else {
                    file.delete()
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create a directory
     */
    suspend fun createDirectory(remotePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val dir = SmbFile(url, context)
            dir.mkdirs()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Rename a file or directory
     */
    suspend fun rename(fromPath: String, toPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val fromUrl = buildUrl(fromPath)
            val toUrl = buildUrl(toPath)
            val fromFile = SmbFile(fromUrl, context)
            val toFile = SmbFile(toUrl, context)
            
            if (fromFile.exists()) {
                fromFile.renameTo(toFile)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if file/directory exists
     */
    suspend fun exists(remotePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            file.exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get file information
     */
    suspend fun getFileInfo(remotePath: String): SmbFileInfo? = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(remotePath)
            val file = SmbFile(url, context)
            
            if (!file.exists()) {
                return@withContext null
            }
            
            SmbFileInfo(
                name = file.name.removeSuffix("/"),
                path = file.path,
                isDirectory = file.isDirectory,
                size = if (file.isFile) file.length() else 0,
                lastModified = file.lastModified
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun buildUrl(path: String): String {
        val cleanPath = path.trim().removePrefix("/")
        // If we have a base share path, prepend it
        val fullPath = if (sharePath.isNotEmpty()) {
            "$sharePath/$cleanPath"
        } else {
            cleanPath
        }
        return "smb://$host:$port/$fullPath"
    }

    private fun deleteRecursive(dir: SmbFile) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteRecursive(file)
            } else {
                file.delete()
            }
        }
        dir.delete()
    }
}

/**
 * SMB file information
 */
data class SmbFileInfo(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)
