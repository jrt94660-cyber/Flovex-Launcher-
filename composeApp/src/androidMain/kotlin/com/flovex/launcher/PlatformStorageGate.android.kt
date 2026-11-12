package com.flovex.launcher

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.documentfile.provider.DocumentFile

private const val STORAGE_PREFS = "flovex_storage"
private const val STORAGE_TREE = "game_tree_uri"

@Composable
actual fun PlatformStorageGate(content: @Composable () -> Unit) {
    val context = LauncherAndroidContext.appContext
    var savedUri by remember {
        mutableStateOf(context?.getSharedPreferences(STORAGE_PREFS, 0)?.getString(STORAGE_TREE, null))
    }
    var error by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri == null) {
            error = "Folder selection was cancelled."
        } else {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val folder = DocumentFile.fromTreeUri(context ?: return@rememberLauncherForActivityResult, uri)
            if (folder == null || folder.name != "fl") {
                error = "Select a folder named fl. Use New folder in the Android picker if it does not exist."
            } else {
                try {
                    context?.contentResolver?.takePersistableUriPermission(uri, flags)
                    context?.getSharedPreferences(STORAGE_PREFS, 0)?.edit()?.putString(STORAGE_TREE, uri.toString())?.apply()
                    savedUri = uri.toString()
                    error = null
                } catch (e: Exception) {
                    error = e.message ?: "Android could not save folder access."
                }
            }
        }
    }

    if (!savedUri.isNullOrBlank()) {
        content()
    } else {
        MaterialTheme {
            Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0F16)) {
                Column(
                    Modifier.fillMaxSize().padding(28.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FLOVEX", color = Color(0xFF45B6F7), fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("LAUNCHER SETUP", color = Color(0xFF9AA8B8), fontSize = 11.sp, letterSpacing = 3.sp)
                    Spacer(Modifier.height(34.dp))
                    Text("The launcher needs file access", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Choose or create a folder named fl. Flovex will use it for Minecraft files, versions, mods, resource packs, and profiles. Android will ask you to approve access.", color = Color(0xFF9AA8B8))
                    Spacer(Modifier.height(22.dp))
                    Button(onClick = { picker.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Grant File Access")
                    }
                    if (error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(error!!, color = Color(0xFFFF8A80))
                    }
                }
            }
        }
    }
}
