package com.example.core.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumFeatureManager @Inject constructor(
    private val billingManager: BillingManager
) {
    
    private val _premiumFeatures = MutableStateFlow<Set<PremiumFeature>>(emptySet())
    val premiumFeatures: StateFlow<Set<PremiumFeature>> = _premiumFeatures.asStateFlow()
    
    fun hasAccessToFeature(feature: PremiumFeature): Boolean {
        return if (feature.isPremium) {
            billingManager.isPremiumUser()
        } else {
            true
        }
    }
    
    fun getAvailableFeatures(): Set<PremiumFeature> {
        return PremiumFeature.values().filter { hasAccessToFeature(it) }.toSet()
    }
    
    fun getPremiumFeatures(): Set<PremiumFeature> {
        return PremiumFeature.values().filter { it.isPremium && billingManager.isPremiumUser() }.toSet()
    }
    
    fun getLockedFeatures(): Set<PremiumFeature> {
        return PremiumFeature.values().filter { it.isPremium && !billingManager.isPremiumUser() }.toSet()
    }
}

enum class PremiumFeature(
    val displayName: String,
    val description: String,
    val isPremium: Boolean
) {
    // Free features
    BASIC_TASKS("Basic Tasks", "Create, edit, and delete tasks", false),
    BASIC_PROJECTS("Basic Projects", "Create up to 3 projects", false),
    BASIC_LABELS("Basic Labels", "Create up to 10 labels", false),
    BASIC_STATISTICS("Basic Statistics", "View basic productivity stats", false),
    BASIC_SYNC("Basic Sync", "Sync across devices", false),
    
    // Premium features
    UNLIMITED_PROJECTS("Unlimited Projects", "Create unlimited projects", true),
    UNLIMITED_LABELS("Unlimited Labels", "Create unlimited labels", true),
    ADVANCED_STATISTICS("Advanced Analytics", "Detailed productivity insights", true),
    AI_SUGGESTIONS("AI Smart Suggestions", "AI-powered task recommendations", true),
    COLLABORATION("Team Collaboration", "Share projects and tasks with others", true),
    CUSTOM_THEMES("Custom Themes", "Personalize your app appearance", true),
    PRIORITY_SUPPORT("Priority Support", "Get help faster with priority support", true),
    CLOUD_BACKUP("Cloud Backup", "Automatic cloud backup and restore", true),
    LOCATION_REMINDERS("Location Reminders", "Get reminded when you're near a location", true),
    VOICE_INPUT("Voice Input", "Create tasks using voice commands", true),
    WIDGETS("Home Screen Widgets", "Quick access from home screen", true),
    BIOMETRIC_LOCK("Biometric Lock", "Secure your app with fingerprint/face", true),
    EXPORT_DATA("Export Data", "Export your tasks to various formats", true),
    API_ACCESS("API Access", "Integrate with third-party apps", true)
}
