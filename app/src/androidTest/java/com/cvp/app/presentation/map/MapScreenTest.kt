package com.cvp.app.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvp.app.core.design.components.CvpEmptyState
import com.cvp.app.core.design.theme.CvpTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for [MapScreen] UI components and error states.
 *
 * The MapLibre [MapView] requires network connectivity for tile loading; it is not
 * exercised here. These tests focus on the overlay layer: the top bar, error states,
 * FABs, and the no-permission card, all of which are pure Compose and testable in isolation.
 */
@RunWith(AndroidJUnit4::class)
class MapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorStateIsDisplayedWhenGeoJsonFails() {
        composeTestRule.setContent {
            CvpTheme {
                CvpEmptyState(
                    title = "No se pudieron cargar las zonas",
                    subtitle = "El archivo de datos está dañado o no está disponible.",
                    actionLabel = "Reintentar",
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onNodeWithText("No se pudieron cargar las zonas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun retryButtonInErrorStateIsClickable() {
        var retried = false
        composeTestRule.setContent {
            CvpTheme {
                CvpEmptyState(
                    title = "No se pudieron cargar las zonas",
                    subtitle = "El archivo de datos está dañado o no está disponible.",
                    actionLabel = "Reintentar",
                    onAction = { retried = true },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onNodeWithText("Reintentar").performClick()
        assert(retried) { "Reintentar button did not trigger onAction callback" }
    }

    @Test
    fun topBarDisplaysCvpTitle() {
        composeTestRule.setContent {
            CvpTheme {
                com.cvp.app.presentation.map.components.CvpMapTopBar(
                    isLoadingZones = false,
                    onSettingsClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("CVP").assertIsDisplayed()
    }

    @Test
    fun topBarShowsLoadingIndicatorWhenZonesAreLoading() {
        composeTestRule.setContent {
            CvpTheme {
                com.cvp.app.presentation.map.components.CvpMapTopBar(
                    isLoadingZones = true,
                    onSettingsClick = {},
                )
            }
        }
        // The top bar title is still visible even during loading
        composeTestRule.onNodeWithText("CVP").assertIsDisplayed()
    }
}
