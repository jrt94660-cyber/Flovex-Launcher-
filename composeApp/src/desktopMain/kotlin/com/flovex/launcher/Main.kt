package com.flovex.launcher

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Flovex Launcher"
    ) {
        App()
    }
}
