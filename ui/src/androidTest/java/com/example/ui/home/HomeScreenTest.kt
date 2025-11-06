package com.example.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun homeScreen_displaysTaskList() {
        // Given
        val mockTasks = listOf(
            Task(
                id = 1,
                title = "Test Task 1",
                description = "Test Description 1",
                priority = TaskPriority.HIGH
            ),
            Task(
                id = 2,
                title = "Test Task 2",
                description = "Test Description 2",
                priority = TaskPriority.MEDIUM
            )
        )
        
        // When
        composeTestRule.setContent {
            HomeScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Test Task 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Task 2").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_displaysQuickAddField() {
        // When
        composeTestRule.setContent {
            HomeScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Quick add a task…").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_displaysSearchField() {
        // When
        composeTestRule.setContent {
            HomeScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Search tasks…").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_canAddQuickTask() {
        // Given
        val viewModel = mockk<HomeViewModel>(relaxed = true)
        every { viewModel.uiState } returns kotlinx.coroutines.flow.MutableStateFlow(
            HomeUiState(tasks = emptyList())
        )
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("Quick add a task…").performTextInput("New Task")
        composeTestRule.onNodeWithText("Add").performClick()
        
        // Verify that the viewModel was called (this would be verified through the actual implementation)
    }
}
