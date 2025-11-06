package com.example.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager as AndroidLocationManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val androidLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    private val _isLocationEnabled = MutableStateFlow(isLocationEnabled())
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun isLocationEnabled(): Boolean {
        return androidLocationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) ||
                androidLocationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER)
    }
    
    fun startLocationUpdates() {
        if (!hasLocationPermission() || !isLocationEnabled()) return
        
        try {
            androidLocationManager.requestLocationUpdates(
                AndroidLocationManager.GPS_PROVIDER,
                10000L, // 10 seconds
                10f, // 10 meters
                locationListener
            )
            
            androidLocationManager.requestLocationUpdates(
                AndroidLocationManager.NETWORK_PROVIDER,
                10000L,
                10f,
                locationListener
            )
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }
    
    fun stopLocationUpdates() {
        androidLocationManager.removeUpdates(locationListener)
    }
    
    fun getLastKnownLocation(): Location? {
        return if (hasLocationPermission()) {
            androidLocationManager.getLastKnownLocation(AndroidLocationManager.GPS_PROVIDER)
                ?: androidLocationManager.getLastKnownLocation(AndroidLocationManager.NETWORK_PROVIDER)
        } else null
    }
    
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    private val locationListener = LocationListener { location ->
        _currentLocation.value = location
    }
}
