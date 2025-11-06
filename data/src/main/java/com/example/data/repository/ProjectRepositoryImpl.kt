package com.example.data.repository

import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.ProjectEntity
import com.example.domain.model.Project
import com.example.domain.model.ProjectStats
import com.example.domain.model.SyncStatus
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectRepository {

    private fun ProjectEntity.toDomain(): Project {
        return Project(
            id = id,
            name = name,
            description = description,
            color = color,
            icon = icon,
            isArchived = isArchived,
            isShared = isShared,
            ownerId = ownerId,
            collaborators = collaborators,
            createdAtEpochMillis = createdAtEpochMillis,
            updatedAtEpochMillis = updatedAtEpochMillis,
            version = version,
            syncStatus = SyncStatus.valueOf(syncStatus)
        )
    }

    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            name = name,
            description = description,
            color = color,
            icon = icon,
            isArchived = isArchived,
            isShared = isShared,
            ownerId = ownerId,
            collaborators = collaborators,
            createdAtEpochMillis = createdAtEpochMillis,
            updatedAtEpochMillis = updatedAtEpochMillis,
            version = version,
            syncStatus = syncStatus.name
        )
    }

    override fun observeAllProjects(): Flow<List<Project>> {
        return projectDao.observeAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeProject(id: String): Flow<Project?> {
        return projectDao.observeProject(id).map { it?.toDomain() }
    }

    override fun observeProjectStats(projectId: String): Flow<ProjectStats> {
        // This would typically involve complex queries to calculate stats
        // For now, return empty stats - would be implemented with proper statistics calculation
        return kotlinx.coroutines.flow.flowOf(ProjectStats())
    }

    override suspend fun createProject(project: Project): String {
        val entity = project.toEntity()
        projectDao.upsert(entity)
        return entity.id
    }

    override suspend fun updateProject(project: Project) {
        projectDao.update(project.toEntity())
    }

    override suspend fun deleteProject(id: String) {
        projectDao.delete(id)
    }

    override suspend fun archiveProject(id: String) {
        projectDao.archive(id)
    }

    override suspend fun shareProject(projectId: String, userIds: List<String>) {
        // Implementation would update collaborators list
        // This is a simplified version
    }

    override suspend fun unshareProject(projectId: String, userId: String) {
        // Implementation would remove user from collaborators
        // This is a simplified version
    }

    override suspend fun getProjectCollaborators(projectId: String): List<String> {
        // Implementation would return collaborators for the project
        return emptyList()
    }
}
