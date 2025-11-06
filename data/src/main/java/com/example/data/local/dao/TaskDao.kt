package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isDone ASC, priority DESC, CASE WHEN dueAtEpochMillis IS NULL THEN 1 ELSE 0 END, dueAtEpochMillis ASC, updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeById(id: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE tasks SET isDone = :done, updatedAtEpochMillis = :updatedAt WHERE id = :id")
    suspend fun setDone(id: Int, done: Boolean, updatedAt: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
