package com.example.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    
    companion object {
        const val CHANNEL_ID_TASK_REMINDER = "task_reminder"
        const val CHANNEL_ID_LOCATION = "location_reminder"
        const val CHANNEL_ID_GENERAL = "general"
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            
            // Task reminder channel
            val taskReminderChannel = NotificationChannel(
                CHANNEL_ID_TASK_REMINDER,
                "Task Reminders",
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task reminders and due dates"
                enableVibration(true)
                enableLights(true)
            }
            
            // Location reminder channel
            val locationChannel = NotificationChannel(
                CHANNEL_ID_LOCATION,
                "Location Reminders",
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when you're near a task location"
                enableVibration(true)
            }
            
            // General channel
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }
            
            systemNotificationManager.createNotificationChannels(
                listOf(taskReminderChannel, locationChannel, generalChannel)
            )
        }
    }
    
    fun showTaskReminder(taskId: String, taskTitle: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASK_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Reminder")
            .setContentText("$taskTitle: $message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(taskId.hashCode(), notification)
    }
    
    fun showLocationReminder(taskId: String, taskTitle: String, location: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_LOCATION)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Location Reminder")
            .setContentText("You're near $location - $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(taskId.hashCode(), notification)
    }
    
    fun showGeneralNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
