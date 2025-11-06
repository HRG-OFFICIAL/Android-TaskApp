package com.example.domain.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val isPremium: Boolean = false,
    val subscriptionType: SubscriptionType = SubscriptionType.FREE,
    val subscriptionExpiresAt: Long? = null,
    val preferences: UserPreferences = UserPreferences(),
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val lastActiveAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

enum class SubscriptionType {
    FREE, PREMIUM, ENTERPRISE
}

data class UserPreferences(
    val theme: Theme = Theme.SYSTEM,
    val language: String = "en",
    val timezone: String = "UTC",
    val dateFormat: String = "MM/dd/yyyy",
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    val defaultReminderTime: Int = 15, // minutes before due
    val enableNotifications: Boolean = true,
    val enableBiometricLock: Boolean = false,
    val enableVoiceInput: Boolean = true,
    val enableSmartSuggestions: Boolean = true,
    val enableAnalytics: Boolean = true,
    val enableCrashReporting: Boolean = true,
    val enableLocationReminders: Boolean = false,
    val enableCollaboration: Boolean = true,
    val enableOfflineMode: Boolean = true,
    val autoSync: Boolean = true,
    val syncFrequency: SyncFrequency = SyncFrequency.REAL_TIME
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class TimeFormat {
    TWELVE_HOUR, TWENTY_FOUR_HOUR
}

enum class SyncFrequency {
    REAL_TIME, HOURLY, DAILY, MANUAL
}
