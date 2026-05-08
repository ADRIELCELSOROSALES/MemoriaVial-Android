package com.cvp.app.presentation.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.components.CvpButton
import com.cvp.app.core.design.components.CvpButtonVariant
import com.cvp.app.core.design.components.CvpCard
import com.cvp.app.core.design.components.CvpChip
import com.cvp.app.core.design.components.CvpDivider
import com.cvp.app.core.design.components.CvpIconBadge
import com.cvp.app.core.design.components.CvpStatItem
import com.cvp.app.core.design.components.CvpTopBar
import com.cvp.app.core.design.theme.CvpTheme

@Composable
fun DesignSystemShowcase(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CvpTopBar(
                title = "Design System",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = CvpTheme.spacing.md,
                end = CvpTheme.spacing.md,
                top = innerPadding.calculateTopPadding() + CvpTheme.spacing.md,
                bottom = innerPadding.calculateBottomPadding() + CvpTheme.spacing.md,
            ),
            verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.lg),
        ) {
            item { ShowcaseSection("Paleta de colores") { ColorPaletteSection() } }
            item { ShowcaseSection("Severidad") { SeveritySection() } }
            item { ShowcaseSection("Tipografía") { TypographySection() } }
            item { ShowcaseSection("Botones") { ButtonsSection() } }
            item { ShowcaseSection("Card") { CardSection() } }
            item { ShowcaseSection("Chips") { ChipsSection() } }
            item { ShowcaseSection("Icon Badges") { IconBadgesSection() } }
            item { ShowcaseSection("Stats") { StatsSection() } }
        }
    }
}

// ── Sections ─────────────────────────────────────────────────────────────────

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.sm)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        CvpDivider()
        content()
    }
}

@Composable
private fun ColorPaletteSection() {
    val cs = MaterialTheme.colorScheme
    val swatches = listOf(
        "primary" to cs.primary,
        "onPrimary" to cs.onPrimary,
        "primaryContainer" to cs.primaryContainer,
        "secondary" to cs.secondary,
        "background" to cs.background,
        "surface" to cs.surface,
        "surfaceVariant" to cs.surfaceVariant,
        "surfaceContainer" to cs.surfaceContainer,
        "onBackground" to cs.onBackground,
        "onSurface" to cs.onSurface,
        "onSurfaceVariant" to cs.onSurfaceVariant,
        "outline" to cs.outline,
        "error" to cs.error,
    )
    Column(verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.xs)) {
        swatches.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(CvpTheme.spacing.xs)) {
                row.forEach { (label, color) ->
                    ColorSwatch(label = label, color = color, modifier = Modifier.weight(1f))
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ColorSwatch(label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp),
                ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SeveritySection() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(CvpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SeverityItem("Baja", CvpTheme.colors.severityLow)
        SeverityItem("Media", CvpTheme.colors.severityMedium)
        SeverityItem("Alta", CvpTheme.colors.severityHigh)
    }
}

@Composable
private fun SeverityItem(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CvpIconBadge(
            icon = Icons.Filled.Place,
            backgroundColor = color,
            size = 44.dp,
            iconSize = 22.dp,
        )
        Spacer(Modifier.height(CvpTheme.spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TypographySection() {
    data class TypeSample(val label: String, val style: androidx.compose.ui.text.TextStyle)

    val samples = with(MaterialTheme.typography) {
        listOf(
            TypeSample("displayLarge", displayLarge),
            TypeSample("displayMedium", displayMedium),
            TypeSample("displaySmall", displaySmall),
            TypeSample("headlineLarge", headlineLarge),
            TypeSample("headlineMedium", headlineMedium),
            TypeSample("titleLarge", titleLarge),
            TypeSample("titleMedium", titleMedium),
            TypeSample("titleSmall", titleSmall),
            TypeSample("bodyLarge", bodyLarge),
            TypeSample("bodyMedium", bodyMedium),
            TypeSample("bodySmall", bodySmall),
            TypeSample("labelLarge", labelLarge),
            TypeSample("labelMedium", labelMedium),
            TypeSample("labelSmall", labelSmall),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.xs)) {
        samples.forEach { (label, style) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(128.dp),
                )
                Text(
                    text = "Buenos Aires",
                    style = style,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun ButtonsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.sm)) {
        CvpButton(text = "Primary", onClick = {}, variant = CvpButtonVariant.Primary)
        CvpButton(text = "Secondary", onClick = {}, variant = CvpButtonVariant.Secondary)
        CvpButton(text = "Text", onClick = {}, variant = CvpButtonVariant.Text)
        CvpButton(text = "Cargando…", onClick = {}, isLoading = true)
        CvpButton(text = "Desactivado", onClick = {}, enabled = false)
    }
}

@Composable
private fun CardSection() {
    CvpCard {
        Text("Zona: Palermo", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(CvpTheme.spacing.xs))
        Text(
            "47 siniestros registrados entre 2022–2024 en el radio de esta zona.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChipsSection() {
    Row(horizontalArrangement = Arrangement.spacedBy(CvpTheme.spacing.sm)) {
        CvpChip(text = "Palermo")
        CvpChip(text = "Moto", onClick = {})
        CvpChip(text = "2023")
        CvpChip(text = "Intersección", onClick = {})
    }
}

@Composable
private fun IconBadgesSection() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(CvpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CvpIconBadge(icon = Icons.Filled.Place, backgroundColor = CvpTheme.colors.severityLow, size = 32.dp, iconSize = 16.dp)
        CvpIconBadge(icon = Icons.Filled.Place, backgroundColor = CvpTheme.colors.severityLow, size = 40.dp, iconSize = 20.dp)
        CvpIconBadge(icon = Icons.Filled.Place, backgroundColor = CvpTheme.colors.severityMedium, size = 48.dp, iconSize = 24.dp)
        CvpIconBadge(icon = Icons.Filled.Place, backgroundColor = CvpTheme.colors.severityHigh, size = 56.dp, iconSize = 28.dp)
    }
}

@Composable
private fun StatsSection() {
    CvpCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CvpStatItem(value = "342", label = "Siniestros")
            CvpStatItem(value = "18", label = "Zonas críticas")
            CvpStatItem(value = "4.2k", label = "Conductores")
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(name = "Showcase — Light", showBackground = true, showSystemUi = true)
@Preview(name = "Showcase — Dark", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DesignSystemShowcasePreview() {
    CvpTheme {
        DesignSystemShowcase(onBack = {})
    }
}
