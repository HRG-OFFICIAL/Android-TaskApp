package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToggleImportantUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, isImportant: Boolean) {
        val task = repository.observeTask(taskId).firstOrNull() ?: return
        repository.upsert(task.copy(isImportant = isImportant))
    }
}