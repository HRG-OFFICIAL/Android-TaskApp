package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.SmartSuggestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartSuggestionDao {
    @Query("SELECT * FROM smart_suggestions WHERE isDismissed = 0 ORDER BY confidence DESC, generatedAt DESC")
    fun observeAllSuggestions(): Flow<List<SmartSuggestionEntity>>

    @Query("SELECT * FROM smart_suggestions WHERE id = :id")
    fun observeSuggestion(id: String): Flow<SmartSuggestionEntity?>

    @Query("SELECT * FROM smart_suggestions WHERE taskId = :taskId AND isDismissed = 0")
    fun observeSuggestionsForTask(taskId: String): Flow<List<SmartSuggestionEntity>>

    @Query("SELECT * FROM smart_suggestions WHERE type = :type AND isDismissed = 0")
    fun observeSuggestionsByType(type: String): Flow<List<SmartSuggestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(suggestion: SmartSuggestionEntity)

    @Update
    suspend fun update(suggestion: SmartSuggestionEntity)

    @Query("UPDATE smart_suggestions SET isAccepted = 1 WHERE id = :id")
    suspend fun accept(id: String)

    @Query("UPDATE smart_suggestions SET isDismissed = 1 WHERE id = :id")
    suspend fun dismiss(id: String)

    @Query("DELETE FROM smart_suggestions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM smart_suggestions WHERE isDismissed = 1 AND generatedAt < :cutoffTime")
    suspend fun deleteOldDismissedSuggestions(cutoffTime: Long)

    @Query("SELECT COUNT(*) FROM smart_suggestions WHERE isDismissed = 0")
    suspend fun getActiveSuggestionCount(): Int
}
