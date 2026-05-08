package com.cvp.app.presentation.zonedetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val cvpColors = CvpTheme.colors

    val severityColor = when (zone.severity) {
        Severity.LOW -> cvpColors.severityLow
        Severity.MEDIUM -> cvpColors.severityMedium
        Severity.HIGH -> cvpColors.severityHigh
    }
    val severityLabel = when (zone.severity) {
        Severity.LOW -> "Riesgo bajo"
        Severity.MEDIUM -> "Riesgo medio"
        Severity.HIGH -> "Riesgo alto"
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
    val animTotal by animateIntAsState(if (started) zone.incidentCount else 0, animSpec, "total")
    val animLeve by animateIntAsState(if (started) zone.leveCount else 0, animSpec, "leve")
    val animGrave by animateIntAsState(if (started) zone.graveCount else 0, animSpec, "grave")
    val animMortal by animateIntAsState(if (started) zone.mortalCount else 0, animSpec, "mortal")

    var showChips by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showChips = true }

    val chips = remember(zone) {
        listOf<Pair<ImageVector, String>>(
            Icons.Outlined.Schedule to zone.predominantHourRange,
            Icons.Outlined.CalendarToday to zone.predominantWeekday,
            Icons.Outlined.Directions to zone.viaType,
            Icons.Outlined.Person to zone.predominantVictimMode,
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
            } // end pulse wrapper
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
            StatItem(value = animTotal, label = "Total")
            StatDivider()
            StatItem(value = animLeve, label = "Leve")
            StatDivider()
            StatItem(value = animGrave, label = "Grave")
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

        // Explanatory card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Esta zona concentra ${zone.incidentCount} incidentes registrados. " +
                    "La mayoría ocurre los ${zone.predominantWeekday.lowercase()} " +
                    "entre las ${zone.predominantHourRange}.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp),
            )
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalButton(
                onClick = {
                    val uri = Uri.parse("google.navigation:q=${zone.latitude},${zone.longitude}")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Outlined.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cómo llegar")
            }
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cerrar")
            }
        }
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
