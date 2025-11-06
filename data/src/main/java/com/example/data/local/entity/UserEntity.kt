package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.converter.StringListConverter
import com.example.data.local.converter.UserPreferencesConverter

@Entity(tableName = "users")
@TypeConverters(StringListConverter::class, UserPreferencesConverter::class)
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val isPremium: Boolean = false,
    val subscriptionType: String = "FREE",
    val subscriptionExpiresAt: Long? = null,
    val preferences: String = "", // JSON string of UserPreferences
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val lastActiveAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: String = "PENDING"
)
