package com.flovex.launcher

import android.content.Context

object LauncherAndroidContext {
    lateinit var appContext: Context
}

actual fun loadLocalProfiles(): List<String> {
    if (!LauncherAndroidContext::appContext.isInitialized) return emptyList()
    return LauncherAndroidContext.appContext.getSharedPreferences("flovex_launcher", Context.MODE_PRIVATE)
        .getString("offlineProfiles", "").orEmpty()
        .split('\n').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}

actual fun saveLocalProfiles(profiles: List<String>) {
    if (!LauncherAndroidContext::appContext.isInitialized) return
    LauncherAndroidContext.appContext.getSharedPreferences("flovex_launcher", Context.MODE_PRIVATE)
        .edit()
        .putString("offlineProfiles", profiles.map { it.trim() }.filter { it.isNotEmpty() }.distinct().joinToString("\n"))
        .apply()
}
