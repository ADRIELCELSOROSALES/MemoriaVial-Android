package com.cvp.app.domain.util

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceUtils {

    private const val EARTH_RADIUS_METERS = 6_371_000.0

    /**
     * Returns the great-circle distance in metres between two points on Earth
     * using the Haversine formula.
     */
    fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val sinDLat = sin(dLat / 2)
        val sinDLon = sin(dLon / 2)
        val a = sinDLat * sinDLat +
            cos(lat1.toRadians()) * cos(lat2.toRadians()) * sinDLon * sinDLon
        return EARTH_RADIUS_METERS * 2.0 * asin(sqrt(a))
    }

    /**
     * Returns the initial bearing in degrees (0–360, clockwise from north) from
     * (lat1, lon1) to (lat2, lon2).
     */
    fun bearingDegrees(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLon = (lon2 - lon1).toRadians()
        val lat1Rad = lat1.toRadians()
        val lat2Rad = lat2.toRadians()
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        return (atan2(y, x).toDegrees() + 360.0) % 360.0
    }

    /**
     * Returns the angular difference [0, 180] between two bearings in degrees.
     */
    fun bearingDeltaDegrees(bearing1: Double, bearing2: Double): Double {
        val delta = abs(bearing1 - bearing2) % 360.0
        return if (delta > 180.0) 360.0 - delta else delta
    }

    private fun Double.toRadians() = this * PI / 180.0
    private fun Double.toDegrees() = this * 180.0 / PI
}
