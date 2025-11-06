package com.example.ui.auth

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.domain.model.AuthUser
import com.example.domain.usecase.CreateAccountUseCase
import com.example.domain.usecase.ResetPasswordUseCase
import com.example.domain.usecase.SignInAnonymouslyUseCase
import com.example.domain.usecase.SignInWithEmailUseCase
import com.example.domain.usecase.SignInWithGoogleUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private lateinit var viewModel: SignInViewModel
    private val signInAnonymously: SignInAnonymouslyUseCase = mockk()
    private val signInWithEmail: SignInWithEmailUseCase = mockk()
    private val createAccount: CreateAccountUseCase = mockk()
    private val signInWithGoogle: SignInWithGoogleUseCase = mockk()
    private val resetPassword: ResetPasswordUseCase = mockk()
    private val googleSignInHelper: GoogleSignInHelper = mockk()
    private val upsertTask: UpsertTaskUseCase = mockk()
    private val launcher: ActivityResultLauncher<IntentSenderRequest> = mockk(relaxed = true)
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val mockAuthUser = AuthUser(uid = "test_uid", email = "test@example.com")
        
        coEvery { signInAnonymously() } returns mockAuthUser
        coEvery { signInWithEmail(any(), any()) } returns mockAuthUser
        coEvery { createAccount(any(), any()) } returns mockAuthUser
        coEvery { signInWithGoogle(any()) } returns mockAuthUser
        coEvery { resetPassword(any()) } returns Unit
        coEvery { upsertTask(any()) } returns 1
        coEvery { googleSignInHelper.beginSignIn() } returns mockk()
        coEvery { googleSignInHelper.getSignInCredentialFromIntent(any()) } returns "test_token"

        viewModel = SignInViewModel(
            signInAnonymously = signInAnonymously,
            signInWithEmail = signInWithEmail,
            createAccount = createAccount,
            signInWithGoogle = signInWithGoogle,
            resetPassword = resetPassword,
            googleSignInHelper = googleSignInHelper,
            upsertTask = upsertTask
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have no loading states active`() {
        val uiState = viewModel.uiState.value
        
        assertFalse(uiState.emailSignInLoading)
        assertFalse(uiState.googleSignInLoading)
        assertFalse(uiState.anonymousSignInLoading)
        assertFalse(uiState.resetPasswordLoading)
        assertEquals("", uiState.email)
        assertEquals("", uiState.password)
        assertFalse(uiState.isSignUpMode)
    }

    @Test
    fun `onEmailSignInClicked should call correct use case`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        
        viewModel.onEmailSignInClicked()
        advanceUntilIdle()
        
        coVerify { signInWithEmail("test@example.com", "password123") }
    }

    @Test
    fun `onEmailSignInClicked in signup mode should call createAccount`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onSignInModeChanged(true)
        
        viewModel.onEmailSignInClicked()
        advanceUntilIdle()
        
        coVerify { createAccount("test@example.com", "password123") }
        coVerify(exactly = 0) { signInWithEmail(any(), any()) }
    }

    @Test
    fun `onAnonymousSignInClicked should call use case and create demo tasks`() = runTest {
        viewModel.onAnonymousSignInClicked()
        advanceUntilIdle()
        
        coVerify { signInAnonymously() }
        coVerify(atLeast = 1) { upsertTask(any()) }
    }

    @Test
    fun `onResetPasswordClicked should call use case with valid email`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        
        viewModel.onResetPasswordClicked()
        advanceUntilIdle()
        
        coVerify { resetPassword("test@example.com") }
    }

    @Test
    fun `email validation should prevent sign-in with invalid email`() = runTest {
        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("password123")
        
        viewModel.onEmailSignInClicked()
        advanceUntilIdle()
        
        assertEquals("Invalid email format", viewModel.uiState.value.emailError)
        coVerify(exactly = 0) { signInWithEmail(any(), any()) }
    }

    @Test
    fun `password validation should prevent sign-in with short password`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("123")
        
        viewModel.onEmailSignInClicked()
        advanceUntilIdle()
        
        assertEquals("Password must be at least 6 characters", viewModel.uiState.value.passwordError)
        coVerify(exactly = 0) { signInWithEmail(any(), any()) }
    }

    @Test
    fun `loading states should be independent for different sign-in methods`() = runTest {
        // Test that each loading state is independent
        assertFalse(viewModel.uiState.value.emailSignInLoading)
        assertFalse(viewModel.uiState.value.googleSignInLoading)
        assertFalse(viewModel.uiState.value.anonymousSignInLoading)
        assertFalse(viewModel.uiState.value.resetPasswordLoading)
        
        // Start anonymous sign-in
        viewModel.onAnonymousSignInClicked()
        
        // Only anonymous should be loading
        assertTrue(viewModel.uiState.value.anonymousSignInLoading)
        assertFalse(viewModel.uiState.value.emailSignInLoading)
        assertFalse(viewModel.uiState.value.googleSignInLoading)
        assertFalse(viewModel.uiState.value.resetPasswordLoading)
        
        advanceUntilIdle()
        
        // All should be false after completion
        assertFalse(viewModel.uiState.value.anonymousSignInLoading)
        assertFalse(viewModel.uiState.value.emailSignInLoading)
        assertFalse(viewModel.uiState.value.googleSignInLoading)
        assertFalse(viewModel.uiState.value.resetPasswordLoading)
    }
}