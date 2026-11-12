package com.flovex.launcher

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformStorageGate(content: @Composable () -> Unit)
