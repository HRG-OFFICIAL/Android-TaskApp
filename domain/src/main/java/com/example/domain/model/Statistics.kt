package com.example.domain.model

data class TaskStatistics(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Float = 0f,
    val averageCompletionTime: Long = 0L, // in minutes
    val productivityScore: Int = 0, // 0-100
    val streakDays: Int = 0,
    val longestStreak: Int = 0,
    val tasksByPriority: Map<TaskPriority, Int> = emptyMap(),
    val tasksByProject: Map<String, Int> = emptyMap(),
    val completionTrend: List<DailyStats> = emptyList(),
    val mostProductiveHour: Int = 9, // 0-23
    val mostProductiveDay: Int = 1, // 1-7 (Monday-Sunday)
    val generatedAt: Long = System.currentTimeMillis()
)

data class DailyStats(
    val date: Long, // epoch millis
    val tasksCompleted: Int = 0,
    val tasksCreated: Int = 0,
    val timeSpent: Long = 0L, // in minutes
    val productivityScore: Int = 0
)

data class WeeklyStats(
    val weekStart: Long, // epoch millis
    val weekEnd: Long,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val averageCompletionTime: Long = 0L,
    val productivityScore: Int = 0,
    val dailyStats: List<DailyStats> = emptyList()
)

data class MonthlyStats(
    val month: Int, // 1-12
    val year: Int,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val averageCompletionTime: Long = 0L,
    val productivityScore: Int = 0,
    val weeklyStats: List<WeeklyStats> = emptyList()
)
