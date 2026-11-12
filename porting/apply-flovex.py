#!/usr/bin/env python3
"""Apply the Flovex-only changes to the pinned Zalith Launcher 2 source."""
from pathlib import Path
import sys

root = Path(sys.argv[1])
base = root / "ZalithLauncher/src/main/java/com/movtery/zalithlauncher"

def patch(rel, old, new, label):
    p = base / rel
    s = p.read_text(encoding="utf-8")
    if new in s:
        return
    if old not in s:
        raise SystemExit(f"Could not apply {label}: source anchor changed in {rel}")
    p.write_text(s.replace(old, new, 1), encoding="utf-8")


# Zalith Launcher 2.8.2 includes MobileGlues as an in-app renderer.
# Do not force one renderer for a particular Minecraft version: let the user
# select a compatible built-in backend in each version's renderer settings.

# Present the fork as Flovex in Android/Gradle project metadata. The Kotlin
# namespace remains upstream-compatible for JNI and is not the installed app name.
settings_file = root / "settings.gradle.kts"
settings_text = settings_file.read_text(encoding="utf-8")
settings_text = settings_text.replace('rootProject.name = "ZalithLauncher"', 'rootProject.name = "FlovexLauncher"')
settings_file.write_text(settings_text, encoding="utf-8")

# Assert the installed Android label is supplied by Flovex build properties.
props = root / "ZalithLauncher/gradle.properties"
props_text = props.read_text(encoding="utf-8")
for key in ("launcher_name", "launcher_app_name"):
    import re
    props_text, count = re.subn(rf"(?m)^{key}=.*$", f"{key}=Flovex Launcher", props_text)
    if count != 1:
        raise SystemExit(f"Expected exactly one {key} in Gradle properties")
props.write_text(props_text, encoding="utf-8")

# Keep Minecraft's native Java file access working. Android's SAF URI tree cannot
# be passed to the JVM as a normal java.io.File, so request Android's all-files
# permission and use /storage/emulated/0/fl as the shared game-data root.
patch(
    "path/PathManager.kt",
    """            DIR_FILES_EXTERNAL = context.getExternalFilesDir(null) ?: run {""",
    """            DIR_FILES_EXTERNAL = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R &&
                Environment.isExternalStorageManager()) {
                File(Environment.getExternalStorageDirectory(), "fl").apply { mkdirs() }
            } else context.getExternalFilesDir(null) ?: run {""",
    "shared Flovex storage root"
)

# Resolve the default .minecraft path dynamically so it switches to /fl after
# the permission is granted, rather than retaining the pre-permission path.
patch(
    "game/path/GamePathManager.kt",
    """    private val defaultGamePath = File(PathManager.DIR_FILES_EXTERNAL, ".minecraft").absolutePath""",
    """    private val defaultGamePath: String
        get() = File(PathManager.DIR_FILES_EXTERNAL, ".minecraft").absolutePath""",
    "dynamic default game path"
)

# Seed FlovexSMP in Minecraft's native servers.dat file exactly once.
patch(
    "game/version/multiplayer/AllServers.kt",
    """                if (!dataFile.exists()) return@withContext

                val compound = NBTIO.readFile(dataFile, false, false)
                    ?: error("Failed to read the server.dat file as a CompoundTag.")
                val servers = compound.asList("servers", null)
                    ?: error("servers entry not found in the NBT structure tree.")

                servers.forEach { tag ->
                    val serverTag = tag as? CompoundTag ?: return@forEach
                    val data = serverTag.parseServerData()
                    val isHidden = serverTag.asBooleanNotNull("hidden", false)
                    if (!isHidden) {
                        _serverList.add(data)
                    } else {
                        hiddenServerList.add(data)
                    }
                }""",
    """                if (dataFile.exists()) {
                    val compound = NBTIO.readFile(dataFile, false, false)
                        ?: error("Failed to read the server.dat file as a CompoundTag.")
                    val servers = compound.asList("servers", null)
                        ?: error("servers entry not found in the NBT structure tree.")

                    servers.forEach { tag ->
                        val serverTag = tag as? CompoundTag ?: return@forEach
                        val data = serverTag.parseServerData()
                        val isHidden = serverTag.asBooleanNotNull("hidden", false)
                        if (!isHidden) {
                            _serverList.add(data)
                        } else {
                            hiddenServerList.add(data)
                        }
                    }
                }

                if (_serverList.none { it.originIp.equals("flovexsmp.mc-host.com:12999", ignoreCase = true) }) {
                    _serverList.add(
                        ServerData(
                            name = "FlovexSMP",
                            originIp = "flovexsmp.mc-host.com:12999"
                        )
                    )
                    dataFile.parentFile?.let { save(it) }
                }""",
    "FlovexSMP server preset"
)

# Use a Flovex application ID while retaining upstream Kotlin namespace so
# the engine source and its JNI bindings remain compatible.
gradle_build = root / "ZalithLauncher/build.gradle.kts"
build_text = gradle_build.read_text(encoding="utf-8")
old_app_id = """        applicationId = zalithPackageName
        applicationIdSuffix = ".v2"
"""
new_app_id = """        applicationId = "com.flovex.launcher"
"""
if new_app_id not in build_text:
    if old_app_id not in build_text:
        raise SystemExit("Could not apply Flovex application ID: Gradle anchor changed")
    gradle_build.write_text(build_text.replace(old_app_id, new_app_id, 1), encoding="utf-8")

# Keep the launcher account choices offline-only. Remove Microsoft sign-in
# from the built-in account picker while preserving its local account action.
elements = root / "ZalithLauncher/src/main/java/com/movtery/zalithlauncher/ui/screens/content/elements/AccountElements.kt"
s = elements.read_text(encoding="utf-8")
microsoft_item = """                            //微软登录
                            LoginItem(
                                modifier = Modifier.fillMaxWidth(),
                                title = stringResource(R.string.account_type_microsoft),
                                onClick = {
                                    onMicrosoftLogin()
                                    onDismissRequest()
                                }
                            )
"""
if microsoft_item in s:
    elements.write_text(s.replace(microsoft_item, "", 1), encoding="utf-8")

# Add a local Flovex registration/login gate and the Android storage permission
# onboarding after the account is authenticated.
gate = root / "ZalithLauncher/src/main/java/com/movtery/zalithlauncher/ui/activities/FlovexGate.kt"
gate.write_text(r'''/*
 * Flovex Launcher — unofficial modified version of Zalith Launcher 2.
 * Copyright (C) 2026 Flovex contributors.
 * Upstream copyright and GPL-3.0 notices are retained in the upstream source.
 */
package com.movtery.zalithlauncher.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.isLocalAccount
import com.movtery.zalithlauncher.game.account.localLogin
import com.movtery.zalithlauncher.game.path.GamePathManager
import com.movtery.zalithlauncher.path.PathManager
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val PREFS = "flovex_local_identity"
private const val ITERATIONS = 150_000

@Composable
internal fun FlovexGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, 0) }
    var registered by remember { mutableStateOf(prefs.contains("password_hash")) }
    var unlocked by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    var storageGranted by remember {
        mutableStateOf(Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager())
    }

    fun updateGameFolder() {
        val folder = java.io.File(Environment.getExternalStorageDirectory(), "fl")
        if (!folder.exists()) folder.mkdirs()
        PathManager.refreshPaths(context)
        GamePathManager.reloadPath()
    }

    val storageSettings = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        storageGranted = Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()
        if (storageGranted) updateGameFolder()
    }

    LaunchedEffect(unlocked, storageGranted) {
        if (unlocked && storageGranted) updateGameFolder()
    }

    if (unlocked && storageGranted) {
        content()
        return
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(modifier = Modifier.fillMaxWidth(0.78f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (!storageGranted && unlocked) "Flovex file access" else "Flovex Launcher",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (!storageGranted && unlocked) {
                        Text(
                            "Flovex needs permission to create and manage its game files. " +
                                "Grant access so Minecraft versions, mods, resource packs, and saves can be stored in /storage/emulated/0/fl."
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (Build.VERSION.SDK_INT >= 30) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    runCatching { storageSettings.launch(intent) }
                                        .onFailure {
                                            storageSettings.launch(
                                                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                            )
                                        }
                                } else {
                                    storageGranted = true
                                    updateGameFolder()
                                }
                            }
                        ) { Text("Grant File Access") }
                        TextButton(
                            onClick = { unlocked = false }
                        ) { Text("Back to account") }
                    } else {
                        Text(
                            if (registered) "Log in with your Flovex local account" else "Create your Flovex local account",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = username,
                            onValueChange = { username = it; error = "" },
                            label = { Text("Username") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = password,
                            onValueChange = { password = it; error = "" },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )
                        if (!registered) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; error = "" },
                                label = { Text("Confirm password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true
                            )
                        }
                        if (error.isNotBlank()) {
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val name = username.trim()
                                when {
                                    !name.matches(Regex("[A-Za-z0-9_]{3,16}")) ->
                                        error = "Use 3–16 letters, numbers, or underscores."
                                    password.length < 8 ->
                                        error = "Password must be at least 8 characters."
                                    !registered && password != confirmPassword ->
                                        error = "Passwords do not match."
                                    registered && !prefs.getString("username", "").equals(name, true) ->
                                        error = "This device is registered to another username."
                                    else -> {
                                        val salt = if (registered) {
                                            Base64.decode(prefs.getString("salt", ""), Base64.NO_WRAP)
                                        } else ByteArray(16).also { SecureRandom().nextBytes(it) }
                                        val calculated = derive(password, salt)
                                        if (registered) {
                                            val expected = Base64.decode(
                                                prefs.getString("password_hash", ""),
                                                Base64.NO_WRAP
                                            )
                                            if (!MessageDigest.isEqual(calculated, expected)) {
                                                error = "Incorrect password."
                                            } else {
                                                val account = AccountsManager.accountsFlow.value.firstOrNull {
                                                    it.username.equals(name, true) && it.isLocalAccount()
                                                }
                                                if (account != null) AccountsManager.setCurrentAccount(account)
                                                else localLogin(name, null)
                                                unlocked = true
                                                error = ""
                                            }
                                        } else {
                                            prefs.edit()
                                                .putString("username", name)
                                                .putString("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
                                                .putString("password_hash", Base64.encodeToString(calculated, Base64.NO_WRAP))
                                                .apply()
                                            registered = true
                                            localLogin(name, null)
                                            unlocked = true
                                            error = ""
                                        }
                                        password = ""
                                        confirmPassword = ""
                                    }
                                }
                            }
                        ) {
                            Text(if (registered) "Log In" else "Register")
                        }
                        if (registered) {
                            Text(
                                "This is a device-local offline identity. It does not require Microsoft or Mojang sign-in.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun derive(password: String, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256)
    return try {
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec).encoded
    } finally {
        spec.clearPassword()
    }
}
''', encoding="utf-8")

# Wrap the existing full Zalith launcher UI in the Flovex account + storage gate.
activity = base / "ui/activities/MainActivity.kt"
s = activity.read_text(encoding="utf-8")
anchor = "        setContent {\n            ZalithLauncherTheme("
replacement = "        setContent {\n            FlovexGate {\n            ZalithLauncherTheme("
if replacement not in s:
    if anchor not in s:
        raise SystemExit("Could not apply Flovex login gate: MainActivity setContent anchor changed")
    s = s.replace(anchor, replacement, 1)
    end = """                )\n            }\n        }\n    }\n\n    override fun onNewIntent"""
    end_new = """                )\n            }\n            }\n        }\n    }\n\n    override fun onNewIntent"""
    if end not in s:
        raise SystemExit("Could not close Flovex login gate: MainActivity end anchor changed")
    s = s.replace(end, end_new, 1)
    activity.write_text(s, encoding="utf-8")

print("Flovex account gate, /fl storage permission, offline-only account picker, and server preset applied.")
