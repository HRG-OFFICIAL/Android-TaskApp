package com.example.core.analytics

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitor @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
    private val _performanceMetrics = MutableStateFlow<Map<String, Long>>(emptyMap())
    val performanceMetrics: StateFlow<Map<String, Long>> = _performanceMetrics.asStateFlow()
    
    fun startTimer(operationName: String): Timer {
        return Timer(operationName, SystemClock.elapsedRealtime())
    }
    
    fun endTimer(timer: Timer) {
        val duration = SystemClock.elapsedRealtime() - timer.startTime
        recordMetric(timer.operationName, duration)
        analyticsManager.logPerformanceMetric(timer.operationName, duration)
    }
    
    fun recordMetric(metricName: String, value: Long) {
        val currentMetrics = _performanceMetrics.value.toMutableMap()
        currentMetrics[metricName] = value
        _performanceMetrics.value = currentMetrics
    }
    
    fun getAverageMetric(metricName: String): Long {
        return _performanceMetrics.value[metricName] ?: 0L
    }
    
    fun clearMetrics() {
        _performanceMetrics.value = emptyMap()
    }
}

data class Timer(
    val operationName: String,
    val startTime: Long
) {
    fun end(performanceMonitor: PerformanceMonitor) {
        performanceMonitor.endTimer(this)
    }
}

inline fun <T> PerformanceMonitor.measure(operationName: String, operation: () -> T): T {
    val timer = startTimer(operationName)
    return try {
        operation()
    } finally {
        endTimer(timer)
    }
}

suspend inline fun <T> PerformanceMonitor.measureSuspend(operationName: String, operation: suspend () -> T): T {
    val timer = startTimer(operationName)
    return try {
        operation()
    } finally {
        endTimer(timer)
    }
}
