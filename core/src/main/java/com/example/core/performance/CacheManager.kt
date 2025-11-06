package com.example.core.performance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val memoryCache: LruCache<String, Bitmap>
    private val diskCacheDir: File
    
    init {
        // Calculate cache size (1/8 of available memory)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
        
        diskCacheDir = File(context.cacheDir, "images")
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
    }
    
    suspend fun getBitmap(key: String): Bitmap? = withContext(Dispatchers.IO) {
        // Check memory cache first
        memoryCache.get(key) ?: run {
            // Check disk cache
            val file = File(diskCacheDir, key)
            if (file.exists()) {
                try {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        memoryCache.put(key, bitmap)
                    }
                    bitmap
                } catch (e: Exception) {
                    null
                }
            } else null
        }
    }
    
    suspend fun putBitmap(key: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        // Store in memory cache
        memoryCache.put(key, bitmap)
        
        // Store in disk cache
        val file = File(diskCacheDir, key)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
    
    suspend fun clearDiskCache() = withContext(Dispatchers.IO) {
        diskCacheDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }
    
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        clearMemoryCache()
        clearDiskCache()
    }
    
    fun getCacheSize(): Long {
        return diskCacheDir.walkTopDown().sumOf { it.length() }
    }
    
    fun getMemoryCacheSize(): Int {
        return memoryCache.size()
    }
    
    fun getMemoryCacheMaxSize(): Int {
        return memoryCache.maxSize()
    }
}
