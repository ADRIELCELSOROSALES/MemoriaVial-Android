package com.cvp.app.presentation.onboarding

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.components.CvpButton
import com.cvp.app.core.design.components.CvpButtonVariant
import com.cvp.app.core.design.components.CvpIconBadge
import com.cvp.app.core.design.theme.CvpTheme
import com.cvp.app.presentation.common.permissions.LocationPermissionState
import com.cvp.app.presentation.common.permissions.rememberLocationPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val permissionHandler = rememberLocationPermissionState()
    var permissionFlowStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { onComplete() }
    }

    // Navigate after the permission dialog resolves
    LaunchedEffect(permissionHandler.state) {
        if (!permissionFlowStarted) return@LaunchedEffect
        when (permissionHandler.state) {
            LocationPermissionState.Granted,
            LocationPermissionState.Denied,
            LocationPermissionState.PermanentlyDenied -> viewModel.onCompleteOnboarding()
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.statusBarsPadding())

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                val isActive = pagerState.currentPage == page
                when (page) {
                    0 -> WelcomePage(isActive = isActive)
                    1 -> HowItWorksPage(isActive = isActive)
                    else -> PermissionsPage(isActive = isActive)
                }
            }

            OnboardingFooter(
                currentPage = pagerState.currentPage,
                onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                onActivateLocation = {
                    when (permissionHandler.state) {
                        LocationPermissionState.Granted -> viewModel.onCompleteOnboarding()
                        LocationPermissionState.PermanentlyDenied -> permissionHandler.openAppSettings()
                        else -> {
                            permissionFlowStarted = true
                            permissionHandler.requestPermission()
                        }
                    }
                },
                onLater = { viewModel.onCompleteOnboarding() },
                isPermanentlyDenied = permissionHandler.state == LocationPermissionState.PermanentlyDenied,
            )
        }

        // "Saltar" only on pages 0 and 1
        if (pagerState.currentPage < 2) {
            TextButton(
                onClick = { viewModel.onSkip() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = CvpTheme.spacing.sm),
            ) {
                Text(
                    text = "Saltar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Pages ───────────────────────────────────────────────────────────────────

@Composable
private fun WelcomePage(isActive: Boolean = false) {
    var illustrationVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) { if (isActive) illustrationVisible = true }
    val scale by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0.9f,
        animationSpec = tween(300),
        label = "welcomeIlluScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "welcomeIlluAlpha",
    )
    PageContainer {
        MapPinIllustration(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
        Spacer(Modifier.height(CvpTheme.spacing.xxl))
        Text(
            text = "Conducí más atento",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(CvpTheme.spacing.md))
        Text(
            text = "CVP te muestra zonas donde se registraron siniestros viales en Buenos Aires, para que sepas dónde extremar la atención.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HowItWorksPage(isActive: Boolean = false) {
    var illustrationVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) { if (isActive) illustrationVisible = true }
    val scale by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0.9f,
        animationSpec = tween(300),
        label = "howIlluScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "howIlluAlpha",
    )
    PageContainer {
        ZonesIllustration(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
        Spacer(Modifier.height(CvpTheme.spacing.xxl))
        Text(
            text = "Datos públicos, memoria colectiva",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(CvpTheme.spacing.md))
        Text(
            text = "Trabajamos con datos oficiales del Observatorio de Movilidad de la Ciudad. Donde el humano olvida, el sistema recuerda.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PermissionsPage(isActive: Boolean = false) {
    var illustrationVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) { if (isActive) illustrationVisible = true }
    val scale by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0.9f,
        animationSpec = tween(300),
        label = "permIlluScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (illustrationVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "permIlluAlpha",
    )
    PageContainer {
        CvpIconBadge(
            icon = Icons.Filled.LocationOn,
            backgroundColor = MaterialTheme.colorScheme.primary,
            size = 96.dp,
            iconSize = 48.dp,
            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
        Spacer(Modifier.height(CvpTheme.spacing.xxl))
        Text(
            text = "Necesitamos tu ubicación",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(CvpTheme.spacing.md))
        Text(
            text = "Solo se usa en tu dispositivo. No guardamos ni enviamos tu ubicación a ningún servidor.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(CvpTheme.spacing.lg))
        Text(
            text = "Podés revocar este permiso en cualquier momento desde la configuración de Android.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = CvpTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

// ─── Footer ──────────────────────────────────────────────────────────────────

@Composable
private fun OnboardingFooter(
    currentPage: Int,
    onNext: () -> Unit,
    onActivateLocation: () -> Unit,
    onLater: () -> Unit,
    isPermanentlyDenied: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CvpTheme.spacing.xl)
            .navigationBarsPadding()
            .padding(bottom = CvpTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.sm),
    ) {
        PageIndicator(pageCount = 3, currentPage = currentPage)
        Spacer(Modifier.height(CvpTheme.spacing.md))
        when (currentPage) {
            0, 1 -> CvpButton(
                text = "Siguiente",
                onClick = onNext,
                variant = CvpButtonVariant.Primary,
            )
            else -> {
                CvpButton(
                    text = if (isPermanentlyDenied) "Abrir configuración" else "Activar ubicación",
                    onClick = onActivateLocation,
                    variant = CvpButtonVariant.Primary,
                )
                CvpButton(
                    text = "Más tarde",
                    onClick = onLater,
                    variant = CvpButtonVariant.Secondary,
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "indicator_$index",
            )
            val color by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                animationSpec = tween(200),
                label = "indicator_color_$index",
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

// ─── Illustrations ────────────────────────────────────────────────────────────

@Composable
private fun MapPinIllustration(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
        label = "pulse_scale",
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
        label = "pulse_alpha",
    )

    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surfaceContainerHigh
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)

    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val baseR = 20.dp.toPx()
        val bgR = size.minDimension / 2f

        drawCircle(surface, bgR, c)

        // Road grid
        for (i in 1..4) {
            val x = size.width * i / 5f
            drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1.5.dp.toPx())
            val y = size.height * i / 5f
            drawLine(grid, Offset(0f, y), Offset(size.width, y), 1.5.dp.toPx())
        }
        // Diagonal road
        drawLine(
            grid.copy(alpha = 0.18f),
            Offset(0f, size.height * 0.65f),
            Offset(size.width * 0.55f, size.height * 0.1f),
            2.dp.toPx(),
        )

        // Pulse ring
        drawCircle(primary.copy(alpha = pulseAlpha), baseR * pulseScale, c)

        // Core
        drawCircle(primary, baseR, c)
        drawCircle(Color.White.copy(alpha = 0.85f), baseR * 0.4f, c)
    }
}

@Composable
private fun ZonesIllustration(modifier: Modifier = Modifier) {
    val low = CvpTheme.colors.severityLow
    val medium = CvpTheme.colors.severityMedium
    val high = CvpTheme.colors.severityHigh
    val surface = MaterialTheme.colorScheme.surfaceContainerHigh
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)

    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val bgR = size.minDimension / 2f
        val dotR = 18.dp.toPx()

        drawCircle(surface, bgR, c)

        for (i in 1..4) {
            val x = size.width * i / 5f
            drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1.5.dp.toPx())
            val y = size.height * i / 5f
            drawLine(grid, Offset(0f, y), Offset(size.width, y), 1.5.dp.toPx())
        }

        val posHigh = Offset(c.x - 48.dp.toPx(), c.y - 32.dp.toPx())
        val posMed = Offset(c.x + 38.dp.toPx(), c.y + 18.dp.toPx())
        val posLow = Offset(c.x - 16.dp.toPx(), c.y + 48.dp.toPx())

        // Halos
        drawCircle(high.copy(alpha = 0.18f), dotR * 1.9f, posHigh)
        drawCircle(medium.copy(alpha = 0.18f), dotR * 1.9f, posMed)
        drawCircle(low.copy(alpha = 0.18f), dotR * 1.9f, posLow)

        // Dots
        drawCircle(high, dotR, posHigh)
        drawCircle(medium, dotR, posMed)
        drawCircle(low, dotR, posLow)

        // White centers
        drawCircle(Color.White.copy(alpha = 0.75f), dotR * 0.38f, posHigh)
        drawCircle(Color.White.copy(alpha = 0.75f), dotR * 0.38f, posMed)
        drawCircle(Color.White.copy(alpha = 0.75f), dotR * 0.38f, posLow)
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(name = "Onboarding — Light", showBackground = true)
@Preview(name = "Onboarding — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnboardingScreenPreview() {
    CvpTheme {
        // Static preview of the welcome page layout
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(Modifier.fillMaxSize()) {
                WelcomePage()
                OnboardingFooter(
                    currentPage = 0,
                    onNext = {},
                    onActivateLocation = {},
                    onLater = {},
                    isPermanentlyDenied = false,
                )
            }
        }
    }
}

@Preview(name = "Onboarding — Permisos", showBackground = true)
@Composable
private fun OnboardingPermissionsPreview() {
    CvpTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(Modifier.fillMaxSize()) {
                PermissionsPage()
                OnboardingFooter(
                    currentPage = 2,
                    onNext = {},
                    onActivateLocation = {},
                    onLater = {},
                    isPermanentlyDenied = false,
                )
            }
        }
    }
}
