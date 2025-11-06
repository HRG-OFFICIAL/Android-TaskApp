package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class DarkModeController(
    val isDark: Boolean,
    val toggle: (Boolean) -> Unit
)

val LocalDarkModeController = staticCompositionLocalOf<DarkModeController> {
    DarkModeController(isDark = false) { _ -> }
}