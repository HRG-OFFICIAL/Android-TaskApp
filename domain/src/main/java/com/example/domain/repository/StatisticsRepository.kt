package com.example.domain.repository

import com.example.domain.model.TaskStatistics
import com.example.domain.model.DailyStats
import com.example.domain.model.WeeklyStats
import com.example.domain.model.MonthlyStats
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun observeTaskStatistics(): Flow<TaskStatistics>
    fun observeDailyStats(startDate: Long, endDate: Long): Flow<List<DailyStats>>
    fun observeWeeklyStats(startWeek: Long, endWeek: Long): Flow<List<WeeklyStats>>
    fun observeMonthlyStats(year: Int): Flow<List<MonthlyStats>>
    suspend fun generateStatistics(): TaskStatistics
    suspend fun getProductivityInsights(): List<String>
    suspend fun getCompletionTrend(days: Int): List<DailyStats>
    suspend fun getMostProductiveHours(): Map<Int, Int> // hour -> count
    suspend fun getMostProductiveDays(): Map<Int, Int> // day -> count
}
