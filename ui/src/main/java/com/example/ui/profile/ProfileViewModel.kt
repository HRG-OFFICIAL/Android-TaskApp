package com.example.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AuthUser
import com.example.domain.model.User
import com.example.domain.model.SubscriptionType
import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val signOut: SignOutUseCase,
) : ViewModel() {
    
    private val _settings = MutableStateFlow<List<SettingItem>>(emptyList())
    private val _userStats = MutableStateFlow(UserStatistics())
    
    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.currentUser,
        _settings.asStateFlow(),
        _userStats.asStateFlow()
    ) { authUser, settings, userStats ->
        ProfileUiState(
            authUser = authUser,
            user = authUser?.let { convertToUser(it) },
            userStats = userStats,
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )
    
    init {
        setupSettings()
        loadUserStats()
    }
    
    private fun convertToUser(authUser: AuthUser): User {
        return User(
            id = authUser.uid,
            email = authUser.email ?: "",
            displayName = authUser.displayName ?: "User",
            photoUrl = authUser.photoUrl,
            isPremium = false, // TODO: Implement premium status check
            subscriptionType = SubscriptionType.FREE // TODO: Implement subscription check
        )
    }
    
    private fun loadUserStats() {
        // TODO: Load actual user statistics from repository
        _userStats.value = UserStatistics(
            tasksCreated = 0,
            tasksCompleted = 0,
            streakDays = 0
        )
    }
    
    private fun setupSettings() {
        val settings = listOf(
            SettingItem(
                title = "Notifications",
                icon = Icons.Filled.Home,
                hasToggle = true,
                isEnabled = true,
                onToggle = { /* Toggle notifications */ }
            ),
            SettingItem(
                title = "Dark Mode",
                icon = Icons.Filled.Person,
                hasToggle = true,
                isEnabled = false,
                onToggle = { /* Toggle dark mode */ }
            ),
            SettingItem(
                title = "Biometric Lock",
                icon = Icons.Filled.CalendarMonth,
                hasToggle = true,
                isEnabled = false,
                onToggle = { /* Toggle biometric lock */ }
            ),
            SettingItem(
                title = "Voice Input",
                icon = Icons.Filled.BarChart,
                hasToggle = true,
                isEnabled = true,
                onToggle = { /* Toggle voice input */ }
            ),
            SettingItem(
                title = "Smart Suggestions",
                icon = Icons.Filled.Home,
                hasToggle = true,
                isEnabled = true,
                onToggle = { /* Toggle smart suggestions */ }
            ),
            SettingItem(
                title = "Sync Settings",
                icon = Icons.Filled.Person,
                hasToggle = false,
                isEnabled = false,
                onToggle = null
            ),
            SettingItem(
                title = "Privacy Settings",
                icon = Icons.Filled.CalendarMonth,
                hasToggle = false,
                isEnabled = false,
                onToggle = null
            )
        )
        
        _settings.value = settings
    }
    
    fun onSettingClick(setting: SettingItem) {
        // Handle setting click
    }
    
    fun onSignOut() {
        viewModelScope.launch {
            try {
                signOut()
            } catch (e: Exception) {
                // TODO: Handle sign out error
            }
        }
    }
    
    fun onDeleteAccount() {
        viewModelScope.launch {
            // Handle account deletion
        }
    }
}

data class ProfileUiState(
    val authUser: AuthUser? = null,
    val user: User? = null,
    val userStats: UserStatistics = UserStatistics(),
    val settings: List<SettingItem> = emptyList()
)

data class UserStatistics(
    val tasksCreated: Int = 0,
    val tasksCompleted: Int = 0,
    val streakDays: Int = 0
)