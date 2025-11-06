package com.example.domain.repository

import com.example.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeTask(id: String): Flow<Task?>
    suspend fun upsert(task: Task): Int
    suspend fun setDone(id: String, done: Boolean)
    suspend fun deleteById(id: String)
    suspend fun clearAll()
}
