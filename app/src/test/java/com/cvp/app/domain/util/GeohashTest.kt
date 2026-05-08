package com.cvp.app.domain.util

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeohashTest {

    // (0,0) encodes to "s00000" at precision 6 — verified manually against the algorithm.
    @Test
    fun `encode known coordinate 0,0 at precision 6`() {
        assertEquals("s00000", Geohash.encode(0.0, 0.0, 6))
    }

    @Test
    fun `encode precision 1 returns single character`() {
        val hash = Geohash.encode(0.0, 0.0, 1)
        assertEquals(1, hash.length)
    }

    @Test
    fun `encode precision 12 returns twelve characters`() {
        val hash = Geohash.encode(-34.6037, -58.3816, 12)
        assertEquals(12, hash.length)
    }

    @Test
    fun `encode result contains only valid base32 characters`() {
        val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
        val hash = Geohash.encode(-34.6037, -58.3816, 9)
        assertTrue(hash.all { it in base32 })
    }

    @Test
    fun `encode precision out of range throws`() {
        assertFailsWith<IllegalArgumentException> { Geohash.encode(0.0, 0.0, 0) }
        assertFailsWith<IllegalArgumentException> { Geohash.encode(0.0, 0.0, 13) }
    }

    @Test
    fun `decode of encoded coordinates round-trips within cell bounds`() {
        val lat = -34.6037
        val lon = -58.3816
        val precision = 6
        val hash = Geohash.encode(lat, lon, precision)
        val (decodedLat, decodedLon) = Geohash.decode(hash)
        val (latH, lonW) = Geohash.cellDimensions(precision)
        assertTrue(abs(decodedLat - lat) <= latH)
        assertTrue(abs(decodedLon - lon) <= lonW)
    }

    @Test
    fun `decode of s00000 returns coordinates near 0,0`() {
        val (lat, lon) = Geohash.decode("s00000")
        assertTrue(abs(lat) < 0.01)
        assertTrue(abs(lon) < 0.01)
    }

    @Test
    fun `decode invalid character throws`() {
        assertFailsWith<IllegalArgumentException> { Geohash.decode("a") }
    }

    @Test
    fun `neighbors returns exactly 8 results`() {
        val hash = Geohash.encode(-34.6037, -58.3816, 6)
        assertEquals(8, Geohash.neighbors(hash).size)
    }

    @Test
    fun `neighbors have the same precision as input`() {
        val precision = 6
        val hash = Geohash.encode(-34.6037, -58.3816, precision)
        Geohash.neighbors(hash).forEach { assertEquals(precision, it.length) }
    }

    @Test
    fun `neighbors are all distinct and different from center`() {
        val hash = Geohash.encode(-34.6037, -58.3816, 6)
        val neighbors = Geohash.neighbors(hash)
        assertEquals(8, neighbors.toSet().size)
        assertFalse(hash in neighbors)
    }

    @Test
    fun `cellDimensions at precision 6 are approximately 0011 degrees lat and 0022 degrees lon`() {
        val (latH, lonW) = Geohash.cellDimensions(6)
        // Precision 6: 30 bits total, 15 lon bits, 15 lat bits
        // latH = 180 / 2^15 ≈ 0.00549°, lonW = 360 / 2^15 ≈ 0.01099°
        assertTrue(abs(latH - 0.00549) < 0.001)
        assertTrue(abs(lonW - 0.01099) < 0.001)
    }
}
