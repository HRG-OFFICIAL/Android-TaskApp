package com.example.domain.usecase

import com.example.domain.repository.TaskRepository
import javax.inject.Inject

class ClearAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() = repository.clearAll()
}