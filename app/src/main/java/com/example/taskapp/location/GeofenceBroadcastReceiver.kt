package com.example.taskapp.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geofencingEvent.hasError()) {
            return
        }
        
        val geofenceTransition = geofencingEvent.geofenceTransition
        
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return
            
            for (geofence in triggeringGeofences) {
                val taskId = geofence.requestId.removePrefix("task_")
                showLocationReminder(context, taskId)
            }
        }
    }
    
    private fun showLocationReminder(context: Context, taskId: String) {
        // This would typically show a notification or update the UI
        // For now, we'll just log the event
        android.util.Log.d("GeofenceReceiver", "User entered geofence for task: $taskId")
    }
}
