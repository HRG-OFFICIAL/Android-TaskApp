package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY updatedAtEpochMillis DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun observeProject(id: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE ownerId = :ownerId AND isArchived = 0")
    fun observeProjectsByOwner(ownerId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE collaborators LIKE '%' || :userId || '%' AND isArchived = 0")
    fun observeSharedProjects(userId: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE projects SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: String)

    @Query("UPDATE projects SET collaborators = :collaborators WHERE id = :id")
    suspend fun updateCollaborators(id: String, collaborators: List<String>)

    @Query("SELECT COUNT(*) FROM projects WHERE ownerId = :ownerId AND isArchived = 0")
    suspend fun getProjectCount(ownerId: String): Int
}
