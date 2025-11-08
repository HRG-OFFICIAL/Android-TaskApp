package com.example.ui.home

import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.usecase.ClearAllTasksUseCase
import com.example.domain.usecase.DeleteTaskUseCase
import com.example.domain.usecase.ObserveTasksUseCase
import com.example.domain.usecase.SetTaskDoneUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val observeTasks: ObserveTasksUseCase = mockk()
    private val upsertTask: UpsertTaskUseCase = mockk()
    private val setTaskDone: SetTaskDoneUseCase = mockk()
    private val deleteTask: DeleteTaskUseCase = mockk()
    private val clearAllTasks: ClearAllTasksUseCase = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { observeTasks() } returns flowOf(emptyList())
        coEvery { upsertTask(any()) } returns 1
        coEvery { setTaskDone(any(), any()) } returns Unit
        coEvery { deleteTask(any()) } returns Unit
        coEvery { clearAllTasks() } returns Unit

        viewModel = HomeViewModel(
            observeTasks = observeTasks,
            upsertTask = upsertTask,
            setTaskDone = setTaskDone,
            deleteTask = deleteTask,
            clearAllTasks = clearAllTasks
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addQuickTask should call use case for valid title`() = runTest {
        viewModel.addQuickTask("Valid Task")
        advanceUntilIdle()
        
        coVerify { upsertTask(any()) }
    }

    @Test
    fun `addQuickTask should not execute for blank title`() = runTest {
        viewModel.addQuickTask("")
        viewModel.addQuickTask("   ")
        
        advanceUntilIdle()
        
        coVerify(exactly = 0) { upsertTask(any()) }
    }

    @Test
    fun `toggleDone should call use case with correct parameters`() = runTest {
        val task = Task(
            id = "1",
            title = "Test Task",
            description = "Description",
            priority = TaskPriority.HIGH,
            isDone = false
        )
        
        viewModel.toggleDone(task)
        advanceUntilIdle()
        
        coVerify { setTaskDone(task.id, !task.isDone) }
    }

    @Test
    fun `delete should call use case with correct id`() = runTest {
        val taskId = "1"
        
        viewModel.delete(taskId)
        advanceUntilIdle()
        
        coVerify { deleteTask(taskId) }
    }

    @Test
    fun `isTaskLoading should return correct state for different actions`() = runTest {
        val taskId = "1"
        
        // Initially no loading states
        assertFalse(viewModel.uiState.value.isTaskLoading(taskId, TaskAction.DELETE))
        assertFalse(viewModel.uiState.value.isTaskLoading(taskId, TaskAction.TOGGLE_COMPLETE))
        
        // Start delete operation
        viewModel.delete(taskId)
        
        // Only delete should be loading
        assertTrue(viewModel.uiState.value.isTaskLoading(taskId, TaskAction.DELETE))
        assertFalse(viewModel.uiState.value.isTaskLoading(taskId, TaskAction.TOGGLE_COMPLETE))
        
        advanceUntilIdle()
        
        // Loading should be cleared
        assertFalse(viewModel.uiState.value.isTaskLoading(taskId, TaskAction.DELETE))
    }

    @Test
    fun `quick add loading state should be independent`() = runTest {
        // Initially not loading
        assertFalse(viewModel.uiState.value.isQuickAddLoading())
        
        // Start quick add
        viewModel.addQuickTask("New Task")
        
        // Should be loading
        assertTrue(viewModel.uiState.value.isQuickAddLoading())
        
        advanceUntilIdle()
        
        // Should not be loading anymore
        assertFalse(viewModel.uiState.value.isQuickAddLoading())
    }
}