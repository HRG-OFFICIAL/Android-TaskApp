package com.example.ui.settings

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalDarkModeController
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit = {}) {
    val darkController = LocalDarkModeController.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance
            SectionTitle("Appearance")
            Spacer(Modifier.height(8.dp))
            SettingsSwitchRow(
                title = "Dark Mode",
                checked = darkController.isDark,
                onCheckedChange = { darkController.toggle(it) }
            )
            Spacer(Modifier.height(8.dp))
            val ui = com.example.ui.settings.LocalUiSettingsController.current
            val currentSize = when (ui.fontScale) {
                0.9f -> "Small"
                1.0f -> "Medium"
                1.1f -> "Large"
                else -> "Medium"
            }
            SettingsChoiceRow(
                title = "Font Size",
                options = listOf("Small", "Medium", "Large"),
                selected = currentSize,
                onSelect = {
                    when (it) {
                        "Small" -> ui.setFontScale(0.9f)
                        "Medium" -> ui.setFontScale(1.0f)
                        "Large" -> ui.setFontScale(1.1f)
                    }
                }
            )
            Divider(Modifier.padding(vertical = 16.dp))

            // Task display
            SectionTitle("Task Display")
            Spacer(Modifier.height(8.dp))
            SettingsSwitchRow("Show Progress Header", ui.showProgressHeader) { ui.setShowProgressHeader(it) }
            SettingsSwitchRow("Show Reminder Badges", ui.showReminderBadges) { ui.setShowReminderBadges(it) }
            SettingsSwitchRow("Show Sub-Tasks", ui.showSubTasks) { ui.setShowSubTasks(it) }
            Divider(Modifier.padding(vertical = 16.dp))

            // Gestures
            SectionTitle("Gestures")
            Spacer(Modifier.height(8.dp))
            SettingsSwitchRow("Enable Swipe Actions", ui.enableGestures) { ui.setEnableGestures(it) }
            SettingsSwitchRow("Show Voice Input Icon", ui.showVoiceIcon) { ui.setShowVoiceIcon(it) }
            Divider(Modifier.padding(vertical = 16.dp))

            // Security
            SectionTitle("Security")
            Spacer(Modifier.height(8.dp))
            // Placeholder; wire to biometric auth later
            SettingsSwitchRow("Fingerprint Lock", false) { }
            Divider(Modifier.padding(vertical = 16.dp))

            // Language
            SectionTitle("Language")
            Spacer(Modifier.height(8.dp))
            SettingsChoiceRow(
                title = "App Language",
                options = listOf("English", "Hindi", "Spanish"),
                selected = ui.language,
                onSelect = { ui.setLanguage(it) }
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Some settings are placeholders and will be wired later.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsChoiceRow(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { opt ->
                FilterChip(
                    selected = selected == opt,
                    onClick = { onSelect(opt) },
                    label = { Text(opt) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}