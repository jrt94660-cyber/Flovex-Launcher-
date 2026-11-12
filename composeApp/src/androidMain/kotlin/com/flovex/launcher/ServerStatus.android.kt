package com.flovex.launcher

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

actual fun checkMinecraftServer(host: String, port: Int, onResult: (Boolean, String) -> Unit) {
    thread(name = "flovex-server-ping", isDaemon = true) {
        val result = runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 4000)
                socket.soTimeout = 4000
                val out = DataOutputStream(socket.getOutputStream())
                val input = DataInputStream(socket.getInputStream())
                val handshake = java.io.ByteArrayOutputStream()
                val packet = DataOutputStream(handshake)
                writeVarInt(packet, 0)
                writeVarInt(packet, 765)
                writeString(packet, host)
                packet.writeShort(port)
                writeVarInt(packet, 1)
                val payload = handshake.toByteArray()
                writeVarInt(out, payload.size)
                out.write(payload)
                val request = byteArrayOf(0)
                writeVarInt(out, request.size)
                out.write(request)
                out.flush()
                readVarInt(input)
                require(readVarInt(input) == 0) { "Unexpected status packet" }
                val jsonLength = readVarInt(input)
                require(jsonLength in 1..65536) { "Invalid status response" }
                val json = ByteArray(jsonLength)
                input.readFully(json)
                val body = json.toString(Charsets.UTF_8)
                val version = Regex("""\"name\"\s*:\s*\"([^\"]+)\"""").find(body)?.groupValues?.get(1)
                val online = Regex("""\"online\"\s*:\s*(\d+)""").find(body)?.groupValues?.get(1)
                val max = Regex("""\"max\"\s*:\s*(\d+)""").find(body)?.groupValues?.get(1)
                listOfNotNull(version, if (online != null && max != null) "$online/$max players" else null)
                    .joinToString(" • ").ifBlank { "Server responded" }
            }
        }
        onResult(result.isSuccess, result.getOrElse { "No response: ${it.message ?: "connection failed"}" })
    }
}

private fun writeVarInt(out: DataOutputStream, value: Int) {
    var v = value
    while (v and -128 != 0) { out.writeByte(v and 127 or 128); v = v ushr 7 }
    out.writeByte(v)
}
private fun readVarInt(input: DataInputStream): Int {
    var value = 0
    var position = 0
    while (true) {
        val current = input.readUnsignedByte()
        value = value or ((current and 127) shl position)
        if (current and 128 == 0) return value
        position += 7
        require(position < 32) { "VarInt is too big" }
    }
}
private fun writeString(out: DataOutputStream, value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    writeVarInt(out, bytes.size)
    out.write(bytes)
}
