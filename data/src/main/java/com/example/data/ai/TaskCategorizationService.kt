package com.example.data.ai

import android.content.Context
import com.example.domain.model.TaskPriority
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskCategorizationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val modelPath = "task_categorization_model.tflite"
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(modelPath)
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            // Handle model loading error
            // In a real app, you'd have a fallback mechanism
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
    
    fun categorizeTask(title: String, description: String): List<String> {
        return try {
            val input = preprocessText("$title $description")
            val output = runInference(input)
            postprocessOutput(output)
        } catch (e: Exception) {
            // Fallback to rule-based categorization
            fallbackCategorization(title, description)
        }
    }
    
    fun suggestPriority(title: String, description: String): TaskPriority? {
        return try {
            val input = preprocessText("$title $description")
            val output = runPriorityInference(input)
            mapToPriority(output)
        } catch (e: Exception) {
            // Fallback to rule-based priority suggestion
            fallbackPrioritySuggestion(title, description)
        }
    }
    
    fun suggestDueDate(title: String, description: String): Long? {
        return try {
            val input = preprocessText("$title $description")
            val output = runDueDateInference(input)
            mapToDueDate(output)
        } catch (e: Exception) {
            // Fallback to rule-based due date suggestion
            fallbackDueDateSuggestion(title, description)
        }
    }
    
    private fun preprocessText(text: String): FloatArray {
        // Convert text to numerical features
        // This would typically involve:
        // 1. Tokenization
        // 2. Word embedding
        // 3. Feature extraction
        // For now, return a simple feature vector
        return FloatArray(128) { 0.0f } // Placeholder
    }
    
    private fun runInference(input: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(10) } // 10 categories
        interpreter.run(input, output)
        return output[0]
    }
    
    private fun runPriorityInference(input: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(4) } // 4 priority levels
        interpreter.run(input, output)
        return output[0]
    }
    
    private fun runDueDateInference(input: FloatArray): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Model not loaded")
        val output = Array(1) { FloatArray(1) } // Single due date prediction
        interpreter.run(input, output)
        return output[0]
    }
    
    private fun postprocessOutput(output: FloatArray): List<String> {
        // Convert model output to category names
        val categories = mutableListOf<String>()
        val threshold = 0.5f
        
        val categoryNames = listOf(
            "Work", "Personal", "Health", "Finance", "Shopping",
            "Travel", "Learning", "Family", "Hobbies", "Urgent"
        )
        
        output.forEachIndexed { index, confidence ->
            if (confidence > threshold && index < categoryNames.size) {
                categories.add(categoryNames[index])
            }
        }
        
        return if (categories.isEmpty()) listOf("General") else categories
    }
    
    private fun mapToPriority(output: FloatArray): TaskPriority {
        val maxIndex = output.indices.maxByOrNull { output[it] } ?: 1
        return when (maxIndex) {
            0 -> TaskPriority.LOW
            1 -> TaskPriority.MEDIUM
            2 -> TaskPriority.HIGH
            3 -> TaskPriority.URGENT
            else -> TaskPriority.MEDIUM
        }
    }
    
    private fun mapToDueDate(output: FloatArray): Long {
        // Convert model output to epoch milliseconds
        val daysFromNow = output[0].toInt()
        return System.currentTimeMillis() + (daysFromNow * 24 * 60 * 60 * 1000L)
    }
    
    // Fallback methods for when AI model is not available
    private fun fallbackCategorization(title: String, description: String): List<String> {
        val text = "$title $description".lowercase()
        val categories = mutableListOf<String>()
        
        when {
            text.contains("work") || text.contains("meeting") || text.contains("project") -> 
                categories.add("Work")
            text.contains("buy") || text.contains("shop") || text.contains("purchase") -> 
                categories.add("Shopping")
            text.contains("exercise") || text.contains("gym") || text.contains("health") -> 
                categories.add("Health")
            text.contains("learn") || text.contains("study") || text.contains("course") -> 
                categories.add("Learning")
            text.contains("urgent") || text.contains("asap") || text.contains("immediate") -> 
                categories.add("Urgent")
            else -> categories.add("Personal")
        }
        
        return categories
    }
    
    private fun fallbackPrioritySuggestion(title: String, description: String): TaskPriority {
        val text = "$title $description".lowercase()
        return when {
            text.contains("urgent") || text.contains("asap") || text.contains("critical") -> 
                TaskPriority.URGENT
            text.contains("important") || text.contains("high") -> 
                TaskPriority.HIGH
            text.contains("low") || text.contains("optional") -> 
                TaskPriority.LOW
            else -> TaskPriority.MEDIUM
        }
    }
    
    private fun fallbackDueDateSuggestion(title: String, description: String): Long? {
        val text = "$title $description".lowercase()
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        
        return when {
            text.contains("today") -> now
            text.contains("tomorrow") -> now + dayInMillis
            text.contains("this week") -> now + (3 * dayInMillis)
            text.contains("next week") -> now + (7 * dayInMillis)
            text.contains("this month") -> now + (14 * dayInMillis)
            else -> null
        }
    }
    
    fun cleanup() {
        interpreter?.close()
        interpreter = null
    }
}
