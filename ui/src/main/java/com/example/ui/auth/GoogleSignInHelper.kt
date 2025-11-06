package com.example.ui.auth

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.example.ui.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private fun createSignInRequest(): BeginSignInRequest {
        val serverId = BuildConfig.GOOGLE_SERVER_CLIENT_ID
        val optionsBuilder = BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setFilterByAuthorizedAccounts(false)

        if (serverId.isNotBlank()) {
            // Use server-side client ID only if provided via BuildConfig
            optionsBuilder.setServerClientId(serverId)
        }

        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(optionsBuilder.build())
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun beginSignIn(): IntentSenderRequest? {
        return try {
            val result = oneTapClient.beginSignIn(createSignInRequest()).await()
            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
        } catch (e: ApiException) {
            // No saved credentials found. Launch the One Tap sign-up flow, or
            // do nothing and continue presenting the signed-out UI.
            null
        }
    }

    suspend fun getSignInCredentialFromIntent(data: android.content.Intent): String? {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            credential.googleIdToken
        } catch (e: ApiException) {
            null
        }
    }
}