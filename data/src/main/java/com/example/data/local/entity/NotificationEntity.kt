package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.converter.StringMapConverter

@Entity(tableName = "notifications")
@TypeConverters(StringMapConverter::class)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val message: String,
    val taskId: String? = null,
    val projectId: String? = null,
    val userId: String,
    val scheduledAt: Long,
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val actionType: String? = null,
    val actionData: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)
