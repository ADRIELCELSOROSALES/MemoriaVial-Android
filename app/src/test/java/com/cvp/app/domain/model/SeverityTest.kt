package com.cvp.app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SeverityTest {

    private fun zone(mortal: Int = 0, grave: Int = 0, leve: Int = 0) = RiskZone(
        id = "test",
        latitude = -34.6037,
        longitude = -58.3814,
        incidentCount = mortal + grave + leve,
        leveCount = leve,
        graveCount = grave,
        mortalCount = mortal,
        predominantHourRange = "00-03",
        predominantWeekday = "lunes",
        viaType = "AVENIDA",
        predominantVictimMode = "AUTO",
        addressLabel = "Test",
        comuna = "1",
        radiusMeters = 25.0,
    )

    @Test
    fun `mortalCount greater than zero yields HIGH`() {
        assertEquals(Severity.HIGH, zone(mortal = 1).severity)
        assertEquals(Severity.HIGH, zone(mortal = 3, grave = 1).severity)
    }

    @Test
    fun `any graveCount with no mortal yields MEDIUM`() {
        assertEquals(Severity.MEDIUM, zone(grave = 1).severity)
        assertEquals(Severity.MEDIUM, zone(grave = 3).severity)
        assertEquals(Severity.MEDIUM, zone(grave = 10).severity)
    }

    @Test
    fun `only leve incidents yields LOW regardless of count`() {
        assertEquals(Severity.LOW, zone(leve = 1).severity)
        assertEquals(Severity.LOW, zone(leve = 50).severity)
    }

    @Test
    fun `all zeros yields LOW`() {
        assertEquals(Severity.LOW, zone().severity)
    }
}
