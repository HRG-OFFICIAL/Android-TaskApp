package com.example.core.crash

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val crashlytics = FirebaseCrashlytics.getInstance()
    
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
    
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Long) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Float) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun log(message: String) {
        crashlytics.log(message)
    }
    
    fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
    
    fun recordNonFatalException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
    
    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }
    
    fun checkForUnsentReports() {
        crashlytics.checkForUnsentReports()
    }
    
    fun sendUnsentReports() {
        crashlytics.sendUnsentReports()
    }
    
    fun deleteUnsentReports() {
        crashlytics.deleteUnsentReports()
    }
}
