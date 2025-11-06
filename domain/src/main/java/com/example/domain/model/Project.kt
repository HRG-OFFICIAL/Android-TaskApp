package com.example.domain.model

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val color: String = "#2196F3", // Material color
    val icon: String = "folder", // Material icon name
    val isArchived: Boolean = false,
    val isShared: Boolean = false,
    val ownerId: String, // User ID
    val collaborators: List<String> = emptyList(), // User IDs
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class ProjectStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Float = 0f,
    val averageCompletionTime: Long = 0L // in minutes
)
