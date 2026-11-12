package com.flovex.launcher

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformStorageGate(content: @Composable () -> Unit) {
    content()
}
