package com.cvp.app.data.local

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GeoJsonLoaderTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun minimalFeature(
        id: String,
        lon: Double,
        lat: Double,
        mortal: Int = 0,
        grave: Int = 0,
        leve: Int = 1,
    ) = """
        {
          "type": "Feature",
          "geometry": { "type": "Point", "coordinates": [$lon, $lat] },
          "properties": {
            "id": "$id",
            "incident_count": ${mortal + grave + leve},
            "leve_count": $leve,
            "grave_count": $grave,
            "mortal_count": $mortal,
            "predominant_hour_range": "07-09",
            "predominant_weekday": "lunes",
            "via_type": "AVENIDA",
            "predominant_victim_mode": "AUTO",
            "address_label": "Test $id",
            "comuna": "1",
            "radius_meters": 25.0
          }
        }
    """.trimIndent()

    private fun featureCollection(vararg features: String) =
        """{ "type": "FeatureCollection", "features": [${features.joinToString(",")}] }"""

    @Test
    fun `valid single feature is parsed correctly`() {
        val raw = featureCollection(minimalFeature("z1", lon = -58.3814, lat = -34.6037))
        val zones = parseGeoJsonString(json, raw)
        assertEquals(1, zones.size)
        assertEquals("z1", zones[0].id)
    }

    @Test
    fun `multiple valid features are all parsed`() {
        val raw = featureCollection(
            minimalFeature("z1", lon = -58.3814, lat = -34.6037),
            minimalFeature("z2", lon = -58.4205, lat = -34.5807),
            minimalFeature("z3", lon = -58.4920, lat = -34.5580),
        )
        assertEquals(3, parseGeoJsonString(json, raw).size)
    }

    @Test
    fun `feature outside CABA bounds is discarded`() {
        val raw = featureCollection(
            minimalFeature("valid", lon = -58.3814, lat = -34.6037),
            minimalFeature("outside", lon = -57.0, lat = -34.6037),
        )
        val zones = parseGeoJsonString(json, raw)
        assertEquals(1, zones.size)
        assertEquals("valid", zones[0].id)
    }

    @Test
    fun `empty features list returns empty list`() {
        val raw = featureCollection()
        assertEquals(emptyList(), parseGeoJsonString(json, raw))
    }

    @Test
    fun `all features outside CABA returns empty list`() {
        val raw = featureCollection(
            minimalFeature("far", lon = -57.0, lat = -33.0),
        )
        assertEquals(emptyList(), parseGeoJsonString(json, raw))
    }

    @Test
    fun `malformed JSON throws an exception`() {
        assertFailsWith<Exception> {
            parseGeoJsonString(json, "not json at all")
        }
    }

    @Test
    fun `lon-lat coordinate swap is applied during parsing`() {
        val lon = -58.3756
        val lat = -34.6435
        val raw = featureCollection(minimalFeature("z1", lon = lon, lat = lat))
        val zone = parseGeoJsonString(json, raw).first()
        assertEquals(lat, zone.latitude)
        assertEquals(lon, zone.longitude)
    }
}
