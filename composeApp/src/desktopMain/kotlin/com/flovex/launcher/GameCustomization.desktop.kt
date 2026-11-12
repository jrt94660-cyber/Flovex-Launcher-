package com.flovex.launcher

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private fun addonDirectory(profile: String, kind: String): File {
    val root = File(System.getProperty("user.home"), ".minecraft")
    val profileRoot = File(root, "flovex-profiles/${safe(profile)}")
    val dir = File(profileRoot, if (kind == "mods") "mods" else "resourcepacks")
    dir.mkdirs()
    return dir
}
private fun safe(value: String) = value.replace(Regex("[^A-Za-z0-9_-]"), "_").take(32).ifBlank { "default" }

actual fun listGameAddons(profile: String, kind: String): List<GameAddon> =
    addonDirectory(profile, kind).listFiles()?.filter { it.isFile && (it.name.endsWith(".jar", true) || it.name.endsWith(".zip", true) || it.name.endsWith(".disabled", true)) }
        ?.sortedBy { it.name.lowercase() }?.map { GameAddon(it.name.removeSuffix(".disabled"), !it.name.endsWith(".disabled"), it.length()) } ?: emptyList()

actual fun importGameAddon(profile: String, kind: String, sourcePath: String): String? = try {
    val source = File(sourcePath.trim())
    val expected = if (kind == "mods") ".jar" else ".zip"
    if (!source.isFile || !source.name.endsWith(expected, true)) return "Choose a valid $expected file."
    val target = File(addonDirectory(profile, kind), source.name)
    if (target.exists()) return "That file already exists in this profile."
    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.COPY_ATTRIBUTES)
    null
} catch (e: Exception) { e.message ?: "Could not import file." }

actual fun setGameAddonEnabled(profile: String, kind: String, fileName: String, enabled: Boolean): Boolean = try {
    val dir = addonDirectory(profile, kind)
    val source = File(dir, fileName + if (enabled) ".disabled" else "")
    val target = File(dir, fileName.removeSuffix(".disabled") + if (enabled) "" else ".disabled")
    source.exists() && source.renameTo(target)
} catch (_: Exception) { false }

actual fun deleteGameAddon(profile: String, kind: String, fileName: String): Boolean =
    File(addonDirectory(profile, kind), fileName).delete()

actual fun loadControlBindings(profile: String): Map<String, String> {
    val file = File(File(System.getProperty("user.home"), ".minecraft/flovex-profiles/${safe(profile)}"), "controls.properties")
    if (!file.exists()) return defaultBindings
    return runCatching { file.readLines().mapNotNull { line -> line.substringBefore("=").takeIf { "=" in line }?.let { it to line.substringAfter("=") } }.toMap() }.getOrDefault(defaultBindings)
}
actual fun saveControlBinding(profile: String, action: String, key: String) {
    val dir = File(System.getProperty("user.home"), ".minecraft/flovex-profiles/${safe(profile)}")
    dir.mkdirs()
    val file = File(dir, "controls.properties")
    val values = loadControlBindings(profile).toMutableMap()
    values[action] = key
    file.writeText(values.entries.joinToString("\n") { "${it.key}=${it.value}" })
}
