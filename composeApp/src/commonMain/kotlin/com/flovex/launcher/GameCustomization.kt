package com.flovex.launcher

data class GameAddon(val fileName: String, val enabled: Boolean, val sizeBytes: Long)

expect fun listGameAddons(profile: String, kind: String): List<GameAddon>
expect fun importGameAddon(profile: String, kind: String, sourcePath: String): String?
expect fun setGameAddonEnabled(profile: String, kind: String, fileName: String, enabled: Boolean): Boolean
expect fun deleteGameAddon(profile: String, kind: String, fileName: String): Boolean
expect fun loadControlBindings(profile: String): Map<String, String>
expect fun saveControlBinding(profile: String, action: String, key: String)

