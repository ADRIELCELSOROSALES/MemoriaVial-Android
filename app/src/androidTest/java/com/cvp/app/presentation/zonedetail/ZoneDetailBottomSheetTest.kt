package com.cvp.app.presentation.zonedetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvp.app.core.design.theme.CvpTheme
import com.cvp.app.domain.model.RiskZone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoneDetailBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testZone = RiskZone(
        id = "test_zone",
        latitude = -34.6037,
        longitude = -58.3816,
        incidentCount = 12,
        leveCount = 8,
        graveCount = 3,
        mortalCount = 1,
        predominantHourRange = "07-09",
        predominantWeekday = "lunes",
        viaType = "AVENIDA",
        predominantVictimMode = "AUTO",
        addressLabel = "Av. Corrientes 1234",
        comuna = "3",
        radiusMeters = 50.0,
    )

    @Test
    fun zoneAddressIsDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                ZoneDetailBottomSheet(zone = testZone, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithText("Av. Corrientes 1234").assertIsDisplayed()
    }

    @Test
    fun severityLabelIsDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                ZoneDetailBottomSheet(zone = testZone, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithText("Riesgo alto").assertIsDisplayed()
    }

    @Test
    fun comunaIsDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                ZoneDetailBottomSheet(zone = testZone, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun contextChipsAreDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                ZoneDetailBottomSheet(zone = testZone, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithText("07-09").assertIsDisplayed()
        composeTestRule.onNodeWithText("lunes").assertIsDisplayed()
    }

    @Test
    fun closarButtonIsDisplayed() {
        composeTestRule.setContent {
            CvpTheme {
                ZoneDetailBottomSheet(zone = testZone, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithText("Cerrar").assertIsDisplayed()
    }
}
