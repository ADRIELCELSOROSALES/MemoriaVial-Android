package com.cvp.app.domain.model

data class RiskZone(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val incidentCount: Int,
    val leveCount: Int,
    val graveCount: Int,
    val mortalCount: Int,
    val predominantHourRange: String,
    val predominantWeekday: String,
    val viaType: String,
    val predominantVictimMode: String,
    val addressLabel: String,
    val comuna: String,
    val radiusMeters: Double,
) {
    /**
     * Derived severity based on the worst-case accident type in this zone.
     * HIGH if any fatality or ≥3 severe injuries; MEDIUM for 1–2 severe injuries; LOW otherwise.
     */
    val severity: Severity
        get() = when {
            mortalCount > 0 || graveCount >= 3 -> Severity.HIGH
            graveCount in 1..2 -> Severity.MEDIUM
            else -> Severity.LOW
        }
}
