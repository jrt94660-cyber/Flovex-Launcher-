package com.flovex.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background = Color(0xFF0B0F16)
private val Panel = Color(0xFF141B26)
private val Accent = Color(0xFF45B6F7)
private val Muted = Color(0xFF9AA8B8)
private const val SERVER_HOST = "flovexsmp.mc-host.com"
private const val SERVER_PORT = 12999

@Composable
fun App() {
    PlatformStorageGate { LauncherContent() }
}

@Composable
private fun LauncherContent() {
    var selectedPage by remember { mutableStateOf("Home") }
    MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = Background, surface = Panel, onBackground = Color.White, onSurface = Color.White)) {
        Row(Modifier.fillMaxSize().background(Background)) {
            Column(
                Modifier.width(190.dp).fillMaxSize().background(Color(0xFF0E141E)).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("FLOVEX", color = Accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("LAUNCHER", color = Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(24.dp))
                listOf("Home", "Server", "Versions", "Mods", "Resource Packs", "Controls", "Performance", "Accounts", "Settings").forEach { page ->
                    Button(
                        onClick = { selectedPage = page }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPage == page) Color(0xFF20364B) else Color.Transparent,
                            contentColor = if (selectedPage == page) Accent else Muted
                        )
                    ) { Text(page, modifier = Modifier.fillMaxWidth()) }
                }
                Spacer(Modifier.weight(1f))
                Text("Preview build 0.3.0", color = Muted, fontSize = 10.sp)
            }
            Column(Modifier.fillMaxSize().padding(30.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(selectedPage, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                when (selectedPage) {
                    "Home" -> HomePage()
                    "Server" -> ServerPage()
                    "Versions" -> VersionsPage()
                    "Mods" -> AddonManagerPage("mods")
                    "Performance" -> PlaceholderPage("Create reversible FPS optimization profiles.")
                    "Accounts" -> AccountsPage()
                    "Resource Packs" -> AddonManagerPage("resourcepacks")
                    "Controls" -> ControlsPage()
                    else -> PlaceholderPage("Launcher preferences and game settings.")
                }
            }
        }
    }
}

@Composable
private fun HomePage() {
    Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("YOUR MINECRAFT, YOUR WAY", color = Accent, fontSize = 11.sp, letterSpacing = 2.sp)
            Text("Ready to play?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Flovex Launcher brings accounts, game versions, mods, and performance profiles into one app.", color = Muted)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}, enabled = false) { Text("PLAY — COMING SOON") }
                Spacer(Modifier.width(12.dp))
                Text("Game installation and launch are not available yet", color = Muted, fontSize = 12.sp)
            }
        }
    }
    ServerStatusCard()
    Text("Quick access", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickCard("Performance", "FPS profiles and optimization mods")
        QuickCard("Accounts", "Microsoft and local profiles")
    }
}

@Composable
private fun ServerPage() {
    Text("Official FlovexSMP server connection monitor", color = Muted)
    ServerStatusCard()
}

@Composable
private fun ServerStatusCard() {
    var isOnline by remember { mutableStateOf<Boolean?>(null) }
    var details by remember { mutableStateOf("Waiting for first status check…") }
    var checking by remember { mutableStateOf(false) }
    var lastChecked by remember { mutableStateOf("Not checked yet") }
    fun refresh() {
        if (checking) return
        checking = true
        details = "Checking server…"
        checkMinecraftServer(SERVER_HOST, SERVER_PORT) { online, message ->
            isOnline = online
            details = message
            lastChecked = "Last checked just now"
            checking = false
        }
    }
    LaunchedEffect(Unit) {
        while (true) { refresh(); delay(30_000) }
    }
    Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("FLOVEXSMP", color = Accent, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val statusColor = when (isOnline) { true -> Color(0xFF4ADE80); false -> Color(0xFFFF6B6B); null -> Color(0xFFFFC66D) }
                Box(Modifier.width(9.dp).height(9.dp).background(statusColor, RoundedCornerShape(50)))
                Text(when (isOnline) { true -> "ONLINE"; false -> "OFFLINE"; null -> if (checking) "CHECKING" else "UNKNOWN" }, color = statusColor, fontWeight = FontWeight.Bold)
            }
            Text("$SERVER_HOST:$SERVER_PORT", fontWeight = FontWeight.SemiBold)
            Text(details, color = Muted, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lastChecked, color = Muted, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                Button(onClick = { refresh() }, enabled = !checking) { Text(if (checking) "Checking…" else "Refresh") }
            }
            Text("Automatically checks every 30 seconds while the launcher is open.", color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun VersionsPage() {
    var versions by remember { mutableStateOf<List<MinecraftVersion>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("release") }
    fun refresh() {
        if (loading) return
        loading = true
        error = null
        fetchMinecraftVersions { result, message ->
            versions = result.orEmpty()
            error = message
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }
    Text("Official version catalogue from Mojang's version manifest.", color = Muted)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { selectedFilter = "release" }) { Text("Releases") }
        Button(onClick = { selectedFilter = "snapshot" }) { Text("Snapshots") }
        Button(onClick = { refresh() }, enabled = !loading) { Text(if (loading) "Loading…" else "Refresh") }
    }
    if (error != null) Text("Could not load versions: $error", color = Color(0xFFFF8A80))
    if (versions.isEmpty() && loading) Text("Connecting to Mojang version manifest…", color = Muted)
    versions.filter { it.type == selectedFilter }.take(30).forEach { version ->
        Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(10.dp)) {
            Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(version.id, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text(if (version.type == "release") "RELEASE" else "SNAPSHOT", color = Muted, fontSize = 10.sp)
            }
        }
    }
    Text("This catalogue is read-only for now. Downloading game files and launching versions are the next implementation step.", color = Color(0xFFFFC66D), fontSize = 12.sp)
}


@Composable
private fun AccountsPage() {
    var profiles by remember { mutableStateOf(loadLocalProfiles()) }
    var profileName by remember { mutableStateOf("") }
    var selectedProfile by remember { mutableStateOf(profiles.firstOrNull().orEmpty()) }
    var validation by remember { mutableStateOf<String?>(null) }

    Text("Local profiles are for offline-mode servers only. They are not Microsoft accounts and do not verify Minecraft ownership.", color = Muted)
    Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("ADD LOCAL PROFILE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            OutlinedTextField(
                value = profileName,
                onValueChange = { profileName = it.take(16); validation = null },
                label = { Text("Minecraft username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                val candidate = profileName.trim()
                if (!candidate.matches(Regex("^[A-Za-z0-9_]{3,16}$"))) {
                    validation = "Use 3–16 letters, numbers, or underscores."
                } else if (profiles.any { it.equals(candidate, ignoreCase = true) }) {
                    validation = "That local profile already exists."
                } else {
                    profiles = profiles + candidate
                    saveLocalProfiles(profiles)
                    selectedProfile = candidate
                    profileName = ""
                    validation = null
                }
            }) { Text("Add profile") }
            if (validation != null) Text(validation!!, color = Color(0xFFFF8A80), fontSize = 12.sp)
        }
    }
    Text("SAVED LOCAL PROFILES", fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Bold)
    if (profiles.isEmpty()) Text("No local profiles saved.", color = Muted)
    profiles.forEach { profile ->
        Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(10.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(profile, fontWeight = FontWeight.SemiBold)
                    Text(if (selectedProfile == profile) "Selected local profile" else "Offline profile", color = Muted, fontSize = 11.sp)
                }
                Button(onClick = { selectedProfile = profile }) { Text("Select") }
                Spacer(Modifier.width(6.dp))
                Button(onClick = {
                    profiles = profiles.filterNot { it == profile }
                    saveLocalProfiles(profiles)
                    if (selectedProfile == profile) selectedProfile = profiles.firstOrNull().orEmpty()
                }) { Text("Remove") }
            }
        }
    }
    Text("Microsoft sign-in and online account verification are not enabled yet. These local profiles cannot authenticate to FlovexSMP if it requires authenticated Minecraft accounts.", color = Color(0xFFFFC66D), fontSize = 12.sp)
}

@Composable
private fun QuickCard(title: String, subtitle: String) {
    Surface(Modifier.width(220.dp), color = Panel, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlaceholderPage(description: String) {
    Box(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(description, color = Muted)
            Text("This module is not implemented yet.", color = Color(0xFFFFC66D), fontSize = 12.sp)
        }
    }
}


@Composable
private fun AddonManagerPage(kind: String) {
    var profile by remember { mutableStateOf(loadLocalProfiles().firstOrNull() ?: "default") }
    var items by remember { mutableStateOf(listGameAddons(profile, kind)) }
    var sourcePath by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    fun reload() { items = listGameAddons(profile, kind) }
    Text(if (kind == "mods") "Manage .jar mods for the selected profile." else "Manage .zip resource packs for the selected profile.", color = Muted)
    OutlinedTextField(value = profile, onValueChange = { profile = it; reload() }, label = { Text("Game profile") }, singleLine = true)
    OutlinedTextField(value = sourcePath, onValueChange = { sourcePath = it }, label = { Text(if (kind == "mods") "Path to mod .jar" else "Path to resource pack .zip") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { message = importGameAddon(profile, kind, sourcePath); if (message == null) sourcePath = ""; reload() }) { Text("Import file") }
        Button(onClick = { reload(); message = "List refreshed." }) { Text("Refresh") }
    }
    if (message != null) Text(message!!, color = Muted, fontSize = 12.sp)
    Text("FILES IN PROFILE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    if (items.isEmpty()) Text("No files found. Import a compatible file to get started.", color = Muted)
    items.forEach { item ->
        Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(10.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.fileName, fontWeight = FontWeight.SemiBold)
                    Text((if (item.enabled) "Enabled" else "Disabled") + " • " + (item.sizeBytes / 1024).coerceAtLeast(1) + " KB", color = Muted, fontSize = 11.sp)
                }
                Button(onClick = { message = if (setGameAddonEnabled(profile, kind, item.fileName, !item.enabled)) null else "Could not change file state."; reload() }) { Text(if (item.enabled) "Disable" else "Enable") }
                Button(onClick = { val file = item.fileName; val ok = deleteGameAddon(profile, kind, file); message = if (ok) "Removed $file" else "Could not remove $file"; reload() }) { Text("Remove") }
            }
        }
    }
    Text("Files are stored in a separate Flovex profile directory. Android currently uses app-specific storage.", color = Muted, fontSize = 11.sp)
}

@Composable
private fun ControlsPage() {
    var profile by remember { mutableStateOf(loadLocalProfiles().firstOrNull() ?: "default") }
    var bindings by remember { mutableStateOf(loadControlBindings(profile)) }
    Text("Edit saved keyboard bindings for this launcher profile.", color = Muted)
    OutlinedTextField(value = profile, onValueChange = { profile = it; bindings = loadControlBindings(it) }, label = { Text("Game profile") }, singleLine = true)
    bindings.forEach { (action, key) ->
        var value by remember(profile, action) { mutableStateOf(key) }
        Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(9.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(action, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                OutlinedTextField(value = value, onValueChange = { value = it.uppercase().take(20) }, label = { Text("Key") }, singleLine = true, modifier = Modifier.width(140.dp))
                Button(onClick = { saveControlBinding(profile, action, value); bindings = loadControlBindings(profile) }) { Text("Save") }
            }
        }
    }
    Button(onClick = { defaultBindings.forEach { (action, key) -> saveControlBinding(profile, action, key) }; bindings = loadControlBindings(profile) }) { Text("Restore defaults") }
    Text("Bindings are saved by Flovex Launcher. Applying them inside Minecraft requires the game integration layer, which is not connected yet.", color = Color(0xFFFFC66D), fontSize = 12.sp)
}
