package com.cvp.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val alertRadiusMeters: Int = 150,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showZones: Boolean = true,
)

@Serializable
enum class ThemeMode { SYSTEM, LIGHT, DARK }
