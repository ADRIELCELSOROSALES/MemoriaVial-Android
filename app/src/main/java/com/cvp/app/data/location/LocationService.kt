package com.cvp.app.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import com.cvp.app.domain.model.UserLocation
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import timber.log.Timber

class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun observeLocation(): Flow<UserLocation> = callbackFlow {
        if (!hasLocationPermissions()) {
            close(SecurityException("Location permissions not granted — request ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION first"))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            5_000L,
        ).apply {
            setMinUpdateIntervalMillis(2_000L)
            setGranularity(Granularity.GRANULARITY_FINE)
        }.build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toUserLocation()) }
            }
        }

        runCatching {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper()).await()
        }.onFailure { e ->
            Timber.e(e, "LocationService: requestLocationUpdates failed")
            close(e)
            return@callbackFlow
        }

        awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): UserLocation? {
        if (!hasLocationPermissions()) return null
        return runCatching { fusedLocationClient.lastLocation.await()?.toUserLocation() }
            .onFailure { Timber.e(it, "LocationService: getLastKnownLocation failed") }
            .getOrNull()
    }

    private fun hasLocationPermissions(): Boolean {
        val fine = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }
}

private fun Location.toUserLocation() = UserLocation(
    latitude = latitude,
    longitude = longitude,
    timestamp = Clock.System.now(),
    speedMps = if (hasSpeed()) speed else null,
    bearingDegrees = if (hasBearing()) bearing else null,
    accuracyMeters = if (hasAccuracy()) accuracy else null,
)
