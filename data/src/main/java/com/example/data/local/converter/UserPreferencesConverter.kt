package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.domain.model.UserPreferences
import com.google.gson.Gson

class UserPreferencesConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromUserPreferences(value: UserPreferences): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toUserPreferences(value: String): UserPreferences {
        return gson.fromJson(value, UserPreferences::class.java)
    }
}
