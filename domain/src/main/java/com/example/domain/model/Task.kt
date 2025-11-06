package com.example.domain.model

import java.util.UUID

data class Task(
    val id: String = "", // Changed to String for better compatibility
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueAtEpochMillis: Long? = null,
    val reminderAtEpochMillis: Long? = null,
    val projectId: String? = null,
    val labelIds: List<String> = emptyList(),
    val recurringRule: RecurringRule? = null,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val estimatedDurationMinutes: Int? = null,
    val actualDurationMinutes: Int? = null,
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val collaborators: List<String> = emptyList(), // User IDs
    val createdBy: String = "", // User ID - will be set by repository
    val assignedTo: String? = null, // User ID
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val completedAtEpochMillis: Long? = null,
    val version: Int = 1,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    // New fields for enhanced UI
    val categoryId: Int = 0, // Index for task category colors (0-7)
    val isImportant: Boolean = false, // Star/important flag
    val progress: Float = 0f, // Progress percentage (0-100)
    val colorIndex: Int = 0, // Custom color index for task cards
    val subTasks: List<SubTask> = emptyList(), // Sub-tasks for detailed view
    val notes: String = "" // Additional notes field
)

enum class TaskPriority(val value: Int, val displayName: String) {
    LOW(0, "Low"),
    MEDIUM(1, "Medium"), 
    HIGH(2, "High"),
    URGENT(3, "Urgent")
}

enum class SyncStatus {
    PENDING, SYNCING, SYNCED, FAILED
}

data class RecurringRule(
    val frequency: RecurringFrequency,
    val interval: Int = 1, // Every X days/weeks/months
    val daysOfWeek: List<Int> = emptyList(), // 1-7 (Monday-Sunday)
    val dayOfMonth: Int? = null, // 1-31
    val endDate: Long? = null,
    val maxOccurrences: Int? = null
)

enum class RecurringFrequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class Attachment(
    val id: String,
    val name: String,
    val type: AttachmentType,
    val url: String,
    val sizeBytes: Long,
    val uploadedAt: Long
)

enum class AttachmentType {
    IMAGE, DOCUMENT, AUDIO, VIDEO, OTHER
}

data class SubTask(
    val id: String = "",
    val title: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
