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
    val severity: Severity
        get() = when {
            mortalCount > 0 -> Severity.HIGH
            graveCount > 0  -> Severity.MEDIUM
            else            -> Severity.LOW
        }
}
