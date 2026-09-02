package com.ksndtech.holistictransceiver.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

class UdpSender {
    private val socket = DatagramSocket()

    suspend fun send(data: ByteArray, host: String, port: Int) = withContext(Dispatchers.IO) {
        try {
            val address = InetSocketAddress(host, port)
            val packet = DatagramPacket(data, data.size, address)
            socket.send(packet)
        } catch (e: Exception) {
            // 送信失敗はログに留め、アプリ全体をクラッシュさせない
            Log.e("UdpSender", "送信失敗: ${e.message}")
        }
    }

    fun close() {
        socket.close()
    }
}