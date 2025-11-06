package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.dto.TaskDto

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val type: String, // SyncOperationType enum as string
    val taskId: Int,
    val taskData: String, // JSON serialized TaskDto
    val userId: String,
    val timestamp: Long,
    val retryCount: Int = 0,
    val lastAttemptTime: Long? = null,
    val errorMessage: String? = null,
    val isProcessing: Boolean = false
)