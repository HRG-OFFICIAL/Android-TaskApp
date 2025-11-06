package com.example.data.mapper

import com.example.data.local.entity.TaskEntity
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskMapper @Inject constructor() {

    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id.toString(),
            title = entity.title,
            description = entity.description,
            isDone = entity.isDone,
            priority = TaskPriority.values().find { it.value == entity.priority } ?: TaskPriority.MEDIUM,
            dueAtEpochMillis = entity.dueAtEpochMillis,
            reminderAtEpochMillis = entity.reminderAtEpochMillis,
            projectId = entity.projectId,
            labelIds = entity.labelIds,
            isArchived = entity.isArchived,
            isPinned = entity.isPinned,
            estimatedDurationMinutes = entity.estimatedDurationMinutes,
            actualDurationMinutes = entity.actualDurationMinutes,
            location = entity.location,
            tags = entity.tags,
            createdBy = entity.createdBy,
            assignedTo = entity.assignedTo.firstOrNull(),
            createdAtEpochMillis = entity.createdAtEpochMillis,
            updatedAtEpochMillis = entity.updatedAtEpochMillis,
            completedAtEpochMillis = entity.completedAtEpochMillis,
            version = entity.version,
            // New fields
            categoryId = entity.categoryId,
            isImportant = entity.isImportant,
            progress = entity.progress,
            colorIndex = entity.colorIndex,
            notes = entity.notes
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id.toIntOrNull() ?: 0,
            title = domain.title,
            description = domain.description,
            isDone = domain.isDone,
            priority = domain.priority.value,
            dueAtEpochMillis = domain.dueAtEpochMillis,
            reminderAtEpochMillis = domain.reminderAtEpochMillis,
            projectId = domain.projectId,
            labelIds = domain.labelIds,
            isArchived = domain.isArchived,
            isPinned = domain.isPinned,
            estimatedDurationMinutes = domain.estimatedDurationMinutes,
            actualDurationMinutes = domain.actualDurationMinutes,
            location = domain.location,
            tags = domain.tags,
            createdBy = domain.createdBy,
            assignedTo = listOfNotNull(domain.assignedTo),
            createdAtEpochMillis = domain.createdAtEpochMillis,
            updatedAtEpochMillis = domain.updatedAtEpochMillis,
            completedAtEpochMillis = domain.completedAtEpochMillis,
            version = domain.version,
            // New fields
            categoryId = domain.categoryId,
            isImportant = domain.isImportant,
            progress = domain.progress,
            colorIndex = domain.colorIndex,
            notes = domain.notes
        )
    }
}