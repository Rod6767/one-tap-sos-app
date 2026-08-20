package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationInfo(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val address: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isLocating: Boolean = false,
    val isGpsEnabled: Boolean = false,
    val hasPermission: Boolean = false,
    val isManualFallback: Boolean = false,
    val errorMessage: String? = null
) {
    val mapsUrl: String
        get() = if (latitude != null && longitude != null) {
            "https://maps.google.com/?q=${"%.6f".format(Locale.US, latitude)},${"%.6f".format(Locale.US, longitude)}"
        } else {
            "https://maps.google.com"
        }

    val sosMessageBody: String
        get() {
            val coords = if (latitude != null && longitude != null) {
                "GPS: ${"%.5f".format(Locale.US, latitude)}, ${"%.5f".format(Locale.US, longitude)} (±${accuracy?.toInt() ?: 10}m)"
            } else {
                "Location: ${address ?: "Current location"}"
            }
            val addrText = if (!address.isNullOrBlank() && !isManualFallback) "\nNear: $address" else if (isManualFallback) "\nNote: $address" else ""
            return "EMERGENCY! I need immediate help!\n$coords$addrText\nMap: $mapsUrl"
        }
}

class EmergencyLocationProvider(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _locationState = MutableStateFlow(LocationInfo())
    val locationState: StateFlow<LocationInfo> = _locationState.asStateFlow()

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateWithLocation(location)
        }

        override fun onProviderEnabled(provider: String) {
            checkGpsStatus()
        }

        override fun onProviderDisabled(provider: String) {
            checkGpsStatus()
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    private var isListening = false

    fun checkGpsStatus(): Boolean {
        val isGpsOn = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                      locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        _locationState.value = _locationState.value.copy(isGpsEnabled = isGpsOn)
        return isGpsOn
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(hasPerm: Boolean) {
        _locationState.value = _locationState.value.copy(
            hasPermission = hasPerm,
            isLocating = hasPerm
        )
        if (!hasPerm || locationManager == null) return

        checkGpsStatus()

        try {
            // Get last known location immediately
            var bestLastLocation: Location? = null
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            for (p in providers) {
                if (locationManager.isProviderEnabled(p)) {
                    val loc = locationManager.getLastKnownLocation(p)
                    if (loc != null) {
                        if (bestLastLocation == null || loc.accuracy < bestLastLocation.accuracy || loc.time > bestLastLocation.time) {
                            bestLastLocation = loc
                        }
                    }
                }
            }

            bestLastLocation?.let {
                updateWithLocation(it)
            }

            if (!isListening) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L,
                        1f,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L,
                        1f,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }
                isListening = true
            }
        } catch (e: Exception) {
            Log.e("EmergencyLocation", "Error starting updates: ${e.message}")
            _locationState.value = _locationState.value.copy(
                isLocating = false,
                errorMessage = "Location service error: ${e.localizedMessage}"
            )
        }
    }

    fun stopLocationUpdates() {
        if (isListening && locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener)
                isListening = false
            } catch (e: Exception) {
                // Ignore
            }
        }
        _locationState.value = _locationState.value.copy(isLocating = false)
    }

    private fun updateWithLocation(location: Location) {
        _locationState.value = _locationState.value.copy(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            altitude = if (location.hasAltitude()) location.altitude else null,
            speed = if (location.hasSpeed()) location.speed else null,
            timestamp = location.time,
            isLocating = false,
            isManualFallback = false,
            errorMessage = null
        )

        // Asynchronously reverse geocode address
        scope.launch {
            val addr = fetchAddress(location.latitude, location.longitude)
            if (addr != null) {
                _locationState.value = _locationState.value.copy(address = addr)
            }
        }
    }

    fun setManualLocation(manualText: String, lat: Double? = null, lng: Double? = null) {
        _locationState.value = _locationState.value.copy(
            latitude = lat ?: 37.7749, // default fallback coordinates if strictly manual
            longitude = lng ?: -122.4194,
            accuracy = 50f,
            address = manualText,
            isManualFallback = true,
            errorMessage = null
        )
    }

    private suspend fun fetchAddress(lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext null
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                formatAddress(addresses?.firstOrNull())
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                formatAddress(addresses?.firstOrNull())
            }
        } catch (e: Exception) {
            Log.e("EmergencyLocation", "Geocoder error: ${e.message}")
            null
        }
    }

    private fun formatAddress(address: Address?): String? {
        if (address == null) return null
        val parts = mutableListOf<String>()
        address.thoroughfare?.let { parts.add(it) }
        address.subLocality?.let { parts.add(it) }
        address.locality?.let { parts.add(it) }
        address.adminArea?.let { parts.add(it) }
        address.countryName?.let { parts.add(it) }
        return if (parts.isNotEmpty()) parts.joinToString(", ") else address.getAddressLine(0)
    }
}
