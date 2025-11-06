package com.example.core.location

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
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return
            triggeringGeofences.forEach { geofence ->
                // Handle geofence transition
                handleGeofenceTransition(geofence.requestId, geofenceTransition)
            }
        }
    }

    private fun handleGeofenceTransition(geofenceId: String, transitionType: Int) {
        val message = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered location: $geofenceId"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Left location: $geofenceId"
            else -> return
        }
        
        // TODO: Show notification for location-based reminder
        // This would require manual dependency injection or context-based notification
    }
}
