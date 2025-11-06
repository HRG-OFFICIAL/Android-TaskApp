package com.example.data.remote.dto

data class TaskDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false,
    val priority: Int = 0,
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
    val createdAtEpochMillis: Long = 0L,
    val updatedAtEpochMillis: Long = 0L,
    val completedAtEpochMillis: Long? = null,
    val version: Int = 1
)
