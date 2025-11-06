package com.example.domain.repository

import com.example.domain.model.NotificationData
import com.example.domain.model.ReminderSettings
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(): Flow<List<NotificationData>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun createNotification(notification: NotificationData): String
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: String)
    suspend fun scheduleReminder(taskId: String, reminderTime: Long)
    suspend fun cancelReminder(taskId: String)
    suspend fun updateReminderSettings(settings: ReminderSettings)
    suspend fun getReminderSettings(taskId: String): ReminderSettings?
    suspend fun scheduleLocationReminder(taskId: String, location: String, radius: Int)
    suspend fun cancelLocationReminder(taskId: String)
}
