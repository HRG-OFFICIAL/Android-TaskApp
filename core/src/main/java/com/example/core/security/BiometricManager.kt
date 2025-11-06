package com.example.core.security

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class BiometricManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun isBiometricAvailable(): Boolean {
        val biometricManager = AndroidBiometricManager.from(context)
        return biometricManager.canAuthenticate(AndroidBiometricManager.Authenticators.BIOMETRIC_WEAK) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }
    
    fun isBiometricEnrolled(): Boolean {
        val biometricManager = AndroidBiometricManager.from(context)
        return biometricManager.canAuthenticate(AndroidBiometricManager.Authenticators.BIOMETRIC_WEAK) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }
    
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Biometric Authentication",
        subtitle: String = "Use your biometric to access the app",
        negativeButtonText: String = "Cancel"
    ): BiometricResult {
        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    continuation.resume(BiometricResult.Error(errString.toString()))
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    continuation.resume(BiometricResult.Success)
                }
                
                override fun onAuthenticationFailed() {
                    continuation.resume(BiometricResult.Failed)
                }
            })
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .build()
            
            biometricPrompt.authenticate(promptInfo)
            
            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
        }
    }
}

sealed class BiometricResult {
    object Success : BiometricResult()
    object Failed : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}
