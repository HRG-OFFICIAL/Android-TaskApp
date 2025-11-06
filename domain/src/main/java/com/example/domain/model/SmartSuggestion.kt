package com.example.domain.model

data class SmartSuggestion(
    val id: String,
    val type: SuggestionType,
    val title: String,
    val description: String,
    val confidence: Float, // 0.0 to 1.0
    val taskId: String? = null,
    val projectId: String? = null,
    val labelId: String? = null,
    val dueDate: Long? = null,
    val priority: TaskPriority? = null,
    val generatedAt: Long = System.currentTimeMillis(),
    val isAccepted: Boolean = false,
    val isDismissed: Boolean = false
)

enum class SuggestionType {
    DUE_DATE, PRIORITY, PROJECT, LABEL, CATEGORY, TIME_ESTIMATE, 
    RECURRING, COLLABORATION, LOCATION, REMINDER
}

data class AIPrediction(
    val taskId: String,
    val predictedCompletionTime: Long, // in minutes
    val confidence: Float,
    val factors: List<String> = emptyList(),
    val generatedAt: Long = System.currentTimeMillis()
)

data class SmartInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val actionable: Boolean = true,
    val priority: InsightPriority = InsightPriority.MEDIUM,
    val data: Map<String, Any> = emptyMap(),
    val generatedAt: Long = System.currentTimeMillis()
)

enum class InsightType {
    PRODUCTIVITY_PATTERN, OVERDUE_TREND, COMPLETION_RATE, 
    TIME_MANAGEMENT, PRIORITY_OPTIMIZATION, COLLABORATION_OPPORTUNITY
}

enum class InsightPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}
