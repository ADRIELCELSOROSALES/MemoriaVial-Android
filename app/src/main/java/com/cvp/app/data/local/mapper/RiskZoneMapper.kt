package com.cvp.app.data.local.mapper

import com.cvp.app.data.local.dto.GeoJsonFeatureDto
import com.cvp.app.domain.model.RiskZone
import timber.log.Timber

object RiskZoneMapper {

    // Generous CABA bounding box — catches all boroughs with a small margin.
    private val LONGITUDE_RANGE = -58.70..-58.20
    private val LATITUDE_RANGE = -34.80..-34.50

    /**
     * Maps a GeoJSON feature DTO to [RiskZone].
     * Returns null if coordinates are missing, have the wrong size, or fall outside CABA.
     * GeoJSON coordinate order is [longitude, latitude] — this function handles the swap.
     * Nullable or "NA" string properties get sensible defaults so the zone is still usable.
     */
    fun fromDto(feature: GeoJsonFeatureDto): RiskZone? {
        val coords = feature.geometry.coordinates
        val id = feature.properties.id

        if (coords.size < 2) {
            Timber.w("Skipping $id: expected ≥2 coordinates, got ${coords.size}")
            return null
        }

        val longitude = coords[0]
        val latitude = coords[1]

        if (longitude !in LONGITUDE_RANGE || latitude !in LATITUDE_RANGE) {
            Timber.w("Skipping $id: coordinates ($longitude, $latitude) outside CABA bounds")
            return null
        }

        val p = feature.properties
        return RiskZone(
            id = p.id,
            latitude = latitude,
            longitude = longitude,
            incidentCount = p.incidentCount ?: 0,
            leveCount = p.leveCount ?: 0,
            graveCount = p.graveCount ?: 0,
            mortalCount = p.mortalCount ?: 0,
            predominantHourRange = p.predominantHourRange.naOrDefault("—"),
            predominantWeekday = p.predominantWeekday.naOrDefault("—"),
            viaType = p.viaType.naOrDefault("CALLE"),
            predominantVictimMode = p.predominantVictimMode.naOrDefault("—"),
            addressLabel = p.addressLabel.naOrDefault("Zona vial"),
            comuna = p.comuna.naOrDefault("—"),
            radiusMeters = p.radiusMeters ?: 25.0,
        )
    }

    private fun String?.naOrDefault(default: String) =
        if (this == null || this == "NA" || this.isBlank()) default else this
}
