package com.example.domain.model

import java.util.UUID

data class Label(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String = "#FF5722", // Material color
    val icon: String = "label", // Material icon name
    val isSystem: Boolean = false, // System labels like "Important", "Urgent"
    val createdBy: String, // User ID
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
