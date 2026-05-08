package com.cvp.app.core.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.cvp.app.domain.model.ThemeMode

@Composable
fun CvpTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val cvpColors = if (darkTheme) DarkCvpColors else LightCvpColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalCvpColors provides cvpColors,
        LocalCvpSpacing provides CvpSpacing,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CvpTypography,
            shapes = CvpShapes,
            content = content,
        )
    }
}

object CvpTheme {
    val colors: CvpColors
        @Composable @ReadOnlyComposable
        get() = LocalCvpColors.current

    val spacing: CvpSpacing
        @Composable @ReadOnlyComposable
        get() = LocalCvpSpacing.current
}
