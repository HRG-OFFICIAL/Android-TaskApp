package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY name ASC")
    fun observeAllLabels(): Flow<List<LabelEntity>>

    @Query("SELECT * FROM labels WHERE id = :id")
    fun observeLabel(id: String): Flow<LabelEntity?>

    @Query("SELECT * FROM labels WHERE createdBy = :userId ORDER BY name ASC")
    fun observeLabelsByUser(userId: String): Flow<List<LabelEntity>>

    @Query("SELECT * FROM labels WHERE isSystem = 1 ORDER BY name ASC")
    fun observeSystemLabels(): Flow<List<LabelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(label: LabelEntity)

    @Update
    suspend fun update(label: LabelEntity)

    @Query("DELETE FROM labels WHERE id = :id AND isSystem = 0")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM labels WHERE createdBy = :userId")
    suspend fun getLabelCount(userId: String): Int
}
