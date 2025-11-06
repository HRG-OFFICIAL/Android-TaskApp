package com.example.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val biometricManager: BiometricManager
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_lock", Context.MODE_PRIVATE)
    
    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()
    
    private val _isBiometricEnabled = MutableStateFlow(isBiometricLockEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()
    
    fun isBiometricLockEnabled(): Boolean {
        return prefs.getBoolean("biometric_enabled", false)
    }
    
    fun setBiometricLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
        _isBiometricEnabled.value = enabled
    }
    
    fun lockApp() {
        _isLocked.value = true
    }
    
    fun unlockApp() {
        _isLocked.value = false
    }
    
    suspend fun authenticateWithBiometric(activity: FragmentActivity): BiometricResult {
        return biometricManager.authenticate(
            activity = activity,
            title = "Unlock TaskApp",
            subtitle = "Use your biometric to access your tasks"
        )
    }
    
    fun shouldShowBiometricPrompt(): Boolean {
        return isBiometricLockEnabled() && biometricManager.isBiometricAvailable()
    }
}
