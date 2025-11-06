package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.mapper.TaskMapper
import com.example.domain.model.DailyStats
import com.example.domain.model.MonthlyStats
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.model.TaskStatistics
import com.example.domain.model.WeeklyStats
import com.example.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper,
) : StatisticsRepository {

    override fun observeTaskStatistics(): Flow<TaskStatistics> {
        return taskDao.observeAll().map { entities ->
            val tasks = entities.map(taskMapper::toDomain)
            computeTaskStatistics(tasks)
        }
    }

    override fun observeDailyStats(startDate: Long, endDate: Long): Flow<List<DailyStats>> {
        return taskDao.observeAll().map { entities ->
            val tasks = entities.map(taskMapper::toDomain)
            computeDailyStats(tasks, startDate, endDate)
        }
    }

    override fun observeWeeklyStats(startWeek: Long, endWeek: Long): Flow<List<WeeklyStats>> {
        return taskDao.observeAll().map { entities ->
            val tasks = entities.map(taskMapper::toDomain)
            val daily = computeDailyStats(tasks, startWeek, endWeek)
            groupDailyIntoWeekly(daily)
        }
    }

    override fun observeMonthlyStats(year: Int): Flow<List<MonthlyStats>> {
        return taskDao.observeAll().map { entities ->
            val tasks = entities.map(taskMapper::toDomain)
            val months = (1..12).map { m ->
                val monthRange = monthStartEnd(year, m)
                val daily = computeDailyStats(tasks, monthRange.first, monthRange.second)
                val weekly = groupDailyIntoWeekly(daily)
                MonthlyStats(
                    month = m,
                    year = year,
                    totalTasks = daily.sumOf { it.tasksCreated },
                    completedTasks = daily.sumOf { it.tasksCompleted },
                    averageCompletionTime = avgCompletionTime(tasks, monthRange.first, monthRange.second),
                    productivityScore = daily.sumOf { it.productivityScore } / (daily.size.coerceAtLeast(1)),
                    weeklyStats = weekly
                )
            }
            months
        }
    }

    override suspend fun generateStatistics(): TaskStatistics {
        val tasks = taskDao.observeAll().first().map(taskMapper::toDomain)
        return computeTaskStatistics(tasks)
    }

    override suspend fun getProductivityInsights(): List<String> {
        val tasks = taskDao.observeAll().first().map(taskMapper::toDomain)
        val stats = computeTaskStatistics(tasks)
        val insights = mutableListOf<String>()

        if (stats.completionRate >= 0.7f) {
            insights += "Strong completion rate — keep up the momentum!"
        } else if (stats.overdueTasks > 0) {
            insights += "Reduce overdue tasks by scheduling smaller chunks."
        }

        val hourCounts = productiveHours(tasks)
        val topHour = hourCounts.maxByOrNull { it.value }?.key
        if (topHour != null) insights += "Most productive around ${topHour}:00 — plan key tasks then."

        val dayCounts = productiveDays(tasks)
        val topDay = dayCounts.maxByOrNull { it.value }?.key
        if (topDay != null) insights += "Best day is ${dayName(topDay)} — batch complex work there."

        if (stats.streakDays > 0) {
            insights += "Current streak: ${stats.streakDays} day(s). Aim to extend it."
        }

        if (insights.isEmpty()) insights += "Start completing tasks to unlock personalized insights."
        return insights
    }

    override suspend fun getCompletionTrend(days: Int): List<DailyStats> {
        val tasks = taskDao.observeAll().first().map(taskMapper::toDomain)
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.DAYS.toMillis(days.toLong())
        return computeDailyStats(tasks, start, end)
    }

    override suspend fun getMostProductiveHours(): Map<Int, Int> {
        val tasks = taskDao.observeAll().first().map(taskMapper::toDomain)
        return productiveHours(tasks)
    }

    override suspend fun getMostProductiveDays(): Map<Int, Int> {
        val tasks = taskDao.observeAll().first().map(taskMapper::toDomain)
        return productiveDays(tasks)
    }

    private fun computeTaskStatistics(tasks: List<Task>): TaskStatistics {
        val now = System.currentTimeMillis()
        val total = tasks.size
        val completed = tasks.count { it.isDone || it.completedAtEpochMillis != null }
        val overdue = tasks.count { it.dueAtEpochMillis?.let { d -> d < now } == true && !(it.isDone) }
        val completionRate = if (total > 0) completed.toFloat() / total else 0f

        val avgCompletionMinutes = tasks
            .filter { it.completedAtEpochMillis != null }
            .map { t ->
                val start = t.createdAtEpochMillis
                val end = t.completedAtEpochMillis ?: start
                TimeUnit.MILLISECONDS.toMinutes((end - start).coerceAtLeast(0))
            }
            .let { list -> if (list.isNotEmpty()) list.sum() / list.size else 0L }

        val productivityScore = (completed * 2 - overdue).coerceIn(0, 100)

        val tasksByPriority: Map<TaskPriority, Int> = tasks.groupingBy { it.priority }.eachCount()
        val tasksByProject: Map<String, Int> = tasks.mapNotNull { it.projectId }.groupingBy { it }.eachCount()

        val trend = getCompletionTrendInternal(tasks, 14)
        val hourCounts = productiveHours(tasks)
        val dayCounts = productiveDays(tasks)
        val mostHour = hourCounts.maxByOrNull { it.value }?.key ?: 9
        val mostDay = dayCounts.maxByOrNull { it.value }?.key ?: 1

        val streaks = computeStreaks(tasks)

        return TaskStatistics(
            totalTasks = total,
            completedTasks = completed,
            overdueTasks = overdue,
            completionRate = completionRate,
            averageCompletionTime = avgCompletionMinutes,
            productivityScore = productivityScore,
            streakDays = streaks.current,
            longestStreak = streaks.longest,
            tasksByPriority = tasksByPriority,
            tasksByProject = tasksByProject,
            completionTrend = trend,
            mostProductiveHour = mostHour,
            mostProductiveDay = mostDay,
            generatedAt = System.currentTimeMillis()
        )
    }

    private fun computeDailyStats(tasks: List<Task>, startDate: Long, endDate: Long): List<DailyStats> {
        val startCal = Calendar.getInstance().apply { timeInMillis = startDate; setToStartOfDay() }
        val endCal = Calendar.getInstance().apply { timeInMillis = endDate; setToEndOfDay() }
        val days = mutableListOf<DailyStats>()
        val cursor = startCal.clone() as Calendar
        while (cursor.timeInMillis <= endCal.timeInMillis) {
            val dayStart = cursor.clone() as Calendar
            dayStart.setToStartOfDay()
            val dayEnd = cursor.clone() as Calendar
            dayEnd.setToEndOfDay()

            val createdCount = tasks.count { it.createdAtEpochMillis in dayStart.timeInMillis..dayEnd.timeInMillis }
            val completedCount = tasks.count { (it.completedAtEpochMillis ?: Long.MIN_VALUE) in dayStart.timeInMillis..dayEnd.timeInMillis }
            val timeSpent = tasks
                .filter { (it.completedAtEpochMillis ?: Long.MIN_VALUE) in dayStart.timeInMillis..dayEnd.timeInMillis }
                .mapNotNull { it.actualDurationMinutes ?: it.estimatedDurationMinutes }
                .map { it.toLong() }
                .sum()

            val productivity = (completedCount * 10 - overdueOnDay(tasks, dayEnd.timeInMillis)).coerceAtLeast(0)

            days += DailyStats(
                date = dayStart.timeInMillis,
                tasksCompleted = completedCount,
                tasksCreated = createdCount,
                timeSpent = timeSpent,
                productivityScore = productivity
            )

            cursor.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }

    private fun groupDailyIntoWeekly(daily: List<DailyStats>): List<WeeklyStats> {
        if (daily.isEmpty()) return emptyList()
        val cal = Calendar.getInstance()
        return daily
            .groupBy { stats ->
                cal.timeInMillis = stats.date
                cal.get(Calendar.WEEK_OF_YEAR)
            }
            .map { (_, days) ->
                val weekStart = days.minOf { it.date }
                val weekEnd = days.maxOf { it.date }
                WeeklyStats(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    totalTasks = days.sumOf { it.tasksCreated },
                    completedTasks = days.sumOf { it.tasksCompleted },
                    averageCompletionTime = if (days.isNotEmpty()) days.sumOf { it.timeSpent } / days.size else 0L,
                    productivityScore = if (days.isNotEmpty()) days.sumOf { it.productivityScore } / days.size else 0,
                    dailyStats = days
                )
            }
            .sortedBy { it.weekStart }
    }

    private fun avgCompletionTime(tasks: List<Task>, start: Long, end: Long): Long {
        val completed = tasks.filter { it.completedAtEpochMillis != null && it.completedAtEpochMillis!! in start..end }
        val mins = completed.map { t ->
            val startMs = t.createdAtEpochMillis
            val endMs = t.completedAtEpochMillis ?: startMs
            TimeUnit.MILLISECONDS.toMinutes((endMs - startMs).coerceAtLeast(0))
        }
        return if (mins.isNotEmpty()) mins.sum() / mins.size else 0L
    }

    private fun getCompletionTrendInternal(tasks: List<Task>, daysBack: Int): List<DailyStats> {
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.DAYS.toMillis(daysBack.toLong())
        return computeDailyStats(tasks, start, end)
    }

    private fun productiveHours(tasks: List<Task>): Map<Int, Int> {
        val cal = Calendar.getInstance()
        val map = mutableMapOf<Int, Int>()
        tasks.forEach { t ->
            val completedAt = t.completedAtEpochMillis
            if (completedAt != null) {
                cal.timeInMillis = completedAt
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                map[hour] = (map[hour] ?: 0) + 1
            }
        }
        return map
    }

    private fun productiveDays(tasks: List<Task>): Map<Int, Int> {
        val cal = Calendar.getInstance()
        val map = mutableMapOf<Int, Int>()
        tasks.forEach { t ->
            val completedAt = t.completedAtEpochMillis
            if (completedAt != null) {
                cal.timeInMillis = completedAt
                val day = cal.get(Calendar.DAY_OF_WEEK)
                val mondayFirst = ((day + 5) % 7) + 1 // Map to 1..7 (Mon..Sun)
                map[mondayFirst] = (map[mondayFirst] ?: 0) + 1
            }
        }
        return map
    }

    private fun computeStreaks(tasks: List<Task>): StreakInfo {
        val cal = Calendar.getInstance()
        val completedDays = tasks
            .mapNotNull { it.completedAtEpochMillis }
            .map { ms ->
                cal.timeInMillis = ms
                cal.setToStartOfDay()
                cal.timeInMillis
            }
            .toSet()

        if (completedDays.isEmpty()) return StreakInfo(0, 0)

        val today = Calendar.getInstance().apply { setToStartOfDay() }
        var currentStreak = 0
        var longestStreak = 0
        var cursor = today.clone() as Calendar
        while (true) {
            val dayMs = cursor.timeInMillis
            if (completedDays.contains(dayMs)) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
                cursor.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        // Compute longest streak over the set
        val allDaysSorted = completedDays.sorted()
        var tempStreak = 1
        for (i in 1 until allDaysSorted.size) {
            val prev = allDaysSorted[i - 1]
            val curr = allDaysSorted[i]
            if (curr - prev <= TimeUnit.DAYS.toMillis(1)) {
                tempStreak++
            } else {
                longestStreak = maxOf(longestStreak, tempStreak)
                tempStreak = 1
            }
        }
        longestStreak = maxOf(longestStreak, tempStreak)
        return StreakInfo(current = currentStreak, longest = longestStreak)
    }

    private fun overdueOnDay(tasks: List<Task>, dayEndMs: Long): Int {
        return tasks.count { t ->
            val due = t.dueAtEpochMillis
            val done = t.isDone
            due != null && due <= dayEndMs && !done
        }
    }

    private fun monthStartEnd(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.setToStartOfDay()
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.DAY_OF_MONTH, -1)
        cal.setToEndOfDay()
        val end = cal.timeInMillis
        return start to end
    }

    private fun dayName(day: Int): String = when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Unknown"
    }

    private data class StreakInfo(val current: Int, val longest: Int)
}

private fun Calendar.setToStartOfDay() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.setToEndOfDay() {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}