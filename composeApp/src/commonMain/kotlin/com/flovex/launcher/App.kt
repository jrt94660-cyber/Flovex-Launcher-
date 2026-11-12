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
    var selectedPage by remember { mutableStateOf("Home") }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Background,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Row(modifier = Modifier.fillMaxSize().background(Background)) {
            Column(
                modifier = Modifier.width(190.dp).fillMaxSize()
                    .background(Color(0xFF0E141E)).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("FLOVEX", color = Accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("LAUNCHER", color = Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(24.dp))
                listOf("Home", "Server", "Versions", "Mods", "Performance", "Accounts", "Settings").forEach { page ->
                    Button(
                        onClick = { selectedPage = page },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPage == page) Color(0xFF20364B) else Color.Transparent,
                            contentColor = if (selectedPage == page) Accent else Muted
                        )
                    ) { Text(page, modifier = Modifier.fillMaxWidth()) }
                }
                Spacer(Modifier.weight(1f))
                Text("Preview build 0.2.0", color = Muted, fontSize = 10.sp)
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(30.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(selectedPage, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                when (selectedPage) {
                    "Home" -> HomePage()
                    "Server" -> ServerPage()
                    "Versions" -> PlaceholderPage("Install and manage Minecraft versions.")
                    "Mods" -> PlaceholderPage("Browse and manage compatible mods and modpacks.")
                    "Performance" -> PlaceholderPage("Create reversible FPS optimization profiles.")
                    "Accounts" -> PlaceholderPage("Add Microsoft accounts or local offline profiles.")
                    else -> PlaceholderPage("Launcher preferences and game settings.")
                }
            }
        }
    }
}

@Composable
private fun HomePage() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("YOUR MINECRAFT, YOUR WAY", color = Accent, fontSize = 11.sp, letterSpacing = 2.sp)
            Text("Ready to play?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                "Flovex Launcher brings accounts, game versions, mods, and performance profiles into one app.",
                color = Muted
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}, enabled = false) { Text("PLAY — COMING SOON") }
                Spacer(Modifier.width(12.dp))
                Text("Game installation is not available yet", color = Muted, fontSize = 12.sp)
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
        while (true) {
            refresh()
            delay(30_000)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("FLOVEXSMP", color = Accent, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val statusColor = when (isOnline) {
                    true -> Color(0xFF4ADE80)
                    false -> Color(0xFFFF6B6B)
                    null -> Color(0xFFFFC66D)
                }
                Box(Modifier.width(9.dp).height(9.dp).background(statusColor, RoundedCornerShape(50)))
                Text(
                    when (isOnline) { true -> "ONLINE"; false -> "OFFLINE"; null -> if (checking) "CHECKING" else "UNKNOWN" },
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Text("$SERVER_HOST:$SERVER_PORT", fontWeight = FontWeight.SemiBold)
            Text(details, color = Muted, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lastChecked, color = Muted, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                Button(onClick = { refresh() }, enabled = !checking) {
                    Text(if (checking) "Checking…" else "Refresh")
                }
            }
            Text("Automatically checks every 30 seconds while the launcher is open.", color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuickCard(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.width(220.dp),
        color = Panel,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlaceholderPage(description: String) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(description, color = Muted)
            Text("This module is not implemented yet.", color = Color(0xFFFFC66D), fontSize = 12.sp)
        }
    }
}
