package com.cvp.app.domain.repository

import com.cvp.app.domain.model.AppSettings
import com.cvp.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setAlertRadius(meters: Int)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setShowZones(enabled: Boolean)
}
