package com.example.taskapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.usecase.ObserveTasksUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskFlowIntegrationTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var observeTasksUseCase: ObserveTasksUseCase
    
    @Inject
    lateinit var upsertTaskUseCase: UpsertTaskUseCase
    
    @Before
    fun init() {
        hiltRule.inject()
    }
    
    @Test
    fun createAndObserveTask_shouldWork() = runTest {
        // Given
        val task = Task(
            title = "Integration Test Task",
            description = "Test Description",
            priority = TaskPriority.HIGH,
            createdBy = "test_user"
        )
        
        // When
        val taskId = upsertTaskUseCase(task)
        val tasks = observeTasksUseCase().first()
        
        // Then
        assertTrue(taskId > 0)
        assertTrue(tasks.any { it.title == "Integration Test Task" })
    }
    
    @Test
    fun createMultipleTasks_shouldAllBeObserved() = runTest {
        // Given
        val tasks = listOf(
            Task(
                title = "Task 1",
                description = "Description 1",
                priority = TaskPriority.HIGH,
                createdBy = "test_user"
            ),
            Task(
                title = "Task 2",
                description = "Description 2",
                priority = TaskPriority.MEDIUM,
                createdBy = "test_user"
            ),
            Task(
                title = "Task 3",
                description = "Description 3",
                priority = TaskPriority.LOW,
                createdBy = "test_user"
            )
        )
        
        // When
        tasks.forEach { upsertTaskUseCase(it) }
        val observedTasks = observeTasksUseCase().first()
        
        // Then
        assertEquals(3, observedTasks.size)
        assertTrue(observedTasks.any { it.title == "Task 1" })
        assertTrue(observedTasks.any { it.title == "Task 2" })
        assertTrue(observedTasks.any { it.title == "Task 3" })
    }
}
