package com.example.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingButtonTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loadingButton_showsTextWhenNotLoading() {
        composeTestRule.setContent {
            AppTheme {
                LoadingButton(
                    onClick = { },
                    isLoading = false
                ) {
                    Text("Sign In")
                }
            }
        }
        
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }
    
    @Test
    fun loadingButton_isDisabledWhenLoading() {
        var clickCount = 0
        
        composeTestRule.setContent {
            AppTheme {
                LoadingButton(
                    onClick = { clickCount++ },
                    isLoading = true
                ) {
                    Text("Sign In")
                }
            }
        }
        
        composeTestRule.onNodeWithText("Sign In").performClick()
        
        // Button should not respond to clicks when loading
        assert(clickCount == 0)
    }
    
    @Test
    fun loadingButton_isClickableWhenNotLoading() {
        var clickCount = 0
        
        composeTestRule.setContent {
            AppTheme {
                LoadingButton(
                    onClick = { clickCount++ },
                    isLoading = false
                ) {
                    Text("Sign In")
                }
            }
        }
        
        composeTestRule.onNodeWithText("Sign In").performClick()
        
        // Button should respond to clicks when not loading
        assert(clickCount == 1)
    }
    
    @Test
    fun loadingIconButton_showsIconWhenNotLoading() {
        composeTestRule.setContent {
            AppTheme {
                LoadingIconButton(
                    onClick = { },
                    isLoading = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
        
        composeTestRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }
    
    @Test
    fun loadingIconButton_isClickableWhenNotLoading() {
        var clickCount = 0
        
        composeTestRule.setContent {
            AppTheme {
                LoadingIconButton(
                    onClick = { clickCount++ },
                    isLoading = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
        
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        
        // Button should respond to clicks when not loading
        assert(clickCount == 1)
    }
    
    @Test
    fun loadingIconButton_isDisabledWhenLoading() {
        var clickCount = 0
        
        composeTestRule.setContent {
            AppTheme {
                LoadingIconButton(
                    onClick = { clickCount++ },
                    isLoading = true
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
        
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        
        // Button should not respond to clicks when loading
        assert(clickCount == 0)
    }
}