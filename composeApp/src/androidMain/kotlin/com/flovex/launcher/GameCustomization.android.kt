package com.flovex.launcher

import java.io.File

private fun addonDirectory(profile: String, kind: String): File {
    val context = LauncherAndroidContext.appContext
    val base = context?.getExternalFilesDir(null) ?: context?.filesDir
    val dir = File(File(File(base, "minecraft"), "profiles/${safe(profile)}"), if (kind == "mods") "mods" else "resourcepacks")
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
    if (!source.isFile || !source.name.endsWith(expected, true)) return "Choose a valid $expected file in app-accessible storage."
    val target = File(addonDirectory(profile, kind), source.name)
    if (target.exists()) return "That file already exists in this profile."
    source.copyTo(target)
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
    val context = LauncherAndroidContext.appContext ?: return defaultBindings
    val prefs = context.getSharedPreferences("controls_${safe(profile)}", 0)
    return defaultBindings.mapValues { (action, default) -> prefs.getString(action, default) ?: default }
}
actual fun saveControlBinding(profile: String, action: String, key: String) {
    LauncherAndroidContext.appContext?.getSharedPreferences("controls_${safe(profile)}", 0)?.edit()?.putString(action, key)?.apply()
}
