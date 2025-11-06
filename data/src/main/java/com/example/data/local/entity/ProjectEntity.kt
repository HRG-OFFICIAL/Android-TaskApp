package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.converter.StringListConverter

@Entity(tableName = "projects")
@TypeConverters(StringListConverter::class)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val color: String = "#2196F3",
    val icon: String = "folder",
    val isArchived: Boolean = false,
    val isShared: Boolean = false,
    val ownerId: String,
    val collaborators: List<String> = emptyList(),
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: String = "PENDING"
)
