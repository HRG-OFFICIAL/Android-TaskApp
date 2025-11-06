package com.example.ui.calendar

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
class CalendarScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun calendarScreen_displaysCalendarView() {
        // Given
        val mockTasks = listOf(
            Task(
                id = 1,
                title = "Test Task 1",
                description = "Test Description 1",
                priority = TaskPriority.HIGH
            )
        )
        
        // When
        composeTestRule.setContent {
            CalendarScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calendar View").assertIsDisplayed()
    }
    
    @Test
    fun calendarScreen_displaysFilterAndAddButtons() {
        // When
        composeTestRule.setContent {
            CalendarScreen()
        }
        
        // Then
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Task").assertIsDisplayed()
    }
    
    @Test
    fun calendarScreen_displaysTasksForSelectedDate() {
        // Given
        val mockTasks = listOf(
            Task(
                id = 2,
                title = "Test Task 1",
                description = "Test Description 1",
                priority = TaskPriority.HIGH
            )
        )
        
        // When
        composeTestRule.setContent {
            CalendarScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Tasks for").assertIsDisplayed()
    }
}
