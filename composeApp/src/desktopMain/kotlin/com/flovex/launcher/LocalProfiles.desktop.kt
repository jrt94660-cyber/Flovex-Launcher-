package com.flovex.launcher

import java.util.prefs.Preferences

private val profilePreferences = Preferences.userRoot().node("com/flovex/launcher")

actual fun loadLocalProfiles(): List<String> =
    profilePreferences.get("offlineProfiles", "").split('\n').map { it.trim() }.filter { it.isNotEmpty() }.distinct()

actual fun saveLocalProfiles(profiles: List<String>) {
    profilePreferences.put("offlineProfiles", profiles.map { it.trim() }.filter { it.isNotEmpty() }.distinct().joinToString("\n"))
}
