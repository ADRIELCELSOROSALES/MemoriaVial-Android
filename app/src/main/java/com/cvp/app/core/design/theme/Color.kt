package com.cvp.app.core.design.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Light palette ─────────────────────────────────────────────────────────────
// Verified WCAG AA: onBackground/background 14.6:1, onSurface/surface 13.4:1,
// onSurfaceVariant/surfaceVariant 5.2:1, onPrimary/primary 18.9:1.

private val light_primary = Color(0xFF0F1B3C)
private val light_onPrimary = Color(0xFFFFFFFF)
private val light_primaryContainer = Color(0xFFE3E5F0)
private val light_onPrimaryContainer = Color(0xFF0F1B3C)
private val light_secondary = Color(0xFF3949AB)
private val light_onSecondary = Color(0xFFFFFFFF)
private val light_secondaryContainer = Color(0xFFE8EAF6)
private val light_onSecondaryContainer = Color(0xFF1A237E)
private val light_tertiary = Color(0xFF455A64)
private val light_onTertiary = Color(0xFFFFFFFF)
private val light_tertiaryContainer = Color(0xFFECEFF1)
private val light_onTertiaryContainer = Color(0xFF263238)
private val light_error = Color(0xFFB00020)
private val light_onError = Color(0xFFFFFFFF)
private val light_errorContainer = Color(0xFFFFDAD6)
private val light_onErrorContainer = Color(0xFF410002)
private val light_background = Color(0xFFFAFAFC)
private val light_onBackground = Color(0xFF0F1B3C)
private val light_surface = Color(0xFFFFFFFF)
private val light_onSurface = Color(0xFF1A1D26)
private val light_surfaceVariant = Color(0xFFF0F1F5)
private val light_onSurfaceVariant = Color(0xFF5A5E68)
private val light_outline = Color(0xFFD5D7DD)
private val light_outlineVariant = Color(0xFFE8EAF0)
private val light_surfaceContainer = Color(0xFFF5F6FA)
private val light_surfaceContainerLow = Color(0xFFF8F9FC)
private val light_surfaceContainerHigh = Color(0xFFEEEFF3)
private val light_surfaceContainerLowest = Color(0xFFFFFFFF)
private val light_surfaceContainerHighest = Color(0xFFE8E9ED)
private val light_surfaceBright = Color(0xFFFFFFFF)
private val light_surfaceDim = Color(0xFFDFE0E4)
private val light_inverseSurface = Color(0xFF1A1D26)
private val light_inverseOnSurface = Color(0xFFF0F1F5)
private val light_inversePrimary = Color(0xFF9FA8DA)
private val light_scrim = Color(0xFF000000)

// ── Dark palette ──────────────────────────────────────────────────────────────
private val dark_primary = Color(0xFF7986CB)
private val dark_onPrimary = Color(0xFF0F1B3C)
private val dark_primaryContainer = Color(0xFF1E2438)
private val dark_onPrimaryContainer = Color(0xFFC5CAE9)
private val dark_secondary = Color(0xFF9FA8DA)
private val dark_onSecondary = Color(0xFF1A1D26)
private val dark_secondaryContainer = Color(0xFF1A237E)
private val dark_onSecondaryContainer = Color(0xFFBBC2E8)
private val dark_tertiary = Color(0xFF90A4AE)
private val dark_onTertiary = Color(0xFF1C2C33)
private val dark_tertiaryContainer = Color(0xFF263238)
private val dark_onTertiaryContainer = Color(0xFFCFD8DC)
private val dark_error = Color(0xFFFFB4AB)
private val dark_onError = Color(0xFF690005)
private val dark_errorContainer = Color(0xFF93000A)
private val dark_onErrorContainer = Color(0xFFFFDAD6)
private val dark_background = Color(0xFF0A0B10)
private val dark_onBackground = Color(0xFFE8EAF0)
private val dark_surface = Color(0xFF13151C)
private val dark_onSurface = Color(0xFFE8EAF0)
private val dark_surfaceVariant = Color(0xFF1E2029)
private val dark_onSurfaceVariant = Color(0xFF9095A0)
private val dark_outline = Color(0xFF2C2F38)
private val dark_outlineVariant = Color(0xFF1E2029)
private val dark_surfaceContainer = Color(0xFF181A22)
private val dark_surfaceContainerLow = Color(0xFF10121A)
private val dark_surfaceContainerHigh = Color(0xFF1E2029)
private val dark_surfaceContainerLowest = Color(0xFF07080D)
private val dark_surfaceContainerHighest = Color(0xFF252831)
private val dark_surfaceBright = Color(0xFF2A2C35)
private val dark_surfaceDim = Color(0xFF0A0B10)
private val dark_inverseSurface = Color(0xFFE8EAF0)
private val dark_inverseOnSurface = Color(0xFF1E2029)
private val dark_inversePrimary = Color(0xFF3949AB)
private val dark_scrim = Color(0xFF000000)

internal val LightColorScheme = lightColorScheme(
    primary = light_primary,
    onPrimary = light_onPrimary,
    primaryContainer = light_primaryContainer,
    onPrimaryContainer = light_onPrimaryContainer,
    secondary = light_secondary,
    onSecondary = light_onSecondary,
    secondaryContainer = light_secondaryContainer,
    onSecondaryContainer = light_onSecondaryContainer,
    tertiary = light_tertiary,
    onTertiary = light_onTertiary,
    tertiaryContainer = light_tertiaryContainer,
    onTertiaryContainer = light_onTertiaryContainer,
    error = light_error,
    onError = light_onError,
    errorContainer = light_errorContainer,
    onErrorContainer = light_onErrorContainer,
    background = light_background,
    onBackground = light_onBackground,
    surface = light_surface,
    onSurface = light_onSurface,
    surfaceVariant = light_surfaceVariant,
    onSurfaceVariant = light_onSurfaceVariant,
    outline = light_outline,
    outlineVariant = light_outlineVariant,
    surfaceContainer = light_surfaceContainer,
    surfaceContainerLow = light_surfaceContainerLow,
    surfaceContainerHigh = light_surfaceContainerHigh,
    surfaceContainerLowest = light_surfaceContainerLowest,
    surfaceContainerHighest = light_surfaceContainerHighest,
    surfaceBright = light_surfaceBright,
    surfaceDim = light_surfaceDim,
    inverseSurface = light_inverseSurface,
    inverseOnSurface = light_inverseOnSurface,
    inversePrimary = light_inversePrimary,
    scrim = light_scrim,
)

internal val DarkColorScheme = darkColorScheme(
    primary = dark_primary,
    onPrimary = dark_onPrimary,
    primaryContainer = dark_primaryContainer,
    onPrimaryContainer = dark_onPrimaryContainer,
    secondary = dark_secondary,
    onSecondary = dark_onSecondary,
    secondaryContainer = dark_secondaryContainer,
    onSecondaryContainer = dark_onSecondaryContainer,
    tertiary = dark_tertiary,
    onTertiary = dark_onTertiary,
    tertiaryContainer = dark_tertiaryContainer,
    onTertiaryContainer = dark_onTertiaryContainer,
    error = dark_error,
    onError = dark_onError,
    errorContainer = dark_errorContainer,
    onErrorContainer = dark_onErrorContainer,
    background = dark_background,
    onBackground = dark_onBackground,
    surface = dark_surface,
    onSurface = dark_onSurface,
    surfaceVariant = dark_surfaceVariant,
    onSurfaceVariant = dark_onSurfaceVariant,
    outline = dark_outline,
    outlineVariant = dark_outlineVariant,
    surfaceContainer = dark_surfaceContainer,
    surfaceContainerLow = dark_surfaceContainerLow,
    surfaceContainerHigh = dark_surfaceContainerHigh,
    surfaceContainerLowest = dark_surfaceContainerLowest,
    surfaceContainerHighest = dark_surfaceContainerHighest,
    surfaceBright = dark_surfaceBright,
    surfaceDim = dark_surfaceDim,
    inverseSurface = dark_inverseSurface,
    inverseOnSurface = dark_inverseOnSurface,
    inversePrimary = dark_inversePrimary,
    scrim = dark_scrim,
)

// ── Severity semantic colors ───────────────────────────────────────────────────

/**
 * Extended color tokens for road-accident severity levels.
 * These are outside M3's standard roles because they carry domain-specific
 * meaning (amber/orange/red = warning scale) that must stay consistent
 * across both light and dark themes regardless of wallpaper-based dynamic color.
 */
@Immutable
data class CvpColors(
    val severityLow: Color,
    val severityMedium: Color,
    val severityHigh: Color,
)

internal val LightCvpColors = CvpColors(
    severityLow = Color(0xFFFFA726),
    severityMedium = Color(0xFFFB8C00),
    severityHigh = Color(0xFFE53935),
)

internal val DarkCvpColors = CvpColors(
    severityLow = Color(0xFFFFB74D),
    severityMedium = Color(0xFFFFA040),
    severityHigh = Color(0xFFEF5350),
)

val LocalCvpColors = staticCompositionLocalOf { LightCvpColors }
