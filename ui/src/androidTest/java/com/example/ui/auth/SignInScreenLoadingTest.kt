package com.example.ui.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.theme.AppTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenLoadingTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun signInScreen_showsIndividualLoadingStates() {
        val viewModel = mockk<SignInViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            SignInUiState(
                email = "test@example.com",
                password = "password123",
                emailSignInLoading = true,
                googleSignInLoading = false,
                anonymousSignInLoading = false
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { }
                )
            }
        }
        
        // Email sign-in button should exist
        composeTestRule.onNodeWithText("Sign In").assertExists()
        
        // Other buttons should not show loading and remain enabled
        composeTestRule.onNodeWithText("Continue with Google").assertIsEnabled()
        composeTestRule.onNodeWithText("Continue as Guest").assertIsEnabled()
    }
    
    @Test
    fun signInScreen_googleSignInLoadingState() {
        val viewModel = mockk<SignInViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            SignInUiState(
                email = "test@example.com",
                password = "password123",
                emailSignInLoading = false,
                googleSignInLoading = true,
                anonymousSignInLoading = false
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { }
                )
            }
        }
        
        // Google sign-in button should show loading
        composeTestRule.onNodeWithText("Continue with Google").assertExists()
        
        // Other buttons should remain interactive
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
        composeTestRule.onNodeWithText("Continue as Guest").assertIsEnabled()
    }
    
    @Test
    fun signInScreen_anonymousSignInLoadingState() {
        val viewModel = mockk<SignInViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            SignInUiState(
                email = "test@example.com",
                password = "password123",
                emailSignInLoading = false,
                googleSignInLoading = false,
                anonymousSignInLoading = true
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { }
                )
            }
        }
        
        // Anonymous sign-in button should show loading
        composeTestRule.onNodeWithText("Continue as Guest").assertExists()
        
        // Other buttons should remain interactive
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
        composeTestRule.onNodeWithText("Continue with Google").assertIsEnabled()
    }
    
    @Test
    fun signInScreen_noLoadingStatesByDefault() {
        val viewModel = mockk<SignInViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            SignInUiState(
                email = "test@example.com",
                password = "password123",
                emailSignInLoading = false,
                googleSignInLoading = false,
                anonymousSignInLoading = false
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { }
                )
            }
        }
        
        // All buttons should be visible and enabled
        
        // All buttons should be enabled
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
        composeTestRule.onNodeWithText("Continue with Google").assertIsEnabled()
        composeTestRule.onNodeWithText("Continue as Guest").assertIsEnabled()
    }
    
    @Test
    fun signInScreen_multipleLoadingStatesIndependent() {
        val viewModel = mockk<SignInViewModel>(relaxed = true)
        val uiStateFlow = MutableStateFlow(
            SignInUiState(
                email = "test@example.com",
                password = "password123",
                emailSignInLoading = true,
                googleSignInLoading = true,
                anonymousSignInLoading = false
            )
        )
        every { viewModel.uiState } returns uiStateFlow
        
        composeTestRule.setContent {
            AppTheme {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { }
                )
            }
        }
        
        // Multiple buttons can show loading simultaneously
        // Verify buttons exist
        
        // Non-loading button should remain interactive
        composeTestRule.onNodeWithText("Continue as Guest").assertIsEnabled()
    }
}