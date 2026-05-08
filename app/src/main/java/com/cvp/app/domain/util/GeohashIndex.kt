package com.cvp.app.domain.util

import com.cvp.app.domain.model.RiskZone

/**
 * Geohash-based spatial index for O(1) proximity lookups.
 *
 * Zones are indexed at [precision] characters. [queryNearby] returns candidates
 * from the query cell plus its 8 neighbors — the caller must still apply a
 * distance filter to remove false positives introduced by the coarse-grid approach.
 *
 * At precision 6, each cell covers approximately 1.2 km × 0.6 km. Checking 9 cells
 * guarantees that any zone within ~2 km of the query point is a candidate, which
 * is more than enough for the 50–300 m alert radii used by CVP.
 */
class GeohashIndex(
    zones: List<RiskZone>,
    private val precision: Int = 6,
) {
    private val index: Map<String, List<RiskZone>> = buildIndex(zones)

    private fun buildIndex(zones: List<RiskZone>): Map<String, List<RiskZone>> {
        val map = mutableMapOf<String, MutableList<RiskZone>>()
        zones.forEach { zone ->
            val hash = Geohash.encode(zone.latitude, zone.longitude, precision)
            map.getOrPut(hash) { mutableListOf() }.add(zone)
        }
        return map
    }

    /**
     * Returns all indexed zones whose geohash cell overlaps with the 3×3 neighborhood
     * of the cell containing ([lat], [lon]).
     */
    fun queryNearby(lat: Double, lon: Double): List<RiskZone> {
        val center = Geohash.encode(lat, lon, precision)
        val cells = listOf(center) + Geohash.neighbors(center)
        return cells.flatMap { index[it] ?: emptyList() }
    }
}
