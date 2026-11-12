package com.flovex.launcher

import android.content.Context

object LauncherAndroidContext {
    var appContext: Context? = null
}

actual fun loadLocalProfiles(): List<String> {
    val context = LauncherAndroidContext.appContext ?: return emptyList()
    return context.getSharedPreferences("flovex_launcher", Context.MODE_PRIVATE)
        .getString("offlineProfiles", "").orEmpty()
        .split('\n').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}

actual fun saveLocalProfiles(profiles: List<String>) {
    val context = LauncherAndroidContext.appContext ?: return
    context.getSharedPreferences("flovex_launcher", Context.MODE_PRIVATE)
        .edit()
        .putString("offlineProfiles", profiles.map { it.trim() }.filter { it.isNotEmpty() }.distinct().joinToString("\n"))
        .apply()
}
