package com.mybackup.smbsync.data.remote

import android.content.Context
import com.mybackup.smbsync.data.model.SmbServer
import jcifs.context.SingletonContext
import jcifs.netbios.Name
import jcifs.netbios.NbtAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmbDiscoveryService @Inject constructor() {

    fun discoverServers(): Flow<SmbServer> = flow {
        try {
            val context = SingletonContext.getInstance()
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces() ?: return@flow
            
            for (intf in interfaces) {
                if (intf.isLoopback || !intf.isUp) continue
                
                val addresses = intf.interfaceAddresses
                for (addr in addresses) {
                    val ip = addr.address
                    if (ip is java.net.Inet4Address) {
                        val subnet = ip.address
                        val prefixLength = addr.networkPrefixLength
                        
                        // Simple scan for /24 subnet (most common)
                        if (prefixLength >= 24) {
                            val baseIp = subnet[0].toInt() and 0xFF shl 24 or
                                    (subnet[1].toInt() and 0xFF shl 16) or
                                    (subnet[2].toInt() and 0xFF shl 8) or
                                    (subnet[3].toInt() and 0xFF)
                            
                            // Scan 1-254
                            for (i in 1..254) {
                                val targetIpVal = (baseIp and 0xFFFFFF00.toInt()) or i
                                val targetIpBytes = byteArrayOf(
                                    (targetIpVal shr 24).toByte(),
                                    (targetIpVal shr 16).toByte(),
                                    (targetIpVal shr 8).toByte(),
                                    targetIpVal.toByte()
                                )
                                val targetAddress = InetAddress.getByAddress(targetIpBytes)
                                
                                if (targetAddress == ip) continue // Skip own IP
                                
                                // Try to connect to port 445 (SMB) with short timeout
                                try {
                                    val socket = java.net.Socket()
                                    socket.connect(java.net.InetSocketAddress(targetAddress, 445), 200)
                                    socket.close()
                                    
                                    // If connection successful, emit server
                                    emit(
                                        SmbServer(
                                            name = targetAddress.hostName ?: targetAddress.hostAddress,
                                            address = targetAddress.hostAddress,
                                            username = "",
                                            encryptedPassword = ""
                                        )
                                    )
                                } catch (e: Exception) {
                                    // Port closed or unreachable
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)
}
