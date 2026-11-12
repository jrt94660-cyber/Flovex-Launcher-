package com.flovex.launcher

import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

actual fun fetchMinecraftVersions(onResult: (List<MinecraftVersion>?, String?) -> Unit) {
    thread(name = "flovex-version-manifest", isDaemon = true) {
        val result = runCatching {
            val connection = URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
                .openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 12000
            connection.setRequestProperty("Accept", "application/json")
            try {
                require(connection.responseCode == 200) { "Minecraft manifest HTTP ${connection.responseCode}" }
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                Regex("""\{"id"\s*:\s*"([^"]+)"\s*,\s*"type"\s*:\s*"([^"]+)"""")
                    .findAll(json)
                    .map { MinecraftVersion(it.groupValues[1], it.groupValues[2]) }
                    .filter { it.type == "release" || it.type == "snapshot" }
                    .toList()
            } finally { connection.disconnect() }
        }
        onResult(result.getOrNull(), result.exceptionOrNull()?.message)
    }
}
