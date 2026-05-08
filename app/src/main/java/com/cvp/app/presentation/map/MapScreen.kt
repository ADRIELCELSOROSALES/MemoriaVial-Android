package com.cvp.app.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cvp.app.core.design.components.CvpEmptyState
import com.cvp.app.core.design.theme.CvpTheme
import com.cvp.app.domain.model.Severity
import com.cvp.app.presentation.common.permissions.LocationPermissionState
import com.cvp.app.presentation.common.permissions.rememberLocationPermissionState
import com.cvp.app.presentation.map.components.CvpMapFabs
import com.cvp.app.presentation.map.components.CvpMapTopBar
import com.cvp.app.presentation.map.components.NoLocationPermissionCard
import com.cvp.app.presentation.map.rendering.RiskZoneRenderer
import com.cvp.app.presentation.zonedetail.ZoneDetailBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: MapViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val cvpColors = CvpTheme.colors
    val mapState = rememberCvpMapState()
    val permissionHandler = rememberLocationPermissionState()
    val snackbarHostState = remember { SnackbarHostState() }

    val zoneColors = remember(cvpColors) {
        mapOf(
            Severity.LOW to cvpColors.severityLow.toArgb(),
            Severity.MEDIUM to cvpColors.severityMedium.toArgb(),
            Severity.HIGH to cvpColors.severityHigh.toArgb(),
        )
    }

    val renderer = remember(mapState) {
        RiskZoneRenderer(mapState.mapView) { zone -> viewModel.onZoneSelected(zone) }
    }
    DisposableEffect(renderer) {
        onDispose { renderer.destroy() }
    }

    // Reload style on theme flip; renderer re-creates its manager inside the callback
    LaunchedEffect(isDark) {
        mapState.loadStyle(isDark) { renderer.onStyleLoaded() }
    }

    LaunchedEffect(uiState.riskZones, uiState.showZones) {
        renderer.updateZones(uiState.riskZones, uiState.showZones, zoneColors)
    }

    LaunchedEffect(permissionHandler.state) {
        if (permissionHandler.state is LocationPermissionState.Granted) {
            viewModel.onPermissionGranted()
        }
    }
    LaunchedEffect(uiState.hasLocationPermission, uiState.userLocation) {
        if (uiState.hasLocationPermission) {
            mapState.setLocationEnabled(
                enabled = true,
                hasBearing = uiState.userLocation?.bearingDegrees != null,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MapEvent.CenterOnUser -> mapState.flyTo(event.location)
                is MapEvent.FocusOnZone -> mapState.focusOnZone(event.zone)
            }
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.loadFailed) {
        val msg = uiState.errorMessage ?: return@LaunchedEffect
        if (uiState.loadFailed) return@LaunchedEffect  // Fatal error — shown as full overlay, not snackbar
        val result = snackbarHostState.showSnackbar(
            message = msg,
            actionLabel = "Reintentar",
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) viewModel.onRetry()
        viewModel.onErrorDismissed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapState.mapView },
            modifier = Modifier.fillMaxSize(),
        )

        CvpMapTopBar(
            isLoadingZones = uiState.isLoadingZones,
            onSettingsClick = onNavigateToSettings,
            modifier = Modifier.align(Alignment.TopStart),
        )

        CvpMapFabs(
            hasLocation = uiState.userLocation != null,
            showZones = uiState.showZones,
            onCenterClick = { viewModel.onCenterOnUser() },
            onToggleZonesClick = { viewModel.onToggleZones() },
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        val showPermissionCard = !uiState.hasLocationPermission &&
            permissionHandler.state !is LocationPermissionState.Granted
        if (showPermissionCard) {
            NoLocationPermissionCard(
                isPermanentlyDenied = permissionHandler.state is LocationPermissionState.PermanentlyDenied,
                onActivateClick = {
                    if (permissionHandler.state is LocationPermissionState.PermanentlyDenied) {
                        permissionHandler.openAppSettings()
                    } else {
                        permissionHandler.requestPermission()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 88.dp, start = 16.dp, end = 88.dp),
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.inversePrimary,
                )
            },
        )
    }

    if (uiState.loadFailed && !uiState.isLoadingZones) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            CvpEmptyState(
                title = "No se pudieron cargar las zonas",
                subtitle = uiState.errorMessage
                    ?: "El archivo de datos está dañado o no está disponible.",
                actionLabel = "Reintentar",
                onAction = { viewModel.onRetry() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    uiState.selectedZone?.let { zone ->
        ZoneDetailBottomSheet(
            zone = zone,
            onDismiss = {
                viewModel.onSheetDismissed()
                mapState.restoreZoom()
            },
        )
    }
}
