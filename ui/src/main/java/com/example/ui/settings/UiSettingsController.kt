package com.example.ui.settings

import androidx.compose.runtime.staticCompositionLocalOf

data class UiSettingsController(
    val showProgressHeader: Boolean,
    val setShowProgressHeader: (Boolean) -> Unit,

    val showReminderBadges: Boolean,
    val setShowReminderBadges: (Boolean) -> Unit,

    val showSubTasks: Boolean,
    val setShowSubTasks: (Boolean) -> Unit,

    val enableGestures: Boolean,
    val setEnableGestures: (Boolean) -> Unit,

    val showVoiceIcon: Boolean,
    val setShowVoiceIcon: (Boolean) -> Unit,

    val fontScale: Float,
    val setFontScale: (Float) -> Unit,

    val language: String,
    val setLanguage: (String) -> Unit,
)

val LocalUiSettingsController = staticCompositionLocalOf<UiSettingsController> {
    UiSettingsController(
        showProgressHeader = true,
        setShowProgressHeader = {},
        showReminderBadges = true,
        setShowReminderBadges = {},
        showSubTasks = false,
        setShowSubTasks = {},
        enableGestures = true,
        setEnableGestures = {},
        showVoiceIcon = false,
        setShowVoiceIcon = {},
        fontScale = 1.0f,
        setFontScale = {},
        language = "English",
        setLanguage = {}
    )
}