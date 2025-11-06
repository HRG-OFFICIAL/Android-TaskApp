package com.example.core.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val analytics: FirebaseAnalytics = Firebase.analytics
    
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        analytics.logEvent(eventName) {
            parameters.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
    
    fun logTaskCreated(taskId: String, priority: String, hasDueDate: Boolean) {
        logEvent("task_created", mapOf(
            "task_id" to taskId,
            "priority" to priority,
            "has_due_date" to hasDueDate
        ))
    }
    
    fun logTaskCompleted(taskId: String, completionTime: Long) {
        logEvent("task_completed", mapOf(
            "task_id" to taskId,
            "completion_time" to completionTime
        ))
    }
    
    fun logTaskDeleted(taskId: String, reason: String) {
        logEvent("task_deleted", mapOf(
            "task_id" to taskId,
            "reason" to reason
        ))
    }
    
    fun logProjectCreated(projectId: String, isShared: Boolean) {
        logEvent("project_created", mapOf(
            "project_id" to projectId,
            "is_shared" to isShared
        ))
    }
    
    fun logUserSignedIn(method: String) {
        logEvent("user_signed_in", mapOf(
            "method" to method
        ))
    }
    
    fun logUserSignedOut() {
        logEvent("user_signed_out")
    }
    
    fun logPremiumUpgrade(planId: String, price: String) {
        logEvent("premium_upgrade", mapOf(
            "plan_id" to planId,
            "price" to price
        ))
    }
    
    fun logFeatureUsed(featureName: String, isPremium: Boolean) {
        logEvent("feature_used", mapOf(
            "feature_name" to featureName,
            "is_premium" to isPremium
        ))
    }
    
    fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf(
            "screen_name" to screenName
        ))
    }
    
    fun logError(errorType: String, errorMessage: String, stackTrace: String? = null) {
        logEvent("error_occurred", mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage,
            "stack_trace" to (stackTrace ?: "")
        ))
    }
    
    fun logPerformanceMetric(metricName: String, value: Long, unit: String = "ms") {
        logEvent("performance_metric", mapOf(
            "metric_name" to metricName,
            "value" to value,
            "unit" to unit
        ))
    }
    
    fun setUserProperty(propertyName: String, value: String) {
        analytics.setUserProperty(propertyName, value)
    }
    
    fun setUserId(userId: String) {
        analytics.setUserId(userId)
    }
}
