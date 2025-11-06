package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.home.HomeScreen
import com.example.ui.edit.AddEditScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.app.AppViewModel

import com.example.ui.auth.SignInScreen
import com.example.ui.stats.StatsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.DarkModeController
import com.example.ui.theme.LocalDarkModeController
import com.example.ui.settings.LocalUiSettingsController
import com.example.ui.settings.UiSettingsController

object Routes {
    const val HOME = "home"
    const val STATS = "stats"
    const val PROFILE = "profile"
}

data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var isDarkMode by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()
    val items = listOf(
        BottomItem(Routes.HOME, "Home", Icons.Filled.Home),
        BottomItem(Routes.STATS, "Stats", Icons.Filled.BarChart),
        BottomItem(Routes.PROFILE, "Profile", Icons.Filled.Person),
    )

    val appViewModel: AppViewModel = hiltViewModel()
    val userState by appViewModel.user.collectAsStateWithLifecycle(initialValue = null)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // No bottom navigation; use top bars inside individual screens
    val start = if (userState == null) "signin" else Routes.HOME
    // App-wide settings (simple in-memory controller)
    var showProgressHeader by rememberSaveable { mutableStateOf(true) }
    var showReminderBadges by rememberSaveable { mutableStateOf(true) }
    var showSubTasks by rememberSaveable { mutableStateOf(false) }
    var enableGestures by rememberSaveable { mutableStateOf(true) }
    var showVoiceIcon by rememberSaveable { mutableStateOf(false) }
    var fontScale by rememberSaveable { mutableStateOf(1.0f) }
    var language by rememberSaveable { mutableStateOf("English") }

    CompositionLocalProvider(
        LocalDarkModeController provides DarkModeController(
            isDark = isDarkMode,
            toggle = { isDarkMode = it }
        ),
        LocalUiSettingsController provides UiSettingsController(
            showProgressHeader = showProgressHeader,
            setShowProgressHeader = { showProgressHeader = it },
            showReminderBadges = showReminderBadges,
            setShowReminderBadges = { showReminderBadges = it },
            showSubTasks = showSubTasks,
            setShowSubTasks = { showSubTasks = it },
            enableGestures = enableGestures,
            setEnableGestures = { enableGestures = it },
            showVoiceIcon = showVoiceIcon,
            setShowVoiceIcon = { showVoiceIcon = it },
            fontScale = fontScale,
            setFontScale = { fontScale = it },
            language = language,
            setLanguage = { language = it }
        )
    ) {
        AppTheme(darkTheme = isDarkMode) {
            NavHost(
                navController = navController,
                startDestination = start,
                modifier = Modifier
            ) {
            composable("signin") { 
                SignInScreen(
                    onNavigateToHome = { 
                        navController.navigate(Routes.HOME) {
                            popUpTo("signin") { inclusive = true }
                        }
                    }
                ) 
            }
            composable(Routes.HOME) { HomeScreen(navController = navController) }
            composable(Routes.STATS) { StatsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Routes.PROFILE) { ProfileScreen() }
            composable("edit") { 
                AddEditScreen(
                    onSaved = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                ) 
            }
            composable("edit/{id}") { 
                AddEditScreen(
                    onSaved = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                ) 
            }
            composable("notifications") { NotificationsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("settings") { SettingsScreen(onNavigateBack = { navController.popBackStack() }) }
            }
        }
    }
}
