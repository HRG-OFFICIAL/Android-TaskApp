package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.converter.StringListConverter

@Entity(tableName = "smart_suggestions")
@TypeConverters(StringListConverter::class)
data class SmartSuggestionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val description: String,
    val confidence: Float,
    val taskId: String? = null,
    val projectId: String? = null,
    val labelId: String? = null,
    val dueDate: Long? = null,
    val priority: String? = null,
    val generatedAt: Long = System.currentTimeMillis(),
    val isAccepted: Boolean = false,
    val isDismissed: Boolean = false
)
