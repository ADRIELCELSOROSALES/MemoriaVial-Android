package com.cvp.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cvp.app.domain.model.AppSettings
import com.cvp.app.domain.model.ThemeMode
import com.cvp.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override val settings: Flow<AppSettings> = context.settingsDataStore.data
        .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
        .map { prefs ->
            AppSettings(
                notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
                alertRadiusMeters = prefs[Keys.ALERT_RADIUS_METERS] ?: 150,
                themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
                showZones = prefs[Keys.SHOW_ZONES] ?: true,
            )
        }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    override suspend fun setAlertRadius(meters: Int) {
        context.settingsDataStore.edit { it[Keys.ALERT_RADIUS_METERS] = meters.coerceIn(50, 300) }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun setShowZones(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.SHOW_ZONES] = enabled }
    }

    private object Keys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val ALERT_RADIUS_METERS = intPreferencesKey("alert_radius_meters")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHOW_ZONES = booleanPreferencesKey("show_zones")
    }
}
