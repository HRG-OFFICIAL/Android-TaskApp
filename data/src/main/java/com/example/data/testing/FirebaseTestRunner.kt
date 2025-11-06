package com.example.data.testing

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Test Runner - Simplified version for integration testing
 * 
 * This class provides methods to test Firebase connectivity and populate sample data
 * that showcases the enhanced UI features with colorful task cards.
 */
@Singleton
class FirebaseTestRunner @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FirebaseTestRunner"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_TASKS = "tasks"
    }

    /**
     * Test Firebase connection and populate sample data
     */
    suspend fun runFirebaseTest(): Boolean {
        return try {
            Log.i(TAG, "🚀 Starting Firebase test...")
            
            // Test connection
            testConnection()
            
            // Create sample data
            createSampleData()
            
            // CLI-only testing preferred: app does not seed 'harsh' or guest here
            
            Log.i(TAG, "✅ Firebase test completed successfully!")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase test failed", e)
            false
        }
    }

    /**
     * Test basic Firebase connection
     */
    private suspend fun testConnection() {
        Log.i(TAG, "🔗 Testing Firebase connection...")
        
        // Test Firestore write/read
        val testDoc = firestore.collection("test").document("connection")
        testDoc.set(mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to "connected"
        )).await()
        
        val snapshot = testDoc.get().await()
        if (!snapshot.exists()) {
            throw Exception("Failed to read test document")
        }
        
        // Clean up
        testDoc.delete().await()
        Log.i(TAG, "✅ Firebase connection successful")
    }

    /**
     * Create sample data for testing the enhanced UI
     */
    private suspend fun createSampleData() {
        Log.i(TAG, "📝 Creating sample data...")
        
        // Create sample users
        val users = listOf(
            createUser("demo_user_1", "demo1@taskapp.com", "Alex Demo", true),
            createUser("demo_user_2", "demo2@taskapp.com", "Jordan Demo", false),
            createUser("guest_demo", "", "Guest User", false, isGuest = true)
        )
        
        // Create sample tasks for each user
        users.forEachIndexed { userIndex, userId ->
            val tasks = generateDemoTasks(userIndex)
            
            tasks.forEachIndexed { taskIndex, task ->
                val taskDoc = firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TASKS)
                    .document("demo_task_${userIndex}_${taskIndex}")
                
                taskDoc.set(task).await()
            }
            
            Log.i(TAG, "✅ Created ${tasks.size} tasks for user $userId")
        }
    }

    /**
     * Create a sample user
     */
    private suspend fun createUser(
        id: String, 
        email: String, 
        displayName: String, 
        isPremium: Boolean,
        isGuest: Boolean = false
    ): String {
        val userData = mapOf(
            "id" to id,
            "email" to email,
            "displayName" to displayName,
            "isPremium" to isPremium,
            "isGuest" to isGuest,
            "subscriptionType" to if (isPremium) "PREMIUM" else "FREE",
            "createdAtEpochMillis" to System.currentTimeMillis(),
            "updatedAtEpochMillis" to System.currentTimeMillis(),
            "preferences" to mapOf(
                "theme" to if (isPremium) "DARK" else "LIGHT",
                "enableVoiceInput" to isPremium,
                "enableSmartSuggestions" to true,
                "enableLocationReminders" to isPremium
            )
        )
        
        firestore.collection(COLLECTION_USERS).document(id).set(userData).await()
        return id
    }

    /**
     * Generate demo tasks with different categories and properties
     */
    private fun generateDemoTasks(userIndex: Int): List<Map<String, Any>> {
        val baseTime = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        
        return when (userIndex) {
            0 -> { // Premium user - more diverse tasks
                listOf(
                    createTask(
                        "🚀 Launch product feature",
                        "Coordinate with teams for Q4 launch",
                        0, true, 75f, false, 3,
                        baseTime + (2 * dayInMillis),
                        listOf("work", "urgent")
                    ),
                    createTask(
                        "📚 Read Clean Architecture",
                        "Continue reading chapter 5",
                        1, false, 40f, false, 1,
                        baseTime + (7 * dayInMillis),
                        listOf("learning", "books")
                    ),
                    createTask(
                        "🏃‍♂️ Morning workout",
                        "30 min cardio + strength training",
                        2, false, 100f, true, 2,
                        null,
                        listOf("health", "fitness"),
                        baseTime - dayInMillis
                    ),
                    createTask(
                        "🛒 Weekly grocery shopping",
                        "Buy ingredients for meal prep",
                        3, false, 0f, false, 1,
                        baseTime + dayInMillis,
                        listOf("shopping", "food")
                    ),
                    createTask(
                        "💡 Brainstorm improvements",
                        "New features for task app",
                        4, true, 25f, false, 2,
                        null,
                        listOf("creativity", "work")
                    ),
                    createTask(
                        "📞 Call mom",
                        "Birthday party planning",
                        5, true, 0f, false, 2,
                        baseTime + (3 * dayInMillis),
                        listOf("family", "birthday")
                    )
                )
            }
            1 -> { // Free user - moderate tasks
                listOf(
                    createTask(
                        "📧 Respond to emails",
                        "Clear inbox and respond",
                        0, false, 60f, false, 2,
                        baseTime + dayInMillis,
                        listOf("work", "communication")
                    ),
                    createTask(
                        "🏠 Clean apartment",
                        "Vacuum and organize",
                        3, false, 100f, true, 1,
                        null,
                        listOf("home", "cleaning"),
                        baseTime - dayInMillis
                    ),
                    createTask(
                        "📱 Update phone apps",
                        "Check for updates",
                        4, false, 0f, false, 1,
                        null,
                        listOf("technology")
                    ),
                    createTask(
                        "🎬 Movie night",
                        "Watch movie with friends",
                        2, false, 0f, false, 1,
                        baseTime + (5 * dayInMillis),
                        listOf("entertainment", "friends")
                    )
                )
            }
            else -> { // Guest user - basic tasks
                listOf(
                    createTask(
                        "📝 Try TaskApp features",
                        "Explore the new interface",
                        0, false, 50f, false, 1,
                        null,
                        listOf("demo", "exploration")
                    ),
                    createTask(
                        "☕ Make coffee",
                        "Brew fresh coffee",
                        1, false, 100f, true, 1,
                        null,
                        listOf("morning", "coffee"),
                        baseTime - (dayInMillis / 4)
                    ),
                    createTask(
                        "🎯 Set daily goals",
                        "Plan what to accomplish",
                        2, true, 0f, false, 2,
                        null,
                        listOf("planning", "goals")
                    )
                )
            }
        }
    }

    // Removed in-app seeding functions to keep app clean; use CLI seeder

    /**
     * Helper function to create a task map
     */
    private fun createTask(
        title: String,
        description: String,
        categoryId: Int,
        isImportant: Boolean,
        progress: Float,
        isDone: Boolean,
        priority: Int,
        dueAtEpochMillis: Long?,
        tags: List<String>,
        completedAtEpochMillis: Long? = null
    ): Map<String, Any> {
        val task = mutableMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "categoryId" to categoryId,
            "isImportant" to isImportant,
            "progress" to progress,
            "isDone" to isDone,
            "priority" to priority,
            "tags" to tags,
            "createdAtEpochMillis" to System.currentTimeMillis(),
            "updatedAtEpochMillis" to System.currentTimeMillis(),
            "version" to 1
        )
        
        dueAtEpochMillis?.let { task["dueAtEpochMillis"] = it }
        completedAtEpochMillis?.let { task["completedAtEpochMillis"] = it }
        
        return task
    }

    /**
     * Clean up demo data
     */
    suspend fun cleanupDemoData() {
        Log.i(TAG, "🧹 Cleaning up demo data...")
        
        try {
            val demoUserIds = listOf("demo_user_1", "demo_user_2", "guest_demo")
            
            demoUserIds.forEach { userId ->
                // Delete user's tasks
                val tasksSnapshot = firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TASKS)
                    .get()
                    .await()
                
                tasksSnapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }
                
                // Delete user document
                firestore.collection(COLLECTION_USERS).document(userId).delete().await()
            }
            
            Log.i(TAG, "✅ Demo data cleanup completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to cleanup demo data", e)
        }
    }
}