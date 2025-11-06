package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.mapper.TaskMapper
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> {
        return taskDao.observeAll().map { entities ->
            entities.map { taskMapper.toDomain(it) }
        }
    }

    override fun observeTask(id: String): Flow<Task?> {
        val intId = id.toIntOrNull() ?: return kotlinx.coroutines.flow.flowOf(null)
        return taskDao.observeById(intId).map { entity ->
            entity?.let { taskMapper.toDomain(it) }
        }
    }

    override suspend fun upsert(task: Task): Int {
        val entity = taskMapper.toEntity(task.copy(updatedAtEpochMillis = System.currentTimeMillis()))
        return taskDao.upsert(entity).toInt()
    }

    override suspend fun setDone(id: String, done: Boolean) {
        val intId = id.toIntOrNull() ?: return
        taskDao.setDone(intId, done, System.currentTimeMillis())
    }

    override suspend fun deleteById(id: String) {
        val intId = id.toIntOrNull() ?: return
        taskDao.deleteById(intId)
    }

    override suspend fun clearAll() {
        taskDao.deleteAll()
    }
}