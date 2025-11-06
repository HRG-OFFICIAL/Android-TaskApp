package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.converter.StringListConverter

@Entity(tableName = "tasks")
@TypeConverters(StringListConverter::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String? = null, // Firestore document ID
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: Int = 1,
    val dueAtEpochMillis: Long? = null,
    val reminderAtEpochMillis: Long? = null,
    val projectId: String? = null,
    val labelIds: List<String> = emptyList(),
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val estimatedDurationMinutes: Int? = null,
    val actualDurationMinutes: Int? = null,
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val createdBy: String = "",
    val assignedTo: List<String> = emptyList(),
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val completedAtEpochMillis: Long? = null,
    val version: Int = 1,
    
    // New fields for enhanced UI
    val categoryId: Int = 0,
    val isImportant: Boolean = false,
    val progress: Float = 0f,
    val colorIndex: Int = 0,
    val notes: String = ""
)
