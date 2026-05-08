package com.cvp.app.domain.util

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

private fun assertNear(expected: Double, actual: Double, tolerance: Double = 1.0, msg: String = "") {
    assertTrue(abs(actual - expected) <= tolerance, "$msg expected=$expected actual=$actual")
}

class DistanceUtilsTest {

    @Test
    fun `haversine between same point is zero`() {
        assertNear(0.0, DistanceUtils.haversineMeters(0.0, 0.0, 0.0, 0.0), tolerance = 0.001)
    }

    @Test
    fun `haversine one degree of latitude is approximately 111195 meters`() {
        val d = DistanceUtils.haversineMeters(0.0, 0.0, 1.0, 0.0)
        assertNear(111_195.0, d, tolerance = 10.0)
    }

    @Test
    fun `haversine one degree of longitude at equator is approximately 111195 meters`() {
        val d = DistanceUtils.haversineMeters(0.0, 0.0, 0.0, 1.0)
        assertNear(111_195.0, d, tolerance = 10.0)
    }

    @Test
    fun `haversine is symmetric`() {
        val d1 = DistanceUtils.haversineMeters(-34.6037, -58.3816, -34.8941, -56.1953)
        val d2 = DistanceUtils.haversineMeters(-34.8941, -56.1953, -34.6037, -58.3816)
        assertNear(d1, d2, tolerance = 0.001)
    }

    @Test
    fun `haversine Buenos Aires to Montevideo is approximately 202 km`() {
        val d = DistanceUtils.haversineMeters(-34.6037, -58.3816, -34.8941, -56.1953)
        assertNear(202_000.0, d, tolerance = 5_000.0)
    }

    @Test
    fun `bearing due north is 0 degrees`() {
        val b = DistanceUtils.bearingDegrees(0.0, 0.0, 1.0, 0.0)
        assertNear(0.0, b, tolerance = 0.001)
    }

    @Test
    fun `bearing due east is 90 degrees`() {
        val b = DistanceUtils.bearingDegrees(0.0, 0.0, 0.0, 1.0)
        assertNear(90.0, b, tolerance = 0.001)
    }

    @Test
    fun `bearing due south is 180 degrees`() {
        val b = DistanceUtils.bearingDegrees(0.0, 0.0, -1.0, 0.0)
        assertNear(180.0, b, tolerance = 0.001)
    }

    @Test
    fun `bearing due west is 270 degrees`() {
        val b = DistanceUtils.bearingDegrees(0.0, 0.0, 0.0, -1.0)
        assertNear(270.0, b, tolerance = 0.001)
    }

    @Test
    fun `bearingDelta between same bearings is 0`() {
        assertNear(0.0, DistanceUtils.bearingDeltaDegrees(45.0, 45.0), tolerance = 0.001)
    }

    @Test
    fun `bearingDelta between opposite bearings is 180`() {
        assertNear(180.0, DistanceUtils.bearingDeltaDegrees(0.0, 180.0), tolerance = 0.001)
    }

    @Test
    fun `bearingDelta wraps correctly across 360 boundary`() {
        // 350 and 10 are 20 degrees apart
        assertNear(20.0, DistanceUtils.bearingDeltaDegrees(10.0, 350.0), tolerance = 0.001)
        assertNear(20.0, DistanceUtils.bearingDeltaDegrees(350.0, 10.0), tolerance = 0.001)
    }

    @Test
    fun `bearingDelta is always between 0 and 180`() {
        val delta = DistanceUtils.bearingDeltaDegrees(90.0, 300.0)
        assertTrue(delta in 0.0..180.0)
    }
}
