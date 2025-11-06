package com.example.ui

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun composeWorks() {
        composeRule.setContent {
            // simple smoke render using a composable
        }
    }
}
