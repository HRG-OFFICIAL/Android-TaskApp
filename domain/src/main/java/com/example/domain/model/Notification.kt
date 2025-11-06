package com.example.domain.model

import java.util.UUID

data class NotificationData(
    val id: String = UUID.randomUUID().toString(),
    val type: NotificationType,
    val title: String,
    val message: String,
    val taskId: String? = null,
    val projectId: String? = null,
    val userId: String,
    val scheduledAt: Long,
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val actionType: NotificationAction? = null,
    val actionData: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    TASK_DUE, TASK_OVERDUE, TASK_REMINDER, TASK_ASSIGNED, 
    TASK_COMPLETED, PROJECT_SHARED, COLLABORATION_INVITE,
    SMART_SUGGESTION, PRODUCTIVITY_INSIGHT, SYSTEM_UPDATE
}

enum class NotificationAction {
    VIEW_TASK, COMPLETE_TASK, SNOOZE, DISMISS, 
    ACCEPT_SUGGESTION, VIEW_PROJECT, RESPOND_TO_INVITE
}

data class ReminderSettings(
    val taskId: String,
    val reminderTimes: List<Long>, // minutes before due
    val isEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val locationEnabled: Boolean = false,
    val locationRadius: Int = 100 // meters
)
