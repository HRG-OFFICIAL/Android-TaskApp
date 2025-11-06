package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOperationDao {
    
    @Query("SELECT * FROM sync_operations ORDER BY timestamp ASC")
    suspend fun getAllOperations(): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getOperationsForUser(userId: String): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations WHERE userId = :userId ORDER BY timestamp ASC")
    fun observeOperationsForUser(userId: String): Flow<List<SyncOperationEntity>>
    
    @Query("SELECT * FROM sync_operations WHERE type = :type ORDER BY timestamp ASC")
    suspend fun getOperationsByType(type: String): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations WHERE taskId = :taskId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestOperationForTask(taskId: Int): SyncOperationEntity?
    
    @Query("SELECT * FROM sync_operations WHERE retryCount < :maxRetries ORDER BY timestamp ASC")
    suspend fun getRetryableOperations(maxRetries: Int): List<SyncOperationEntity>
    
    @Query("SELECT COUNT(*) FROM sync_operations WHERE userId = :userId")
    suspend fun getOperationCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM sync_operations WHERE userId = :userId AND type = :type")
    suspend fun getOperationCountByType(userId: String, type: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: SyncOperationEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<SyncOperationEntity>): List<Long>
    
    @Update
    suspend fun updateOperation(operation: SyncOperationEntity)
    
    @Delete
    suspend fun deleteOperation(operation: SyncOperationEntity)
    
    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun deleteOperationById(id: Long)
    
    @Query("DELETE FROM sync_operations WHERE taskId = :taskId")
    suspend fun deleteOperationsForTask(taskId: Int)
    
    @Query("DELETE FROM sync_operations WHERE userId = :userId")
    suspend fun deleteOperationsForUser(userId: String)
    
    @Query("DELETE FROM sync_operations WHERE timestamp < :cutoffTime")
    suspend fun deleteOldOperations(cutoffTime: Long)
    
    @Query("DELETE FROM sync_operations WHERE retryCount >= :maxRetries")
    suspend fun deleteFailedOperations(maxRetries: Int)
    
    @Query("DELETE FROM sync_operations")
    suspend fun deleteAllOperations()
}