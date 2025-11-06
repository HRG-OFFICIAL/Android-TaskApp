package com.example.domain.repository

import com.example.domain.model.Project
import com.example.domain.model.ProjectStats
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeAllProjects(): Flow<List<Project>>
    fun observeProject(id: String): Flow<Project?>
    fun observeProjectStats(projectId: String): Flow<ProjectStats>
    suspend fun createProject(project: Project): String
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(id: String)
    suspend fun archiveProject(id: String)
    suspend fun shareProject(projectId: String, userIds: List<String>)
    suspend fun unshareProject(projectId: String, userId: String)
    suspend fun getProjectCollaborators(projectId: String): List<String>
}
