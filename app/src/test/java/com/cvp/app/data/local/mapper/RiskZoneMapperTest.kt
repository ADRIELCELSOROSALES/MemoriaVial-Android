package com.cvp.app.data.local.mapper

import com.cvp.app.data.local.dto.GeoJsonFeatureDto
import com.cvp.app.data.local.dto.GeoJsonGeometryDto
import com.cvp.app.data.local.dto.GeoJsonPropertiesDto
import com.cvp.app.domain.model.Severity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RiskZoneMapperTest {

    private fun feature(
        lon: Double = -58.3814,
        lat: Double = -34.6037,
        id: String = "zone_test",
        mortal: Int = 0,
        grave: Int = 0,
        leve: Int = 1,
    ) = GeoJsonFeatureDto(
        type = "Feature",
        geometry = GeoJsonGeometryDto(
            type = "Point",
            coordinates = listOf(lon, lat),   // GeoJSON order: [lon, lat]
        ),
        properties = GeoJsonPropertiesDto(
            id = id,
            incidentCount = mortal + grave + leve,
            leveCount = leve,
            graveCount = grave,
            mortalCount = mortal,
            predominantHourRange = "07-09",
            predominantWeekday = "lunes",
            viaType = "AVENIDA",
            predominantVictimMode = "AUTO",
            addressLabel = "Test address",
            comuna = "1",
            radiusMeters = 25.0,
        ),
    )

    @Test
    fun `valid feature within CABA bounds maps to RiskZone`() {
        val zone = RiskZoneMapper.fromDto(feature())
        assertNotNull(zone)
        assertEquals("zone_test", zone.id)
    }

    @Test
    fun `GeoJSON lon-lat order is swapped to lat-lon in domain model`() {
        val lon = -58.3814
        val lat = -34.6037
        val zone = RiskZoneMapper.fromDto(feature(lon = lon, lat = lat))
        assertNotNull(zone)
        assertEquals(lat, zone.latitude)
        assertEquals(lon, zone.longitude)
    }

    @Test
    fun `all properties are mapped correctly`() {
        val zone = RiskZoneMapper.fromDto(feature(grave = 2, leve = 3))
        assertNotNull(zone)
        assertEquals(5, zone.incidentCount)
        assertEquals(3, zone.leveCount)
        assertEquals(2, zone.graveCount)
        assertEquals(0, zone.mortalCount)
        assertEquals("07-09", zone.predominantHourRange)
        assertEquals("lunes", zone.predominantWeekday)
        assertEquals("AVENIDA", zone.viaType)
        assertEquals("AUTO", zone.predominantVictimMode)
        assertEquals("Test address", zone.addressLabel)
        assertEquals("1", zone.comuna)
        assertEquals(25.0, zone.radiusMeters)
    }

    @Test
    fun `longitude outside CABA bounds returns null`() {
        assertNull(RiskZoneMapper.fromDto(feature(lon = -58.0)))   // too far east
        assertNull(RiskZoneMapper.fromDto(feature(lon = -59.0)))   // too far west
    }

    @Test
    fun `latitude outside CABA bounds returns null`() {
        assertNull(RiskZoneMapper.fromDto(feature(lat = -34.40)))  // too far north
        assertNull(RiskZoneMapper.fromDto(feature(lat = -34.90)))  // too far south
    }

    @Test
    fun `empty coordinates list returns null`() {
        val bad = feature().copy(
            geometry = GeoJsonGeometryDto(type = "Point", coordinates = emptyList()),
        )
        assertNull(RiskZoneMapper.fromDto(bad))
    }

    @Test
    fun `single coordinate returns null`() {
        val bad = feature().copy(
            geometry = GeoJsonGeometryDto(type = "Point", coordinates = listOf(-58.3814)),
        )
        assertNull(RiskZoneMapper.fromDto(bad))
    }

    @Test
    fun `severity is propagated from domain model`() {
        val high = RiskZoneMapper.fromDto(feature(mortal = 1))
        assertNotNull(high)
        assertEquals(Severity.HIGH, high.severity)

        val medium = RiskZoneMapper.fromDto(feature(grave = 2))
        assertNotNull(medium)
        assertEquals(Severity.MEDIUM, medium.severity)

        val low = RiskZoneMapper.fromDto(feature(leve = 5))
        assertNotNull(low)
        assertEquals(Severity.LOW, low.severity)
    }
}
