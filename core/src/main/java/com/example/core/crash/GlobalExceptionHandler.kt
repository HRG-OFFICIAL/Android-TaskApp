package com.example.core.crash

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalExceptionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val crashReporter: CrashReporter
) {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        scope.launch {
            handleException(throwable)
        }
    }
    
    fun handleException(throwable: Throwable) {
        // Log to Crashlytics
        crashReporter.recordException(throwable)
        
        // Log additional context
        crashReporter.log("Exception occurred: ${throwable.message}")
        crashReporter.setCustomKey("exception_type", throwable.javaClass.simpleName)
        crashReporter.setCustomKey("exception_message", throwable.message ?: "Unknown error")
        
        // Log stack trace
        val stackTrace = throwable.stackTraceToString()
        crashReporter.log("Stack trace: $stackTrace")
    }
    
    fun handleNonFatalException(throwable: Throwable, context: String = "") {
        crashReporter.setCustomKey("error_context", context)
        crashReporter.recordNonFatalException(throwable)
    }
    
    fun setUserContext(userId: String, isPremium: Boolean, version: String) {
        crashReporter.setUserId(userId)
        crashReporter.setCustomKey("is_premium", isPremium)
        crashReporter.setCustomKey("app_version", version)
    }
}
