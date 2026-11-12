package com.flovex.launcher

/**
 * Performs a Minecraft Java Edition Server List Ping.
 * Implementations must invoke the callback off the UI thread.
 */
expect fun checkMinecraftServer(host: String, port: Int, onResult: (online: Boolean, details: String) -> Unit)
