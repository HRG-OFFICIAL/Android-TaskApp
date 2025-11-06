package com.example.data.remote

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreListenerManager @Inject constructor() {
    
    companion object {
        private const val TAG = "FirestoreListenerManager"
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val INITIAL_RECONNECT_DELAY = 1000L
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeListeners = ConcurrentHashMap<String, ListenerInfo>()
    
    data class ListenerInfo(
        val registration: ListenerRegistration,
        val reconnectCallback: () -> ListenerRegistration,
        var reconnectAttempts: Int = 0,
        var isActive: Boolean = true
    )
    
    fun registerListener(
        listenerId: String,
        registration: ListenerRegistration,
        reconnectCallback: () -> ListenerRegistration
    ) {
        Log.d(TAG, "Registering listener: $listenerId")
        
        // Remove existing listener if any
        removeListener(listenerId)
        
        val listenerInfo = ListenerInfo(
            registration = registration,
            reconnectCallback = reconnectCallback
        )
        
        activeListeners[listenerId] = listenerInfo
    }
    
    fun removeListener(listenerId: String) {
        activeListeners[listenerId]?.let { listenerInfo ->
            Log.d(TAG, "Removing listener: $listenerId")
            listenerInfo.isActive = false
            listenerInfo.registration.remove()
            activeListeners.remove(listenerId)
        }
    }
    
    fun removeAllListeners() {
        Log.d(TAG, "Removing all listeners (${activeListeners.size})")
        activeListeners.values.forEach { listenerInfo ->
            listenerInfo.isActive = false
            listenerInfo.registration.remove()
        }
        activeListeners.clear()
    }
    
    fun handleListenerError(listenerId: String, error: Exception) {
        val listenerInfo = activeListeners[listenerId] ?: return
        
        if (!listenerInfo.isActive) {
            Log.d(TAG, "Listener $listenerId is no longer active, skipping reconnection")
            return
        }
        
        Log.w(TAG, "Listener error for $listenerId, attempting reconnection", error)
        
        scope.launch {
            attemptReconnection(listenerId, listenerInfo)
        }
    }
    
    private suspend fun attemptReconnection(listenerId: String, listenerInfo: ListenerInfo) {
        if (listenerInfo.reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnection attempts reached for listener: $listenerId")
            removeListener(listenerId)
            return
        }
        
        listenerInfo.reconnectAttempts++
        val delay = INITIAL_RECONNECT_DELAY * (1L shl (listenerInfo.reconnectAttempts - 1))
        
        Log.d(TAG, "Reconnecting listener $listenerId (attempt ${listenerInfo.reconnectAttempts}/$MAX_RECONNECT_ATTEMPTS) in ${delay}ms")
        delay(delay)
        
        if (!listenerInfo.isActive) {
            Log.d(TAG, "Listener $listenerId is no longer active, cancelling reconnection")
            return
        }
        
        try {
            val newRegistration = listenerInfo.reconnectCallback()
            
            // Update the listener info with new registration
            val updatedInfo = listenerInfo.copy(
                registration = newRegistration,
                reconnectAttempts = 0 // Reset attempts on successful reconnection
            )
            activeListeners[listenerId] = updatedInfo
            
            Log.d(TAG, "Successfully reconnected listener: $listenerId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reconnect listener: $listenerId", e)
            
            // Schedule another reconnection attempt
            scope.launch {
                attemptReconnection(listenerId, listenerInfo)
            }
        }
    }
    
    fun getActiveListenerCount(): Int = activeListeners.size
    
    fun isListenerActive(listenerId: String): Boolean {
        return activeListeners[listenerId]?.isActive == true
    }
}