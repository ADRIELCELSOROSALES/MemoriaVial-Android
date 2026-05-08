package com.cvp.app.data.repository

import app.cash.turbine.test
import com.cvp.app.domain.model.AppSettings
import com.cvp.app.domain.model.ThemeMode
import com.cvp.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the [SettingsRepository] contract using a manual fake.
 * The real [SettingsRepositoryImpl] uses Android DataStore and is covered
 * by instrumentation tests on a real device.
 */
class SettingsRepositoryTest {

    private class FakeSettingsRepository : SettingsRepository {
        private val _settings = MutableStateFlow(AppSettings())
        override val settings: Flow<AppSettings> = _settings.asStateFlow()

        override suspend fun setNotificationsEnabled(enabled: Boolean) {
            _settings.update { it.copy(notificationsEnabled = enabled) }
        }

        override suspend fun setVibrationEnabled(enabled: Boolean) {
            _settings.update { it.copy(vibrationEnabled = enabled) }
        }

        override suspend fun setAlertRadius(meters: Int) {
            _settings.update { it.copy(alertRadiusMeters = meters.coerceIn(50, 300)) }
        }

        override suspend fun setThemeMode(mode: ThemeMode) {
            _settings.update { it.copy(themeMode = mode) }
        }

        override suspend fun setShowZones(enabled: Boolean) {
            _settings.update { it.copy(showZones = enabled) }
        }
    }

    private val repo: SettingsRepository = FakeSettingsRepository()

    @Test
    fun `default settings match AppSettings defaults`() = runTest {
        repo.settings.test {
            val settings = awaitItem()
            assertTrue(settings.notificationsEnabled)
            assertTrue(settings.vibrationEnabled)
            assertEquals(150, settings.alertRadiusMeters)
            assertEquals(ThemeMode.SYSTEM, settings.themeMode)
            assertTrue(settings.showZones)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setNotificationsEnabled updates flow`() = runTest {
        repo.settings.test {
            awaitItem() // consume initial
            repo.setNotificationsEnabled(false)
            assertFalse(awaitItem().notificationsEnabled)
            repo.setNotificationsEnabled(true)
            assertTrue(awaitItem().notificationsEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setVibrationEnabled updates flow`() = runTest {
        repo.settings.test {
            awaitItem()
            repo.setVibrationEnabled(false)
            assertFalse(awaitItem().vibrationEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setAlertRadius updates flow`() = runTest {
        repo.settings.test {
            awaitItem()
            repo.setAlertRadius(200)
            assertEquals(200, awaitItem().alertRadiusMeters)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setAlertRadius clamps to 50-300 range`() = runTest {
        repo.settings.test {
            awaitItem()
            repo.setAlertRadius(10)
            assertEquals(50, awaitItem().alertRadiusMeters)
            repo.setAlertRadius(500)
            assertEquals(300, awaitItem().alertRadiusMeters)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setThemeMode updates flow`() = runTest {
        repo.settings.test {
            awaitItem()
            repo.setThemeMode(ThemeMode.DARK)
            assertEquals(ThemeMode.DARK, awaitItem().themeMode)
            repo.setThemeMode(ThemeMode.LIGHT)
            assertEquals(ThemeMode.LIGHT, awaitItem().themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setShowZones updates flow`() = runTest {
        repo.settings.test {
            awaitItem()
            repo.setShowZones(false)
            assertFalse(awaitItem().showZones)
            repo.setShowZones(true)
            assertTrue(awaitItem().showZones)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple settings change independently`() = runTest {
        repo.setNotificationsEnabled(false)
        repo.setThemeMode(ThemeMode.DARK)
        repo.settings.test {
            val settings = awaitItem()
            assertFalse(settings.notificationsEnabled)
            assertEquals(ThemeMode.DARK, settings.themeMode)
            assertTrue(settings.showZones) // unchanged
            cancelAndIgnoreRemainingEvents()
        }
    }
}
