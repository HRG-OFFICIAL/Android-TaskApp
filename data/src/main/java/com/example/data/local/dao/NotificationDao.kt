package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY scheduledAt DESC")
    fun observeNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE id = :id")
    fun observeNotification(id: String): Flow<NotificationEntity?>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY scheduledAt DESC")
    fun observeUnreadNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCount(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notification: NotificationEntity)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM notifications WHERE userId = :userId AND isRead = 1 AND scheduledAt < :cutoffTime")
    suspend fun deleteOldReadNotifications(userId: String, cutoffTime: Long)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
