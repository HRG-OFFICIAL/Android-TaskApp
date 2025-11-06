package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class LabelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: String = "#FF5722",
    val icon: String = "label",
    val isSystem: Boolean = false,
    val createdBy: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: String = "PENDING"
)
