package com.example.domain.usecase

import com.example.domain.repository.TaskRepository
import javax.inject.Inject

class SetTaskDoneUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: String, done: Boolean) = repository.setDone(id, done)
}
