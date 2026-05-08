package com.cvp.app.presentation.welcome

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cvp.app.core.design.components.CvpButton
import com.cvp.app.core.design.components.CvpButtonVariant
import com.cvp.app.core.design.theme.CvpTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    onNavigateToShowcase: () -> Unit = {},
    viewModel: WelcomeViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is ZonesLoadState.Success) {
            val count = (uiState as ZonesLoadState.Success).count
            Toast.makeText(context, "$count zonas cargadas", Toast.LENGTH_LONG).show()
        }
        if (uiState is ZonesLoadState.Error) {
            val msg = (uiState as ZonesLoadState.Error).message
            Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = CvpTheme.spacing.xl),
        ) {
            Text(
                text = "CVP",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(CvpTheme.spacing.sm))
            Text(
                text = "Capa Vial Predictiva",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(CvpTheme.spacing.xxl))
            CvpButton(
                text = "Cargar zonas",
                onClick = { viewModel.loadZones() },
                variant = CvpButtonVariant.Secondary,
                isLoading = uiState is ZonesLoadState.Loading,
            )
            Spacer(modifier = Modifier.height(CvpTheme.spacing.sm))
            TextButton(onClick = onNavigateToShowcase) {
                Text(
                    text = "Design System →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Welcome — Light", showBackground = true)
@Preview(name = "Welcome — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WelcomeScreenPreview() {
    CvpTheme {
        // Preview uses a no-op lambda instead of the real ViewModel
        WelcomeScreen(viewModel = WelcomeViewModel(repository = FakeRiskZoneRepository()))
    }
}

// Preview-only stub — never used in production code
private class FakeRiskZoneRepository : com.cvp.app.domain.repository.RiskZoneRepository {
    override suspend fun getAllZones() = emptyList<com.cvp.app.domain.model.RiskZone>()
    override suspend fun refresh() = Unit
}
