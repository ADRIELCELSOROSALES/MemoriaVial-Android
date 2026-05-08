package com.cvp.app.domain.usecase

import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.UserLocation
import com.cvp.app.domain.util.DistanceUtils

/**
 * Returns zones within [radiusMeters] of [location].
 * When [location] has a bearing, only zones within a 180° forward cone (±90° of travel
 * direction) are returned, avoiding alerts for zones already passed.
 */
class DetectNearbyZonesUseCase {

    operator fun invoke(
        location: UserLocation,
        zones: List<RiskZone>,
        radiusMeters: Double,
    ): List<RiskZone> {
        val bearing = location.bearingDegrees?.toDouble()
        return zones.filter { zone ->
            val distance = DistanceUtils.haversineMeters(
                location.latitude, location.longitude,
                zone.latitude, zone.longitude,
            )
            if (distance > radiusMeters) return@filter false
            if (bearing == null) return@filter true
            val zoneBearing = DistanceUtils.bearingDegrees(
                location.latitude, location.longitude,
                zone.latitude, zone.longitude,
            )
            DistanceUtils.bearingDeltaDegrees(bearing, zoneBearing) <= 90.0
        }
    }
}
