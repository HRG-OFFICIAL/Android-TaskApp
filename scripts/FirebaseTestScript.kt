package scripts

import android.util.Log
import com.example.data.remote.dto.TaskDto
import com.example.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Connection Test Script
 * 
 * This script tests Firebase connectivity and populates the database with sample data
 * for 2 registered users and 1 guest user to demonstrate the enhanced UI features.
 */
@Singleton
class FirebaseTestScript @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FirebaseTestScript"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_TASKS = "tasks"
        private const val COLLECTION_PROJECTS = "projects"
    }

    /**
     * Main test function that runs all Firebase tests
     */
    suspend fun runFirebaseTests(): TestResult {
        return try {
            Log.i(TAG, "🚀 Starting Firebase Connection Tests...")
            
            // Test 1: Firebase Connection
            testFirebaseConnection()
            
            // Test 2: Authentication Test
            testAuthentication()
            
            // Test 3: Create Test Users
            val testUsers = createTestUsers()
            
            // Test 4: Populate Sample Data
            populateSampleData(testUsers)
            
            // Test 5: Test Data Retrieval
            testDataRetrieval(testUsers)
            
            Log.i(TAG, "✅ All Firebase tests completed successfully!")
            TestResult.Success("All Firebase tests passed")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase test failed", e)
            TestResult.Error("Firebase test failed: ${e.message}")
        }
    }

    /**
     * Test Firebase connection and basic functionality
     */
    private suspend fun testFirebaseConnection() {
        Log.i(TAG, "🔗 Testing Firebase connection...")
        
        try {
            // Test Firestore connection by writing a test document
            val testDoc = firestore.collection("test").document("connection")
            testDoc.set(mapOf(
                "timestamp" to System.currentTimeMillis(),
                "status" to "connected"
            )).await()
            
            // Read it back to confirm connection
            val snapshot = testDoc.get().await()
            if (snapshot.exists()) {
                Log.i(TAG, "✅ Firestore connection successful")
            } else {
                throw Exception("Failed to read test document")
            }
            
            // Clean up test document
            testDoc.delete().await()
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firestore connection failed", e)
            throw e
        }
    }

    /**
     * Test Firebase Authentication
     */
    private suspend fun testAuthentication() {
        Log.i(TAG, "🔐 Testing Firebase Authentication...")
        
        try {
            // Test anonymous authentication for guest user
            val result = auth.signInAnonymously().await()
            val user = result.user
            
            if (user != null) {
                Log.i(TAG, "✅ Anonymous authentication successful: ${user.uid}")
            } else {
                throw Exception("Anonymous authentication failed")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Authentication test failed", e)
            throw e
        }
    }

    /**
     * Create test users with different profiles
     */
    private suspend fun createTestUsers(): List<TestUser> {
        Log.i(TAG, "👥 Creating test users...")
        
        val users = listOf(
            // User 1: Premium user with lots of tasks
            TestUser(
                id = "user_premium_001",
                email = "premium.user@taskapp.com",
                displayName = "Alex Premium",
                isPremium = true,
                subscriptionType = SubscriptionType.PREMIUM,
                preferences = UserPreferences(
                    theme = Theme.DARK,
                    enableVoiceInput = true,
                    enableSmartSuggestions = true,
                    enableLocationReminders = true
                )
            ),
            
            // User 2: Free user with moderate usage
            TestUser(
                id = "user_free_002",
                email = "free.user@taskapp.com",
                displayName = "Jordan Free",
                isPremium = false,
                subscriptionType = SubscriptionType.FREE,
                preferences = UserPreferences(
                    theme = Theme.LIGHT,
                    enableVoiceInput = false,
                    enableSmartSuggestions = true,
                    enableLocationReminders = false
                )
            ),
            
            // User 3: Guest user (anonymous)
            TestUser(
                id = "guest_user_003",
                email = "",
                displayName = "Guest User",
                isPremium = false,
                subscriptionType = SubscriptionType.FREE,
                isGuest = true,
                preferences = UserPreferences(
                    theme = Theme.SYSTEM,
                    enableVoiceInput = false,
                    enableSmartSuggestions = false,
                    enableLocationReminders = false
                )
            )
        )
        
        // Create user documents in Firestore
        users.forEach { user ->
            val userDoc = firestore.collection(COLLECTION_USERS).document(user.id)
            userDoc.set(user.toFirestoreMap()).await()
            Log.i(TAG, "✅ Created user: ${user.displayName}")
        }
        
        return users
    }

    /**
     * Populate sample data for testing the enhanced UI
     */
    private suspend fun populateSampleData(users: List<TestUser>) {
        Log.i(TAG, "📝 Populating sample data...")
        
        users.forEach { user ->
            val tasks = generateSampleTasks(user)
            
            tasks.forEach { task ->
                val taskDoc = firestore
                    .collection(COLLECTION_USERS)
                    .document(user.id)
                    .collection(COLLECTION_TASKS)
                    .document()
                
                taskDoc.set(task.toFirestoreMap()).await()
            }
            
            Log.i(TAG, "✅ Created ${tasks.size} tasks for ${user.displayName}")
        }
    }

    /**
     * Generate sample tasks with different categories and properties
     */
    private fun generateSampleTasks(user: TestUser): List<SampleTask> {
        val baseTime = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        
        return when {
            user.isPremium -> {
                // Premium user gets more diverse tasks
                listOf(
                    SampleTask(
                        title = "🚀 Launch new product feature",
                        description = "Coordinate with design and engineering teams for the Q4 product launch",
                        categoryId = 0, // Blue
                        isImportant = true,
                        progress = 75f,
                        isDone = false,
                        priority = 3,
                        dueAtEpochMillis = baseTime + (2 * dayInMillis),
                        tags = listOf("work", "urgent", "product")
                    ),
                    SampleTask(
                        title = "📚 Read 'Clean Architecture' book",
                        description = "Continue reading chapter 5 about dependency inversion",
                        categoryId = 1, // Yellow
                        isImportant = false,
                        progress = 40f,
                        isDone = false,
                        priority = 1,
                        dueAtEpochMillis = baseTime + (7 * dayInMillis),
                        tags = listOf("learning", "books")
                    ),
                    SampleTask(
                        title = "🏃‍♂️ Morning workout routine",
                        description = "30 minutes cardio + strength training",
                        categoryId = 2, // Pink
                        isImportant = false,
                        progress = 100f,
                        isDone = true,
                        priority = 2,
                        completedAtEpochMillis = baseTime - dayInMillis,
                        tags = listOf("health", "fitness")
                    ),
                    SampleTask(
                        title = "🛒 Weekly grocery shopping",
                        description = "Buy ingredients for meal prep: chicken, vegetables, rice",
                        categoryId = 3, // Green
                        isImportant = false,
                        progress = 0f,
                        isDone = false,
                        priority = 1,
                        dueAtEpochMillis = baseTime + dayInMillis,
                        tags = listOf("shopping", "food")
                    ),
                    SampleTask(
                        title = "💡 Brainstorm app improvements",
                        description = "Think of new features for the task management app",
                        categoryId = 4, // Purple
                        isImportant = true,
                        progress = 25f,
                        isDone = false,
                        priority = 2,
                        tags = listOf("creativity", "work", "ideas")
                    ),
                    SampleTask(
                        title = "📞 Call mom for birthday planning",
                        description = "Discuss plans for mom's surprise birthday party next month",
                        categoryId = 5, // Orange
                        isImportant = true,
                        progress = 0f,
                        isDone = false,
                        priority = 2,
                        dueAtEpochMillis = baseTime + (3 * dayInMillis),
                        tags = listOf("family", "birthday")
                    ),
                    SampleTask(
                        title = "✅ Complete project documentation",
                        description = "Write comprehensive documentation for the new API endpoints",
                        categoryId = 6, // Red
                        isImportant = false,
                        progress = 100f,
                        isDone = true,
                        priority = 2,
                        completedAtEpochMillis = baseTime - (2 * dayInMillis),
                        tags = listOf("work", "documentation")
                    ),
                    SampleTask(
                        title = "🌱 Water plants",
                        description = "Water all indoor plants and check for any issues",
                        categoryId = 7, // Teal
                        isImportant = false,
                        progress = 100f,
                        isDone = true,
                        priority = 1,
                        completedAtEpochMillis = baseTime - (dayInMillis / 2),
                        tags = listOf("home", "plants")
                    )
                )
            }
            
            user.isGuest -> {
                // Guest user gets basic tasks
                listOf(
                    SampleTask(
                        title = "📝 Try out TaskApp features",
                        description = "Explore the beautiful new interface and features",
                        categoryId = 0, // Blue
                        isImportant = false,
                        progress = 50f,
                        isDone = false,
                        priority = 1,
                        tags = listOf("demo", "exploration")
                    ),
                    SampleTask(
                        title = "☕ Make coffee",
                        description = "Brew a fresh cup of coffee to start the day",
                        categoryId = 1, // Yellow
                        isImportant = false,
                        progress = 100f,
                        isDone = true,
                        priority = 1,
                        completedAtEpochMillis = baseTime - (dayInMillis / 4),
                        tags = listOf("morning", "coffee")
                    ),
                    SampleTask(
                        title = "🎯 Set daily goals",
                        description = "Plan out what to accomplish today",
                        categoryId = 2, // Pink
                        isImportant = true,
                        progress = 0f,
                        isDone = false,
                        priority = 2,
                        tags = listOf("planning", "goals")
                    )
                )
            }
            
            else -> {
                // Free user gets moderate tasks
                listOf(
                    SampleTask(
                        title = "📧 Respond to emails",
                        description = "Clear inbox and respond to important emails",
                        categoryId = 0, // Blue
                        isImportant = false,
                        progress = 60f,
                        isDone = false,
                        priority = 2,
                        dueAtEpochMillis = baseTime + dayInMillis,
                        tags = listOf("work", "communication")
                    ),
                    SampleTask(
                        title = "🏠 Clean apartment",
                        description = "Vacuum, dust, and organize living spaces",
                        categoryId = 3, // Green
                        isImportant = false,
                        progress = 100f,
                        isDone = true,
                        priority = 1,
                        completedAtEpochMillis = baseTime - dayInMillis,
                        tags = listOf("home", "cleaning")
                    ),
                    SampleTask(
                        title = "📱 Update phone apps",
                        description = "Check for app updates and install them",
                        categoryId = 4, // Purple
                        isImportant = false,
                        progress = 0f,
                        isDone = false,
                        priority = 1,
                        tags = listOf("technology", "maintenance")
                    ),
                    SampleTask(
                        title = "🎬 Watch movie with friends",
                        description = "Movie night planned for this weekend",
                        categoryId = 2, // Pink
                        isImportant = false,
                        progress = 0f,
                        isDone = false,
                        priority = 1,
                        dueAtEpochMillis = baseTime + (5 * dayInMillis),
                        tags = listOf("entertainment", "friends")
                    ),
                    SampleTask(
                        title = "💰 Review monthly budget",
                        description = "Check expenses and plan for next month",
                        categoryId = 1, // Yellow
                        isImportant = true,
                        progress = 25f,
                        isDone = false,
                        priority = 2,
                        dueAtEpochMillis = baseTime + (10 * dayInMillis),
                        tags = listOf("finance", "planning")
                    )
                )
            }
        }
    }

    /**
     * Test data retrieval to ensure everything was saved correctly
     */
    private suspend fun testDataRetrieval(users: List<TestUser>) {
        Log.i(TAG, "📊 Testing data retrieval...")
        
        users.forEach { user ->
            try {
                // Test user data retrieval
                val userDoc = firestore.collection(COLLECTION_USERS).document(user.id).get().await()
                if (userDoc.exists()) {
                    Log.i(TAG, "✅ User data retrieved: ${user.displayName}")
                } else {
                    Log.w(TAG, "⚠️ User data not found: ${user.displayName}")
                }
                
                // Test tasks retrieval
                val tasksSnapshot = firestore
                    .collection(COLLECTION_USERS)
                    .document(user.id)
                    .collection(COLLECTION_TASKS)
                    .get()
                    .await()
                
                Log.i(TAG, "✅ Retrieved ${tasksSnapshot.documents.size} tasks for ${user.displayName}")
                
                // Log task details for verification
                tasksSnapshot.documents.forEach { doc ->
                    val task = doc.data
                    Log.d(TAG, "  📝 Task: ${task?.get("title")} (Category: ${task?.get("categoryId")}, Important: ${task?.get("isImportant")})")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to retrieve data for ${user.displayName}", e)
                throw e
            }
        }
    }

    /**
     * Clean up test data (optional)
     */
    suspend fun cleanupTestData() {
        Log.i(TAG, "🧹 Cleaning up test data...")
        
        try {
            // Delete test users and their tasks
            val testUserIds = listOf("user_premium_001", "user_free_002", "guest_user_003")
            
            testUserIds.forEach { userId ->
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
            
            Log.i(TAG, "✅ Test data cleanup completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to cleanup test data", e)
        }
    }
}

// Data classes for test data
data class TestUser(
    val id: String,
    val email: String,
    val displayName: String,
    val isPremium: Boolean = false,
    val subscriptionType: SubscriptionType = SubscriptionType.FREE,
    val isGuest: Boolean = false,
    val preferences: UserPreferences = UserPreferences()
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "email" to email,
        "displayName" to displayName,
        "isPremium" to isPremium,
        "subscriptionType" to subscriptionType.name,
        "isGuest" to isGuest,
        "preferences" to preferences.toMap(),
        "createdAtEpochMillis" to System.currentTimeMillis(),
        "updatedAtEpochMillis" to System.currentTimeMillis(),
        "version" to 1
    )
}

data class SampleTask(
    val title: String,
    val description: String = "",
    val categoryId: Int = 0,
    val isImportant: Boolean = false,
    val progress: Float = 0f,
    val isDone: Boolean = false,
    val priority: Int = 1,
    val dueAtEpochMillis: Long? = null,
    val completedAtEpochMillis: Long? = null,
    val tags: List<String> = emptyList()
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "categoryId" to categoryId,
        "isImportant" to isImportant,
        "progress" to progress,
        "isDone" to isDone,
        "priority" to priority,
        "dueAtEpochMillis" to dueAtEpochMillis,
        "completedAtEpochMillis" to completedAtEpochMillis,
        "tags" to tags,
        "createdAtEpochMillis" to System.currentTimeMillis(),
        "updatedAtEpochMillis" to System.currentTimeMillis(),
        "version" to 1
    )
}

// Extension function for UserPreferences
private fun UserPreferences.toMap(): Map<String, Any> = mapOf(
    "theme" to theme.name,
    "language" to language,
    "timezone" to timezone,
    "dateFormat" to dateFormat,
    "timeFormat" to timeFormat.name,
    "defaultReminderTime" to defaultReminderTime,
    "enableNotifications" to enableNotifications,
    "enableBiometricLock" to enableBiometricLock,
    "enableVoiceInput" to enableVoiceInput,
    "enableSmartSuggestions" to enableSmartSuggestions,
    "enableAnalytics" to enableAnalytics,
    "enableCrashReporting" to enableCrashReporting,
    "enableLocationReminders" to enableLocationReminders,
    "enableCollaboration" to enableCollaboration,
    "enableOfflineMode" to enableOfflineMode,
    "autoSync" to autoSync,
    "syncFrequency" to syncFrequency.name
)

sealed class TestResult {
    data class Success(val message: String) : TestResult()
    data class Error(val message: String) : TestResult()
}