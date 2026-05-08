package com.cvp.app.domain.model

import kotlinx.datetime.Instant

data class ProximityEvent(
    val zone: RiskZone,
    val distanceMeters: Double,
    val timestamp: Instant,
)
