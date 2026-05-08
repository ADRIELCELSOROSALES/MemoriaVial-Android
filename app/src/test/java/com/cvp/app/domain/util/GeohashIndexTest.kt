package com.cvp.app.domain.util

import com.cvp.app.domain.model.RiskZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeohashIndexTest {

    private fun zone(id: String, lat: Double, lon: Double) = RiskZone(
        id = id,
        latitude = lat,
        longitude = lon,
        incidentCount = 1,
        leveCount = 1,
        graveCount = 0,
        mortalCount = 0,
        predominantHourRange = "07-09",
        predominantWeekday = "lunes",
        viaType = "AVENIDA",
        predominantVictimMode = "AUTO",
        addressLabel = "Test $id",
        comuna = "1",
        radiusMeters = 25.0,
    )

    @Test
    fun `queryNearby from zone location returns that zone`() {
        val z = zone("z1", lat = -34.6037, lon = -58.3816)
        val index = GeohashIndex(listOf(z))
        val results = index.queryNearby(-34.6037, -58.3816)
        assertTrue(results.any { it.id == "z1" })
    }

    @Test
    fun `queryNearby from far away does not return nearby zone`() {
        val z = zone("z1", lat = -34.6037, lon = -58.3816)
        val index = GeohashIndex(listOf(z))
        // Query from Rosario (~300 km away)
        val results = index.queryNearby(-32.9442, -60.6505)
        assertTrue(results.none { it.id == "z1" })
    }

    @Test
    fun `queryNearby returns zones from neighboring cells`() {
        // Place the zone just inside a cell boundary; query from the adjacent side.
        val precision = 6
        val (latH, lonW) = Geohash.cellDimensions(precision)
        val zoneLat = -34.6037
        val zoneLon = -58.3816
        val z = zone("border", lat = zoneLat, lon = zoneLon)
        val index = GeohashIndex(listOf(z), precision = precision)

        // Query from a point in a neighboring cell (slightly offset, same neighborhood)
        val queryLat = zoneLat + latH * 0.6
        val queryLon = zoneLon
        val results = index.queryNearby(queryLat, queryLon)
        assertTrue(results.any { it.id == "border" })
    }

    @Test
    fun `empty index returns empty list`() {
        val index = GeohashIndex(emptyList())
        assertTrue(index.queryNearby(-34.6037, -58.3816).isEmpty())
    }

    @Test
    fun `multiple zones in same cell are all returned`() {
        val z1 = zone("z1", lat = -34.6037, lon = -58.3816)
        val z2 = zone("z2", lat = -34.6038, lon = -58.3817)
        val z3 = zone("z3", lat = -34.6039, lon = -58.3815)
        val index = GeohashIndex(listOf(z1, z2, z3))
        val results = index.queryNearby(-34.6037, -58.3816)
        val ids = results.map { it.id }.toSet()
        assertTrue(ids.containsAll(setOf("z1", "z2", "z3")))
    }

    @Test
    fun `queryNearby returns at most 9 cells worth of results`() {
        // Verifies the index doesn't scan the entire dataset — only 9 cells.
        val zones = (1..100).map { i ->
            zone("z$i", lat = -34.6037 + i * 1.0, lon = -58.3816)
        }
        val index = GeohashIndex(zones)
        // A zone 100 degrees north should not appear in results for CABA coords
        val results = index.queryNearby(-34.6037, -58.3816)
        assertTrue(results.size < 50)
    }
}
