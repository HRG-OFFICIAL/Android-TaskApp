package com.example.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LoadingButton
import com.example.ui.components.LoadingOutlinedButton
import com.example.ui.components.LoadingTextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand badge
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 2.dp
            ) {
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // App Title
            Text(
                text = "TaskApp",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.isSignUpMode) "Create your account" else "Sign in to sync your tasks",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

        // Email/Password Form
        var isFormFocused by remember { mutableStateOf(false) }
        val animatedElevation by animateDpAsState(targetValue = if (isFormFocused) 6.dp else 2.dp, label = "formElevation")

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = animatedElevation)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode toggle chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val signInSelectedColor by animateColorAsState(
                        targetValue = if (!uiState.isSignUpMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        label = "signInChipColor"
                    )
                    val signUpSelectedColor by animateColorAsState(
                        targetValue = if (uiState.isSignUpMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                        label = "signUpChipColor"
                    )
                    FilterChip(
                        selected = !uiState.isSignUpMode,
                        onClick = { viewModel.onSignInModeChanged(false) },
                        label = { Text("Sign In") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = signInSelectedColor,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = uiState.isSignUpMode,
                        onClick = { viewModel.onSignInModeChanged(true) },
                        label = { Text("Sign Up") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = signUpSelectedColor,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }

                // Email Field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFormFocused = it.isFocused },
                    shape = MaterialTheme.shapes.large,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = uiState.emailError != null,
                    supportingText = uiState.emailError?.let { { Text(it) } },
                    enabled = !uiState.emailSignInLoading && !uiState.googleSignInLoading && !uiState.anonymousSignInLoading
                )

                // Password Field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFormFocused = it.isFocused },
                    shape = MaterialTheme.shapes.large,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            focusManager.clearFocus()
                            viewModel.onEmailSignInClicked()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = uiState.passwordError != null,
                    supportingText = uiState.passwordError?.let { { Text(it) } },
                    enabled = !uiState.emailSignInLoading && !uiState.googleSignInLoading && !uiState.anonymousSignInLoading
                )

                // Sign In/Up Button
                LoadingButton(
                    onClick = viewModel::onEmailSignInClicked,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.emailSignInLoading,
                    enabled = !uiState.googleSignInLoading && !uiState.anonymousSignInLoading
                ) {
                    Text(if (uiState.isSignUpMode) "Create Account" else "Sign In")
                }

                // Forgot Password (only in sign-in mode)
                if (!uiState.isSignUpMode) {
                    LoadingTextButton(
                        onClick = viewModel::onResetPasswordClicked,
                        isLoading = uiState.resetPasswordLoading,
                        enabled = !uiState.emailSignInLoading && !uiState.googleSignInLoading && !uiState.anonymousSignInLoading
                    ) {
                        Text("Forgot Password?")
                    }
                }

                // Toggle Sign In/Up Mode
                LoadingTextButton(
                    onClick = { viewModel.onSignInModeChanged(!uiState.isSignUpMode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.emailSignInLoading && !uiState.googleSignInLoading && !uiState.anonymousSignInLoading
                ) {
                    Text(
                        if (uiState.isSignUpMode) "Already have an account? Sign In" 
                        else "Don't have an account? Sign Up"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Google Sign In Button (Filled Tonal with 'G' badge)
        LoadingButton(
            onClick = { 
                viewModel.onGoogleSignInClicked(googleSignInLauncher)
            },
            modifier = Modifier.fillMaxWidth(),
            isLoading = uiState.googleSignInLoading,
            enabled = !uiState.emailSignInLoading && !uiState.anonymousSignInLoading,
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("G", color = Color(0xFF4285F4), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Anonymous Sign In Button
        LoadingTextButton(
            onClick = viewModel::onAnonymousSignInClicked,
            modifier = Modifier.fillMaxWidth(),
            isLoading = uiState.anonymousSignInLoading,
            enabled = !uiState.emailSignInLoading && !uiState.googleSignInLoading
        ) {
            Text("Continue as Guest")
        }

        // Terms and Privacy footer
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { /* TODO: open Terms URL */ }) { Text("Terms") }
            Text(" · ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = { /* TODO: open Privacy URL */ }) { Text("Privacy") }
        }

        // Success Message for Password Reset
        if (uiState.showResetPasswordSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Password reset email sent!",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Check your email for instructions to reset your password.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Error Message
        uiState.generalError?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        }
    }
}
