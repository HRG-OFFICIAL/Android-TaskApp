package com.example.domain.repository

import com.example.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    fun observeAllLabels(): Flow<List<Label>>
    fun observeLabel(id: String): Flow<Label?>
    suspend fun createLabel(label: Label): String
    suspend fun updateLabel(label: Label)
    suspend fun deleteLabel(id: String)
    suspend fun getLabelsForTask(taskId: String): List<Label>
    suspend fun addLabelToTask(taskId: String, labelId: String)
    suspend fun removeLabelFromTask(taskId: String, labelId: String)
}
