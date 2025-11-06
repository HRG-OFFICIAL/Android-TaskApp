package com.example.domain.repository

import com.example.domain.model.SmartSuggestion
import com.example.domain.model.AIPrediction
import com.example.domain.model.SmartInsight
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    fun observeSmartSuggestions(): Flow<List<SmartSuggestion>>
    fun observeSmartInsights(): Flow<List<SmartInsight>>
    suspend fun generateSuggestions(taskId: String): List<SmartSuggestion>
    suspend fun acceptSuggestion(suggestionId: String)
    suspend fun dismissSuggestion(suggestionId: String)
    suspend fun predictCompletionTime(taskId: String): AIPrediction?
    suspend fun generateInsights(): List<SmartInsight>
    suspend fun analyzeProductivityPatterns(): List<String>
    suspend fun suggestOptimalTaskScheduling(): List<SmartSuggestion>
    suspend fun categorizeTask(title: String, description: String): List<String>
    suspend fun extractKeywords(text: String): List<String>
    suspend fun suggestDueDate(taskTitle: String, description: String): Long?
    suspend fun suggestPriority(taskTitle: String, description: String): com.example.domain.model.TaskPriority?
}
