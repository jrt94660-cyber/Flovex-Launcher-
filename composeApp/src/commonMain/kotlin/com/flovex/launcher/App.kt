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

private val Background = Color(0xFF0B0F16)
private val Panel = Color(0xFF141B26)
private val Accent = Color(0xFF45B6F7)
private val Muted = Color(0xFF9AA8B8)

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
        Row(
            modifier = Modifier.fillMaxSize().background(Background)
        ) {
            Column(
                modifier = Modifier.width(190.dp).fillMaxSize()
                    .background(Color(0xFF0E141E)).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("FLOVEX", color = Accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("LAUNCHER", color = Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(24.dp))
                listOf("Home", "Versions", "Mods", "Performance", "Accounts", "Settings").forEach { page ->
                    Button(
                        onClick = { selectedPage = page },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPage == page) Color(0xFF20364B) else Color.Transparent,
                            contentColor = if (selectedPage == page) Accent else Muted
                        )
                    ) {
                        Text(page, modifier = Modifier.fillMaxWidth())
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("Preview build 0.1.0", color = Muted, fontSize = 10.sp)
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(30.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(selectedPage, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                when (selectedPage) {
                    "Home" -> HomePage()
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
                Button(onClick = {}, enabled = false) {
                    Text("PLAY — COMING SOON")
                }
                Spacer(Modifier.width(12.dp))
                Text("No game installation configured", color = Muted, fontSize = 12.sp)
            }
        }
    }
    Text("Quick access", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickCard("Performance", "FPS profiles and optimization mods")
        QuickCard("Accounts", "Microsoft and local profiles")
    }
}

@Composable
private fun QuickCard(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.weight(1f),
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
