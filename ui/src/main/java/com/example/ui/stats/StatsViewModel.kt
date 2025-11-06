package com.example.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.TaskStatistics
import com.example.domain.model.DailyStats
import com.example.domain.usecase.statistics.GenerateStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val generateStatisticsUseCase: GenerateStatisticsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val statistics = generateStatisticsUseCase()
                val recentActivity = generateRecentActivity()
                val insights = generateInsights()
                
                _uiState.value = _uiState.value.copy(
                    statistics = statistics,
                    recentActivity = recentActivity,
                    insights = insights
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }
    
    private fun generateRecentActivity(): List<DailyStats> {
        // Generate dummy recent activity data
        return listOf(
            DailyStats(
                date = System.currentTimeMillis() - 86400000L, // Yesterday
                tasksCompleted = 5,
                tasksCreated = 3,
                productivityScore = 85
            ),
            DailyStats(
                date = System.currentTimeMillis() - 172800000L, // 2 days ago
                tasksCompleted = 3,
                tasksCreated = 4,
                productivityScore = 72
            ),
            DailyStats(
                date = System.currentTimeMillis() - 259200000L, // 3 days ago
                tasksCompleted = 7,
                tasksCreated = 2,
                productivityScore = 91
            )
        )
    }
    
    private fun generateInsights(): List<String> {
        return listOf(
            "You're most productive in the morning - try scheduling important tasks before 10 AM",
            "Consider breaking down large tasks into smaller, manageable chunks",
            "Your completion rate has improved by 15% this week - keep it up!",
            "Try using the Pomodoro technique for better focus during work sessions"
        )
    }
    
    fun refreshStatistics() {
        loadStatistics()
    }

    // Alias for UI action
    fun reload() = refreshStatistics()
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StatsUiState(
    val statistics: TaskStatistics = TaskStatistics(),
    val recentActivity: List<DailyStats> = emptyList(),
    val insights: List<String> = emptyList(),
    val error: String? = null
)