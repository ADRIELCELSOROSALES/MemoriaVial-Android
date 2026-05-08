package com.cvp.app.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cvp.app.presentation.designsystem.DesignSystemShowcase
import com.cvp.app.presentation.map.MapScreen
import com.cvp.app.presentation.onboarding.OnboardingScreen
import com.cvp.app.presentation.settings.SettingsScreen
import com.cvp.app.presentation.settings.about.AboutScreen
import com.cvp.app.presentation.settings.privacy.PrivacyScreen
import com.cvp.app.presentation.welcome.WelcomeScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

@Serializable
object MapRoute

@Serializable
private object WelcomeRoute

@Serializable
object SettingsRoute

@Serializable
object AboutRoute

@Serializable
object PrivacyRoute

@Serializable
private object DesignSystemShowcaseRoute

@Composable
fun AppNavHost(
    startDestination: Any = WelcomeRoute,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<OnboardingRoute>(
            exitTransition = { fadeOut(tween(300)) },
        ) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(MapRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<MapRoute>(
            enterTransition = { fadeIn(tween(300)) },
            popEnterTransition = { fadeIn(tween(200)) },
        ) {
            MapScreen(
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
            )
        }
        composable<SettingsRoute>(
            enterTransition = { slideInHorizontally(tween(200)) { it } },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { slideOutHorizontally(tween(200)) { it } },
        ) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(AboutRoute) },
                onNavigateToPrivacy = { navController.navigate(PrivacyRoute) },
            )
        }
        composable<AboutRoute>(
            enterTransition = { slideInVertically(tween(250)) { it } },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { slideOutVertically(tween(250)) { it } },
        ) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable<PrivacyRoute>(
            enterTransition = { slideInVertically(tween(250)) { it } },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { slideOutVertically(tween(250)) { it } },
        ) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable<WelcomeRoute> {
            WelcomeScreen(
                onNavigateToShowcase = { navController.navigate(DesignSystemShowcaseRoute) },
            )
        }
        composable<DesignSystemShowcaseRoute> {
            DesignSystemShowcase(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
