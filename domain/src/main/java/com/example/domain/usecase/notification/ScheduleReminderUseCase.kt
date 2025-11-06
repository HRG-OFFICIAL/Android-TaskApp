package com.example.domain.usecase.notification

import com.example.domain.repository.NotificationRepository
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(taskId: String, reminderTime: Long) {
        notificationRepository.scheduleReminder(taskId, reminderTime)
    }
}
