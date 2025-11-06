package com.example.core.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    
    private val _memoryInfo = MutableStateFlow(MemoryInfo())
    val memoryInfo: StateFlow<MemoryInfo> = _memoryInfo.asStateFlow()
    
    fun getMemoryInfo(): MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val debug = Debug.MemoryInfo()
        Debug.getMemoryInfo(debug)
        
        return MemoryInfo(
            totalMemory = memoryInfo.totalMem,
            availableMemory = memoryInfo.availMem,
            usedMemory = memoryInfo.totalMem - memoryInfo.availMem,
            isLowMemory = memoryInfo.lowMemory,
            threshold = memoryInfo.threshold,
            nativeHeapSize = Debug.getNativeHeapSize(),
            nativeHeapAllocated = Debug.getNativeHeapAllocatedSize(),
            nativeHeapFree = Debug.getNativeHeapFreeSize()
        )
    }
    
    fun updateMemoryInfo() {
        _memoryInfo.value = getMemoryInfo()
    }
    
    fun isLowMemory(): Boolean {
        return getMemoryInfo().isLowMemory
    }
    
    fun getMemoryUsagePercentage(): Float {
        val info = getMemoryInfo()
        return (info.usedMemory.toFloat() / info.totalMemory.toFloat()) * 100f
    }
    
    fun shouldOptimizeMemory(): Boolean {
        return getMemoryUsagePercentage() > 80f || isLowMemory()
    }
    
    fun suggestMemoryOptimization(): List<String> {
        val suggestions = mutableListOf<String>()
        val info = getMemoryInfo()
        
        if (info.isLowMemory) {
            suggestions.add("Close unused apps to free up memory")
        }
        
        if (getMemoryUsagePercentage() > 80f) {
            suggestions.add("Consider clearing app cache")
        }
        
        if (info.nativeHeapAllocated > info.nativeHeapSize * 0.8) {
            suggestions.add("Native heap usage is high - consider optimizing image loading")
        }
        
        return suggestions
    }
}

data class MemoryInfo(
    val totalMemory: Long = 0L,
    val availableMemory: Long = 0L,
    val usedMemory: Long = 0L,
    val isLowMemory: Boolean = false,
    val threshold: Long = 0L,
    val nativeHeapSize: Long = 0L,
    val nativeHeapAllocated: Long = 0L,
    val nativeHeapFree: Long = 0L
)
