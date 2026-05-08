package com.cvp.app.presentation.map.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CvpMapFabs(
    hasLocation: Boolean,
    showZones: Boolean,
    onCenterClick: () -> Unit,
    onToggleZonesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        ZoneToggleFab(showZones = showZones, onClick = onToggleZonesClick)

        MyLocationFab(
            hasLocation = hasLocation,
            onClick = onCenterClick,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ZoneToggleFab(
    showZones: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by animateColorAsState(
        targetValue = if (showZones) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "zoneToggleColor",
    )
    SmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        containerColor = containerColor,
        contentColor = if (showZones) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
        ),
    ) {
        Crossfade(
            targetState = showZones,
            animationSpec = tween(durationMillis = 150),
            label = "zoneIcon",
        ) { visible ->
            Icon(
                imageVector = if (visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                contentDescription = if (visible) "Ocultar zonas" else "Mostrar zonas",
            )
        }
    }
}

@Composable
private fun MyLocationFab(
    hasLocation: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    FloatingActionButton(
        onClick = {
            if (hasLocation) {
                scope.launch {
                    rotation.snapTo(0f)
                    rotation.animateTo(360f, tween(durationMillis = 400))
                }
                onClick()
            }
        },
        modifier = modifier
            .alpha(if (hasLocation) 1f else 0.45f)
            .graphicsLayer { rotationZ = rotation.value },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = "Mi ubicación",
        )
    }
}
