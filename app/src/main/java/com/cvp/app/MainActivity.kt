package com.cvp.app

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cvp.app.core.design.theme.CvpTheme
import com.cvp.app.domain.model.ThemeMode
import com.cvp.app.domain.repository.SettingsRepository
import com.cvp.app.presentation.AppNavHost
import com.cvp.app.presentation.MapRoute
import com.cvp.app.presentation.OnboardingRoute
import com.cvp.app.presentation.main.MainViewModel
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { provider ->
            provider.view.animate()
                .alpha(0f)
                .setDuration(300L)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { provider.remove() }
                .start()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Bridge the Android Koin instance to the Compose composition.
            // Required by koin-androidx-compose so that koinViewModel() / koinInject()
            // can resolve dependencies without falling back to an uninitialized context.
            KoinAndroidContext {
                val settingsRepository = koinInject<SettingsRepository>()
                val themeMode by settingsRepository.settings
                    .map { it.themeMode }
                    .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

                CvpTheme(themeMode = themeMode) {
                    val viewModel: MainViewModel = koinViewModel()
                    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

                    when (onboardingCompleted) {
                        null -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        true -> AppNavHost(
                            startDestination = MapRoute,
                            modifier = Modifier.fillMaxSize(),
                        )
                        false -> AppNavHost(
                            startDestination = OnboardingRoute,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
