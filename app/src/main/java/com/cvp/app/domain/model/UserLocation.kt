package com.cvp.app.domain.model

import kotlinx.datetime.Instant

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Instant,
    val speedMps: Float?,
    val bearingDegrees: Float?,
    val accuracyMeters: Float?,
)
