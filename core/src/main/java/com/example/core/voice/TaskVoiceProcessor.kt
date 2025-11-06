package com.example.core.voice

// Removed domain imports to avoid circular dependency
import javax.inject.Inject
import javax.inject.Singleton

enum class VoiceTaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}

data class VoiceTaskData(
    val title: String,
    val description: String? = null,
    val priority: VoiceTaskPriority = VoiceTaskPriority.MEDIUM,
    val dueDate: String? = null
)

@Singleton
class TaskVoiceProcessor @Inject constructor() {
    
    fun processVoiceInput(spokenText: String): ProcessedTask {
        val normalizedText = spokenText.lowercase().trim()
        
        // Extract priority keywords
        val priority = extractPriority(normalizedText)
        
        // Extract due date keywords
        val dueDate = extractDueDate(normalizedText)
        
        // Extract project keywords
        val project = extractProject(normalizedText)
        
        // Clean up the text for title
        val title = cleanTextForTitle(normalizedText)
        
        return ProcessedTask(
            title = title,
            description = "",
            priority = priority,
            dueDate = dueDate,
            project = project,
            originalText = spokenText
        )
    }
    
    private fun extractPriority(text: String): VoiceTaskPriority {
        return when {
            text.contains("urgent") || text.contains("asap") || text.contains("critical") -> VoiceTaskPriority.URGENT
            text.contains("important") || text.contains("high priority") -> VoiceTaskPriority.HIGH
            text.contains("low priority") || text.contains("optional") -> VoiceTaskPriority.LOW
            else -> VoiceTaskPriority.MEDIUM
        }
    }
    
    private fun extractDueDate(text: String): Long? {
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        
        return when {
            text.contains("today") -> now
            text.contains("tomorrow") -> now + dayInMillis
            text.contains("this week") -> now + (3 * dayInMillis)
            text.contains("next week") -> now + (7 * dayInMillis)
            text.contains("this month") -> now + (14 * dayInMillis)
            text.contains("next month") -> now + (30 * dayInMillis)
            else -> null
        }
    }
    
    private fun extractProject(text: String): String? {
        val projectKeywords = mapOf(
            "work" to "Work",
            "personal" to "Personal",
            "health" to "Health",
            "finance" to "Finance",
            "shopping" to "Shopping",
            "travel" to "Travel",
            "learning" to "Learning",
            "family" to "Family",
            "hobbies" to "Hobbies"
        )
        
        return projectKeywords.entries.find { (keyword, _) ->
            text.contains(keyword)
        }?.value
    }
    
    private fun cleanTextForTitle(text: String): String {
        // Remove common voice command words
        val wordsToRemove = listOf(
            "create", "add", "new", "task", "todo", "remind", "me", "to",
            "urgent", "important", "high", "low", "priority", "today", "tomorrow",
            "this week", "next week", "this month", "next month"
        )
        
        var cleanedText = text
        wordsToRemove.forEach { word ->
            cleanedText = cleanedText.replace("\\b$word\\b".toRegex(), "")
        }
        
        return cleanedText.trim().replace("\\s+".toRegex(), " ").replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
}

data class ProcessedTask(
    val title: String,
    val description: String,
    val priority: VoiceTaskPriority,
    val dueDate: Long?,
    val project: String?,
    val originalText: String
)
