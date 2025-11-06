package com.example.domain.usecase.project

import com.example.domain.model.Project
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProjectsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> {
        return projectRepository.observeAllProjects()
    }
}
