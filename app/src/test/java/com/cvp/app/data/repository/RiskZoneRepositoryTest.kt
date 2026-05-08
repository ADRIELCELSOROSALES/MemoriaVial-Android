package com.cvp.app.data.repository

import com.cvp.app.data.local.ZoneLoader
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.Severity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RiskZoneRepositoryTest {

    private fun fakeZone(id: String) = RiskZone(
        id = id,
        latitude = -34.6037,
        longitude = -58.3814,
        incidentCount = 1,
        leveCount = 1,
        graveCount = 0,
        mortalCount = 0,
        predominantHourRange = "07-09",
        predominantWeekday = "lunes",
        viaType = "AVENIDA",
        predominantVictimMode = "AUTO",
        addressLabel = "Test",
        comuna = "1",
        radiusMeters = 25.0,
    )

    @Test
    fun `getAllZones returns zones from loader on first call`() = runTest {
        val zones = listOf(fakeZone("z1"), fakeZone("z2"))
        val repo = RiskZoneRepositoryImpl(ZoneLoader { Result.success(zones) })

        val result = repo.getAllZones()

        assertEquals(2, result.size)
        assertEquals("z1", result[0].id)
    }

    @Test
    fun `getAllZones uses cached result on second call without re-invoking loader`() = runTest {
        var callCount = 0
        val zones = listOf(fakeZone("z1"))
        val repo = RiskZoneRepositoryImpl(ZoneLoader {
            callCount++
            Result.success(zones)
        })

        repo.getAllZones()
        repo.getAllZones()

        assertEquals(1, callCount)
    }

    @Test
    fun `loader failure propagates exception to caller`() = runTest {
        val repo = RiskZoneRepositoryImpl(ZoneLoader {
            Result.failure(RuntimeException("disk error"))
        })

        assertFailsWith<RuntimeException> { repo.getAllZones() }
    }

    @Test
    fun `refresh forces reload from loader even when cache is populated`() = runTest {
        var callCount = 0
        val initialZones = listOf(fakeZone("z1"))
        val refreshedZones = listOf(fakeZone("z2"), fakeZone("z3"))

        val repo = RiskZoneRepositoryImpl(ZoneLoader {
            callCount++
            if (callCount == 1) Result.success(initialZones) else Result.success(refreshedZones)
        })

        repo.getAllZones()
        repo.refresh()
        val afterRefresh = repo.getAllZones()  // should hit cache (refreshed), no extra loader call

        assertEquals(2, callCount)
        assertEquals(2, afterRefresh.size)
        assertEquals("z2", afterRefresh[0].id)
    }

    @Test
    fun `refresh failure leaves previous cache intact`() = runTest {
        val zones = listOf(fakeZone("z1"))
        var fail = false
        val repo = RiskZoneRepositoryImpl(ZoneLoader {
            if (fail) Result.failure(RuntimeException("network error"))
            else Result.success(zones)
        })

        repo.getAllZones()  // populate cache
        fail = true
        repo.refresh()     // loader returns failure — cache must survive

        val result = repo.getAllZones()
        assertEquals(1, result.size)
        assertEquals("z1", result[0].id)
    }
}
