package com.example.ui.auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.usecase.CreateAccountUseCase
import com.example.domain.usecase.ResetPasswordUseCase
import com.example.domain.usecase.SignInAnonymouslyUseCase
import com.example.domain.usecase.SignInWithEmailUseCase
import com.example.domain.usecase.SignInWithGoogleUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInAnonymously: SignInAnonymouslyUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val createAccount: CreateAccountUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val resetPassword: ResetPasswordUseCase,
    private val googleSignInHelper: GoogleSignInHelper,
    private val upsertTask: UpsertTaskUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun onSignInModeChanged(isSignUp: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSignUpMode = isSignUp,
            emailError = null,
            passwordError = null,
            generalError = null
        )
    }

    fun onEmailSignInClicked() {
        val currentState = _uiState.value
        if (!validateEmailAndPassword(currentState.email, currentState.password)) return

        _uiState.value = currentState.copy(emailSignInLoading = true, generalError = null)
        
        viewModelScope.launch {
            try {
                if (currentState.isSignUpMode) {
                    createAccount(currentState.email, currentState.password)
                } else {
                    signInWithEmail(currentState.email, currentState.password)
                }
                _uiState.value = _uiState.value.copy(emailSignInLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    emailSignInLoading = false,
                    generalError = e.message ?: "Authentication failed"
                )
            }
        }
    }

    fun onGoogleSignInClicked(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        _uiState.value = _uiState.value.copy(googleSignInLoading = true, generalError = null)
        
        viewModelScope.launch {
            try {
                val intentSenderRequest = googleSignInHelper.beginSignIn()
                if (intentSenderRequest != null) {
                    launcher.launch(intentSenderRequest)
                } else {
                    _uiState.value = _uiState.value.copy(
                        googleSignInLoading = false,
                        generalError = "Google Sign-In not available"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    googleSignInLoading = false,
                    generalError = e.message ?: "Failed to start Google sign-in"
                )
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val idToken = data?.let { googleSignInHelper.getSignInCredentialFromIntent(it) }
                if (idToken != null) {
                    signInWithGoogle(idToken)
                    _uiState.value = _uiState.value.copy(googleSignInLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        googleSignInLoading = false,
                        generalError = "Google sign-in was cancelled"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    googleSignInLoading = false,
                    generalError = e.message ?: "Google sign-in failed"
                )
            }
        }
    }

    fun onAnonymousSignInClicked() {
        _uiState.value = _uiState.value.copy(anonymousSignInLoading = true, generalError = null)
        
        viewModelScope.launch {
            try {
                signInAnonymously()
                _uiState.value = _uiState.value.copy(anonymousSignInLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    anonymousSignInLoading = false,
                    generalError = e.message ?: "Anonymous sign-in failed"
                )
            }
        }
    }

    fun onResetPasswordClicked() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Enter your email to reset password")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Invalid email format")
            return
        }

        _uiState.value = _uiState.value.copy(resetPasswordLoading = true, generalError = null)
        
        viewModelScope.launch {
            try {
                resetPassword(email)
                _uiState.value = _uiState.value.copy(
                    resetPasswordLoading = false,
                    showResetPasswordSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    resetPasswordLoading = false,
                    generalError = e.message ?: "Failed to send reset email"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(generalError = null)
    }

    fun clearResetPasswordSuccess() {
        _uiState.value = _uiState.value.copy(showResetPasswordSuccess = false)
    }

    private fun validateEmailAndPassword(email: String, password: String): Boolean {
        var isValid = true
        val currentState = _uiState.value

        if (email.isBlank()) {
            _uiState.value = currentState.copy(emailError = "Email is required")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = currentState.copy(emailError = "Invalid email format")
            isValid = false
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Password is required")
            isValid = false
        } else if (password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "Password must be at least 6 characters")
            isValid = false
        }

        return isValid
    }
}

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isSignUpMode: Boolean = false,
    val emailSignInLoading: Boolean = false,
    val googleSignInLoading: Boolean = false,
    val anonymousSignInLoading: Boolean = false,
    val resetPasswordLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val showResetPasswordSuccess: Boolean = false,
)
