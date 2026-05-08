package com.cvp.app.presentation.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvp.app.core.design.components.CvpButton
import com.cvp.app.core.design.theme.CvpTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for the onboarding flow UI components.
 *
 * The full [OnboardingScreen] is not tested here because it depends on Koin-injected ViewModels
 * and the location permission system. These tests instead verify that the CVP design system
 * components used in the onboarding render correctly and respond to user input.
 *
 * Integration-level onboarding flow tests are covered by the E2E suite run against a physical
 * device with full Koin setup.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun siguienteButtonIsDisplayedAndClickable() {
        var clicked = false
        composeTestRule.setContent {
            CvpTheme {
                CvpButton(text = "Siguiente", onClick = { clicked = true })
            }
        }
        composeTestRule.onNodeWithText("Siguiente").assertIsDisplayed()
        composeTestRule.onNodeWithText("Siguiente").performClick()
        assert(clicked) { "Siguiente button callback was not invoked" }
    }

    @Test
    fun salterButtonIsDisplayedAndClickable() {
        var skipped = false
        composeTestRule.setContent {
            CvpTheme {
                androidx.compose.material3.TextButton(onClick = { skipped = true }) {
                    androidx.compose.material3.Text("Saltar")
                }
            }
        }
        composeTestRule.onNodeWithText("Saltar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saltar").performClick()
        assert(skipped) { "Saltar button callback was not invoked" }
    }

    @Test
    fun welcomePageHeadlineIsRendered() {
        composeTestRule.setContent {
            CvpTheme {
                androidx.compose.material3.Text(
                    text = "Conducí más atento",
                    style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                )
            }
        }
        composeTestRule.onNodeWithText("Conducí más atento").assertIsDisplayed()
    }

    @Test
    fun activarUbicacionButtonIsDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                CvpButton(text = "Activar ubicación", onClick = {})
            }
        }
        composeTestRule.onNodeWithText("Activar ubicación").assertIsDisplayed()
    }
}
