package com.example.data.ai

import android.content.Context
import com.example.domain.model.Task
import com.example.domain.model.TaskStatistics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductivityAnalysisService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val modelPath = "productivity_analysis_model.tflite"
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(modelPath)
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            // Handle model loading error
        }
    }
    
    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    fun analyzeProductivityPatterns(tasks: List<Task>): List<String> {
        return try {
            val features = extractProductivityFeatures(tasks)
            val output = runProductivityInference(features)
            generateInsights(output)
        } catch (e: Exception) {
            fallbackProductivityAnalysis(tasks)
        }
    }
    
    fun predictOptimalTaskScheduling(tasks: List<Task>): List<Task> {
        return try {
            val features = extractSchedulingFeatures(tasks)
            val output = runSchedulingInference(features)
            optimizeTaskOrder(tasks, output)
        } catch (e: Exception) {
            // Fallback to simple priority-based sorting
            tasks.sortedByDescending { it.priority.value }
        }
    }
    
    fun calculateProductivityScore(statistics: TaskStatistics): Int {
        return try {
            val features = extractScoreFeatures(statistics)
            val output = runScoreInference(features)
            (output[0] * 100).toInt().coerceIn(0, 100)
        } catch (e: Exception) {
            fallbackProductivityScore(statistics)
        }
    }
    
    private fun extractProductivityFeatures(tasks: List<Task>): FloatArray {
        val features = FloatArray(20)
        
        // Extract various productivity metrics
        features[0] = tasks.size.toFloat() // Total tasks
        features[1] = tasks.count { it.isDone }.toFloat() // Completed tasks
        features[2] = tasks.count { !it.isDone }.toFloat() // Pending tasks
        features[3] = tasks.count { it.priority.value >= 2 }.toFloat() // High priority tasks
        features[4] = tasks.count { it.dueAtEpochMillis != null }.toFloat() // Tasks with due dates
        
        // Calculate completion rate
        features[5] = if (tasks.isNotEmpty()) {
            tasks.count { it.isDone }.toFloat() / tasks.size.toFloat()
        } else 0f
        
        // Calculate average task duration (if available)
        val tasksWithDuration = tasks.filter { it.actualDurationMinutes != null }
        features[6] = if (tasksWithDuration.isNotEmpty()) {
            tasksWithDuration.map { it.actualDurationMinutes!! }.average().toFloat()
        } else 0f
        
        // Calculate overdue tasks
        val now = System.currentTimeMillis()
        features[7] = tasks.count { 
            it.dueAtEpochMillis != null && it.dueAtEpochMillis!! < now && !it.isDone
        }.toFloat()
        
        // Calculate task distribution by priority
        features[8] = tasks.count { it.priority == com.example.domain.model.TaskPriority.LOW }.toFloat()
        features[9] = tasks.count { it.priority == com.example.domain.model.TaskPriority.MEDIUM }.toFloat()
        features[10] = tasks.count { it.priority == com.example.domain.model.TaskPriority.HIGH }.toFloat()
        features[11] = tasks.count { it.priority == com.example.domain.model.TaskPriority.URGENT }.toFloat()
        
        // Calculate project distribution
        val projectCount = tasks.mapNotNull { it.projectId }.distinct().size
        features[12] = projectCount.toFloat()
        
        // Calculate label distribution
        val labelCount = tasks.flatMap { it.labelIds }.distinct().size
        features[13] = labelCount.toFloat()
        
        // Calculate recurring tasks
        features[14] = tasks.count { it.recurringRule != null }.toFloat()
        
        // Calculate collaboration tasks
        features[15] = tasks.count { it.collaborators.isNotEmpty() }.toFloat()
        
        // Calculate tasks with attachments
        features[16] = tasks.count { it.attachments.isNotEmpty() }.toFloat()
        
        // Calculate tasks with location
        features[17] = tasks.count { it.location != null }.toFloat()
        
        // Calculate average task age
        val nowMillis = System.currentTimeMillis()
        val avgAge = if (tasks.isNotEmpty()) {
            tasks.map { nowMillis - it.createdAtEpochMillis }.average() / (24 * 60 * 60 * 1000) // in days
        } else 0.0
        features[18] = avgAge.toFloat()
        
        // Calculate task update frequency
        val avgUpdateAge = if (tasks.isNotEmpty()) {
            tasks.map { nowMillis - it.updatedAtEpochMillis }.average() / (24 * 60 * 60 * 1000) // in days
        } else 0.0
        features[19] = avgUpdateAge.toFloat()
        
        return features
    }
    
    private fun extractSchedulingFeatures(tasks: List<Task>): FloatArray {
        val features = FloatArray(15)
        
        // Extract scheduling-related features
        features[0] = tasks.size.toFloat() // Total tasks
        features[1] = tasks.count { it.dueAtEpochMillis != null }.toFloat() // Tasks with due dates
        features[2] = tasks.count { it.estimatedDurationMinutes != null }.toFloat() // Tasks with time estimates
        
        // Calculate priority distribution
        features[3] = tasks.count { it.priority == com.example.domain.model.TaskPriority.URGENT }.toFloat()
        features[4] = tasks.count { it.priority == com.example.domain.model.TaskPriority.HIGH }.toFloat()
        features[5] = tasks.count { it.priority == com.example.domain.model.TaskPriority.MEDIUM }.toFloat()
        features[6] = tasks.count { it.priority == com.example.domain.model.TaskPriority.LOW }.toFloat()
        
        // Calculate due date distribution
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        features[7] = tasks.count { 
            it.dueAtEpochMillis != null && it.dueAtEpochMillis!! <= now + dayInMillis
        }.toFloat() // Due today
        features[8] = tasks.count { 
            it.dueAtEpochMillis != null && it.dueAtEpochMillis!! <= now + (7 * dayInMillis)
        }.toFloat() // Due this week
        features[9] = tasks.count { 
            it.dueAtEpochMillis != null && it.dueAtEpochMillis!! <= now + (30 * dayInMillis)
        }.toFloat() // Due this month
        
        // Calculate average estimated duration
        val tasksWithDuration = tasks.filter { it.estimatedDurationMinutes != null }
        features[10] = if (tasksWithDuration.isNotEmpty()) {
            tasksWithDuration.map { it.estimatedDurationMinutes!! }.average().toFloat()
        } else 0f
        
        // Calculate project distribution
        val projectCount = tasks.mapNotNull { it.projectId }.distinct().size
        features[11] = projectCount.toFloat()
        
        // Calculate collaboration tasks
        features[12] = tasks.count { it.collaborators.isNotEmpty() }.toFloat()
        
        // Calculate recurring tasks
        features[13] = tasks.count { it.recurringRule != null }.toFloat()
        
        // Calculate pinned tasks
        features[14] = tasks.count { it.isPinned }.toFloat()
        
        return features
    }
    
    private fun extractScoreFeatures(statistics: TaskStatistics): FloatArray {
        val features = FloatArray(10)
        
        features[0] = statistics.totalTasks.toFloat()
        features[1] = statistics.completedTasks.toFloat()
        features[2] = statistics.overdueTasks.toFloat()
        features[3] = statistics.completionRate
        features[4] = statistics.averageCompletionTime.toFloat()
        features[5] = statistics.streakDays.toFloat()
        features[6] = statistics.longestStreak.toFloat()
        features[7] = statistics.mostProductiveHour.toFloat()
        features[8] = statistics.mostProductiveDay.toFloat()
        features[9] = statistics.productivityScore.toFloat()
        
        return features
    }
    
    private fun runProductivityInference(features: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(5) } // 5 insight categories
        interpreter.run(features, output)
        return output[0]
    }
    
    private fun runSchedulingInference(features: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(10) } // 10 scheduling recommendations
        interpreter.run(features, output)
        return output[0]
    }
    
    private fun runScoreInference(features: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(1) } // Single productivity score
        interpreter.run(features, output)
        return output[0]
    }
    
    private fun generateInsights(output: FloatArray): List<String> {
        val insights = mutableListOf<String>()
        val threshold = 0.5f
        
        val insightTemplates = listOf(
            "Consider breaking down large tasks into smaller, manageable pieces",
            "Your completion rate is excellent! Keep up the great work.",
            "You have several overdue tasks. Consider rescheduling or delegating them.",
            "Try to maintain a consistent daily routine for better productivity.",
            "Consider using time-blocking techniques for better task management."
        )
        
        output.forEachIndexed { index, confidence ->
            if (confidence > threshold && index < insightTemplates.size) {
                insights.add(insightTemplates[index])
            }
        }
        
        return insights
    }
    
    private fun optimizeTaskOrder(tasks: List<Task>, recommendations: FloatArray): List<Task> {
        // Use AI recommendations to reorder tasks
        // This is a simplified implementation
        return tasks.sortedWith { task1, task2 ->
            val score1 = calculateTaskScore(task1, recommendations)
            val score2 = calculateTaskScore(task2, recommendations)
            score2.compareTo(score1) // Higher score first
        }
    }
    
    private fun calculateTaskScore(task: Task, recommendations: FloatArray): Float {
        var score = 0f
        
        // Priority weight
        score += task.priority.value * recommendations[0]
        
        // Due date weight
        if (task.dueAtEpochMillis != null) {
            val daysUntilDue = (task.dueAtEpochMillis!! - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
            score += (1f / (daysUntilDue + 1)) * recommendations[1]
        }
        
        // Duration weight
        if (task.estimatedDurationMinutes != null) {
            score += (1f / (task.estimatedDurationMinutes!! / 60f + 1)) * recommendations[2]
        }
        
        // Collaboration weight
        score += task.collaborators.size * recommendations[3]
        
        // Project weight
        if (task.projectId != null) {
            score += recommendations[4]
        }
        
        return score
    }
    
    private fun fallbackProductivityAnalysis(tasks: List<Task>): List<String> {
        val insights = mutableListOf<String>()
        
        val completionRate = if (tasks.isNotEmpty()) {
            tasks.count { it.isDone }.toFloat() / tasks.size.toFloat()
        } else 0f
        
        when {
            completionRate > 0.8f -> insights.add("Great job! You're maintaining a high completion rate.")
            completionRate < 0.5f -> insights.add("Consider breaking down large tasks into smaller ones.")
            else -> insights.add("You're making steady progress. Keep it up!")
        }
        
        val overdueCount = tasks.count { 
            it.dueAtEpochMillis != null && it.dueAtEpochMillis!! < System.currentTimeMillis() && !it.isDone
        }
        
        if (overdueCount > 0) {
            insights.add("You have $overdueCount overdue tasks. Consider rescheduling them.")
        }
        
        return insights
    }
    
    private fun fallbackProductivityScore(statistics: TaskStatistics): Int {
        var score = 0
        
        // Base score from completion rate
        score += (statistics.completionRate * 40).toInt()
        
        // Bonus for streak
        score += minOf(statistics.streakDays * 2, 20)
        
        // Penalty for overdue tasks
        score -= minOf(statistics.overdueTasks * 5, 30)
        
        return score.coerceIn(0, 100)
    }
    
    fun cleanup() {
        interpreter?.close()
        interpreter = null
    }
}
