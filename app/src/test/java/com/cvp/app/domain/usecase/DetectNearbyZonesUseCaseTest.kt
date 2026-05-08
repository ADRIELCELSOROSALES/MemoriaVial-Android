package com.cvp.app.domain.usecase

import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.UserLocation
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetectNearbyZonesUseCaseTest {

    private val useCase = DetectNearbyZonesUseCase()

    private fun location(
        lat: Double,
        lon: Double,
        bearingDegrees: Float? = null,
    ) = UserLocation(
        latitude = lat,
        longitude = lon,
        timestamp = Clock.System.now(),
        speedMps = null,
        bearingDegrees = bearingDegrees,
        accuracyMeters = null,
    )

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

    // Zone ~111m north of origin (lat+0.001 ≈ 111m)
    private val zoneNorth = zone("north", lat = 0.001, lon = 0.0)

    // Zone ~111m south of origin
    private val zoneSouth = zone("south", lat = -0.001, lon = 0.0)

    // Zone ~111m east of origin
    private val zoneEast = zone("east", lat = 0.0, lon = 0.001)

    // Zone ~500m away — outside 300m radius
    private val zoneFar = zone("far", lat = 0.005, lon = 0.0)

    @Test
    fun `without bearing all zones within radius are returned`() {
        val loc = location(0.0, 0.0, bearingDegrees = null)
        val result = useCase(loc, listOf(zoneNorth, zoneSouth, zoneEast), radiusMeters = 200.0)
        assertEquals(3, result.size)
    }

    @Test
    fun `zone outside radius is excluded regardless of bearing`() {
        val loc = location(0.0, 0.0)
        val result = useCase(loc, listOf(zoneFar), radiusMeters = 200.0)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `with bearing north includes zones ahead and excludes zones behind`() {
        val loc = location(0.0, 0.0, bearingDegrees = 0f)  // heading north
        val result = useCase(loc, listOf(zoneNorth, zoneSouth), radiusMeters = 200.0)
        assertTrue(result.any { it.id == "north" }, "Zone to the north should be included")
        assertTrue(result.none { it.id == "south" }, "Zone to the south should be excluded")
    }

    @Test
    fun `with bearing south includes south zone and excludes north zone`() {
        val loc = location(0.0, 0.0, bearingDegrees = 180f)  // heading south
        val result = useCase(loc, listOf(zoneNorth, zoneSouth), radiusMeters = 200.0)
        assertTrue(result.any { it.id == "south" })
        assertTrue(result.none { it.id == "north" })
    }

    @Test
    fun `zone exactly at 90 degree bearing delta is included`() {
        // Heading north, zone is due east — bearing delta = 90° — should be included (edge case)
        val loc = location(0.0, 0.0, bearingDegrees = 0f)
        val result = useCase(loc, listOf(zoneEast), radiusMeters = 200.0)
        assertTrue(result.any { it.id == "east" })
    }

    @Test
    fun `empty zone list returns empty result`() {
        val loc = location(0.0, 0.0)
        val result = useCase(loc, emptyList(), radiusMeters = 200.0)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `all zones outside radius returns empty result`() {
        val loc = location(0.0, 0.0)
        val result = useCase(loc, listOf(zoneFar), radiusMeters = 50.0)
        assertTrue(result.isEmpty())
    }
}
