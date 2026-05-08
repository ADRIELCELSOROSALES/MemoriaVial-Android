package com.cvp.app.domain.usecase

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShouldNotifyUseCaseTest {

    private val useCase = ShouldNotifyUseCase()

    @Test
    fun `first call for a zone returns true`() {
        assertTrue(useCase.shouldNotify("zone_1"))
    }

    @Test
    fun `second call for the same zone returns false`() {
        useCase.shouldNotify("zone_1")
        assertFalse(useCase.shouldNotify("zone_1"))
    }

    @Test
    fun `different zones each return true on first call`() {
        assertTrue(useCase.shouldNotify("zone_a"))
        assertTrue(useCase.shouldNotify("zone_b"))
        assertTrue(useCase.shouldNotify("zone_c"))
    }

    @Test
    fun `clearSession resets notification state`() {
        useCase.shouldNotify("zone_1")
        useCase.clearSession()
        assertTrue(useCase.shouldNotify("zone_1"))
    }

    @Test
    fun `clearSession allows all zones to notify again`() {
        useCase.shouldNotify("zone_1")
        useCase.shouldNotify("zone_2")
        useCase.clearSession()
        assertTrue(useCase.shouldNotify("zone_1"))
        assertTrue(useCase.shouldNotify("zone_2"))
    }

    @Test
    fun `subsequent calls after first are all false until clearSession`() {
        useCase.shouldNotify("zone_x")
        repeat(5) {
            assertFalse(useCase.shouldNotify("zone_x"))
        }
        useCase.clearSession()
        assertTrue(useCase.shouldNotify("zone_x"))
    }
}
