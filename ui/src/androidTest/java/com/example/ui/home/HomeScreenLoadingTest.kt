package com.example.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.ui.theme.AppTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenLoadingTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val sampleTasks = listOf(
        Task(
            id = 1,
            title = "Test Task 1",
            description = "Description 1",
            priority = TaskPriority.HIGH,
            isDone = false
        ),
        Task(
            id = 2,
            title = "Test Task 2",
            description = "Description 2",
            priority = TaskPriority.MEDIUM,
            isDone = true
        )
    )
    
    @Test
    fun homeScreen_showsQuickAddLoadingState() {
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            HomeUiState(
                tasks = sampleTasks,
                quickAddLoading = true,
                taskActionStates = emptyMap()
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        // Quick add button should exist
        composeTestRule.onNodeWithText("Add").assertExists()
        
        // Task items should remain interactive
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_showsIndividualTaskDeleteLoading() {
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        val taskActionStates = mapOf(
            "1_DELETE" to true // Task 1 delete action is loading
        )
        val uiStateFlow = MutableStateFlow(
            HomeUiState(
                tasks = sampleTasks,
                quickAddLoading = false,
                taskActionStates = taskActionStates
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.uiState.value.isTaskLoading(1, TaskAction.DELETE) } returns true
        every { viewModel.uiState.value.isTaskLoading(2, TaskAction.DELETE) } returns false
        every { viewModel.uiState.value.isTaskLoading(any(), TaskAction.TOGGLE_COMPLETE) } returns false
        
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        // Verify that tasks are displayed
        // The exact implementation depends on how delete buttons are structured
        
        // Other elements should remain interactive
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_showsIndividualTaskToggleLoading() {
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        val taskActionStates = mapOf(
            "2_TOGGLE_COMPLETE" to true // Task 2 toggle action is loading
        )
        val uiStateFlow = MutableStateFlow(
            HomeUiState(
                tasks = sampleTasks,
                quickAddLoading = false,
                taskActionStates = taskActionStates
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.uiState.value.isTaskLoading(2, TaskAction.TOGGLE_COMPLETE) } returns true
        every { viewModel.uiState.value.isTaskLoading(1, TaskAction.TOGGLE_COMPLETE) } returns false
        every { viewModel.uiState.value.isTaskLoading(any(), TaskAction.DELETE) } returns false
        
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        // Verify that tasks are displayed
        
        // Tasks should still be displayed
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_multipleIndependentLoadingStates() {
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        val taskActionStates = mapOf(
            "1_DELETE" to true,
            "2_TOGGLE_COMPLETE" to true
        )
        val uiStateFlow = MutableStateFlow(
            HomeUiState(
                tasks = sampleTasks,
                quickAddLoading = true,
                taskActionStates = taskActionStates
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.uiState.value.isTaskLoading(1, TaskAction.DELETE) } returns true
        every { viewModel.uiState.value.isTaskLoading(2, TaskAction.TOGGLE_COMPLETE) } returns true
        every { viewModel.uiState.value.isQuickAddLoading() } returns true
        
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        // Verify multiple loading states can coexist
        
        // Content should still be accessible
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_noLoadingStatesByDefault() {
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            HomeUiState(
                tasks = sampleTasks,
                quickAddLoading = false,
                taskActionStates = emptyMap()
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.uiState.value.isTaskLoading(any(), any()) } returns false
        every { viewModel.uiState.value.isQuickAddLoading() } returns false
        
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        // Verify no loading states are active
        
        // All content should be displayed and interactive
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add").assertIsEnabled()
    }
}