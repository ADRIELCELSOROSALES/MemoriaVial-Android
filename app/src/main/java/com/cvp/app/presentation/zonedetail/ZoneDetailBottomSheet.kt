package com.cvp.app.presentation.zonedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpTheme
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.Severity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneDetailBottomSheet(
    zone: RiskZone,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        ZoneDetailContent(zone = zone, onDismiss = onDismiss)
    }
}

@Composable
private fun ZoneDetailContent(zone: RiskZone, onDismiss: () -> Unit) {
    val cvpColors = CvpTheme.colors

    val severityColor = when (zone.severity) {
        Severity.LOW    -> cvpColors.severityLow
        Severity.MEDIUM -> cvpColors.severityMedium
        Severity.HIGH   -> cvpColors.severityHigh
    }
    val severityLabel = when (zone.severity) {
        Severity.LOW    -> "Solo incidentes leves"
        Severity.MEDIUM -> "Con heridos graves"
        Severity.HIGH   -> "Con víctimas fatales"
    }

    val badgeTransition = rememberInfiniteTransition(label = "badgePulse")
    val badgeScale by badgeTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "badgeScale",
    )

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val animSpec = tween<Int>(300, easing = LinearOutSlowInEasing)
    val animTotal  by animateIntAsState(if (started) zone.incidentCount else 0, animSpec, "total")
    val animLeve   by animateIntAsState(if (started) zone.leveCount     else 0, animSpec, "leve")
    val animGrave  by animateIntAsState(if (started) zone.graveCount    else 0, animSpec, "grave")
    val animMortal by animateIntAsState(if (started) zone.mortalCount   else 0, animSpec, "mortal")

    var showChips by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showChips = true }

    var showAdvice by remember { mutableStateOf(false) }

    val chips = remember(zone) {
        listOf<Pair<ImageVector, String>>(
            Icons.Outlined.Schedule     to zone.predominantHourRange,
            Icons.Outlined.CalendarToday to zone.predominantWeekday,
            Icons.Outlined.Directions   to zone.viaType,
            Icons.Outlined.Person       to zone.predominantVictimMode,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header: severity badge + address + commune
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = badgeScale
                    scaleY = badgeScale
                },
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(severityColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = zone.addressLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = severityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = severityColor,
                        fontWeight = FontWeight.Medium,
                    )
                    Text("·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = zone.comuna,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Stats row with counting animation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(value = animTotal,  label = "Total")
            StatDivider()
            StatItem(value = animLeve,   label = "Leve")
            StatDivider()
            StatItem(value = animGrave,  label = "Grave")
            StatDivider()
            StatItem(value = animMortal, label = "Mortal")
        }

        // Context chips with stagger animation
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            itemsIndexed(chips) { index, (icon, label) ->
                AnimatedVisibility(
                    visible = showChips,
                    enter = fadeIn(tween(200, delayMillis = index * 60)) +
                        slideInVertically(tween(200, delayMillis = index * 60)) { it / 2 },
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                        },
                    )
                }
            }
        }

        // Expandable safety advice section
        AnimatedVisibility(
            visible = showAdvice,
            enter = fadeIn(tween(200)) + expandVertically(tween(250)),
            exit  = fadeOut(tween(150)) + shrinkVertically(tween(200)),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = severityColor.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "¿Por qué tener cuidado aquí?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = severityColor,
                        )
                    }
                    Text(
                        text = safetyAdvice(zone),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalButton(
                onClick = { showAdvice = !showAdvice },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (showAdvice) "Ocultar" else "Más info")
            }
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cerrar")
            }
        }
    }
}

private fun safetyAdvice(zone: RiskZone): String {
    val mode = zone.predominantVictimMode.lowercase()
    val day  = zone.predominantWeekday
    val hour = zone.predominantHourRange
    return when (zone.severity) {
        Severity.HIGH ->
            "Esta zona registró ${zone.mortalCount} víctima${if (zone.mortalCount != 1) "s" else ""} fatal${if (zone.mortalCount != 1) "es" else ""}. " +
            "Los accidentes ocurren con mayor frecuencia los $day entre las $hour. " +
            "El modo de transporte más afectado es $mode. " +
            "Extreme la precaución al circular por esta intersección, respete la señalización y reduzca la velocidad."
        Severity.MEDIUM ->
            "Zona con ${zone.graveCount} herido${if (zone.graveCount != 1) "s" else ""} grave${if (zone.graveCount != 1) "s" else ""}. " +
            "El grupo más expuesto son los usuarios de $mode. " +
            "Mayor riesgo los $day entre las $hour. " +
            "Circule con especial atención y evite distracciones al pasar por aquí."
        Severity.LOW ->
            "Alta frecuencia de incidentes: ${zone.leveCount} herido${if (zone.leveCount != 1) "s" else ""} leve${if (zone.leveCount != 1) "s" else ""} registrados. " +
            "Los $mode son el grupo más afectado. " +
            "Mayor actividad los $day entre las $hour. " +
            "Aunque los incidentes son leves, la recurrencia indica un punto de riesgo permanente."
    }
}

@Composable
private fun StatItem(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}
