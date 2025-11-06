package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converter.StringListConverter
import com.example.data.local.converter.StringMapConverter
import com.example.data.local.converter.UserPreferencesConverter
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        ProjectEntity::class,
        LabelEntity::class,
        UserEntity::class,
        SmartSuggestionEntity::class,
        NotificationEntity::class,
        SyncOperationEntity::class
    ],
    version = 4,
    exportSchema = true,
)
@TypeConverters(
    StringListConverter::class,
    StringMapConverter::class,
    UserPreferencesConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun labelDao(): LabelDao
    abstract fun userDao(): UserDao
    abstract fun smartSuggestionDao(): SmartSuggestionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncOperationDao(): SyncOperationDao
}
