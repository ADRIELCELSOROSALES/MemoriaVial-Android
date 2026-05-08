package com.cvp.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cvp.app.BuildConfig
import com.cvp.app.core.design.components.CvpDivider
import com.cvp.app.domain.model.ThemeMode
import com.cvp.app.presentation.settings.components.SectionHeader
import com.cvp.app.presentation.settings.components.SettingItem
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }
    var sliderPosition by remember(settings.alertRadiusMeters) {
        mutableFloatStateOf(settings.alertRadiusMeters.toFloat())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.navigationBarsPadding(),
        ) {

            // ── Alertas ───────────────────────────────────────────────────
            item { SectionHeader("Alertas") }

            item {
                SettingItem(
                    title = "Notificaciones",
                    subtitle = "Recibir avisos al acercarte a zonas registradas",
                    trailing = {
                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        )
                    },
                )
            }

            item {
                SettingItem(
                    title = "Vibración",
                    subtitle = "Vibrar junto con la notificación",
                    enabled = settings.notificationsEnabled,
                    trailing = {
                        Switch(
                            checked = settings.vibrationEnabled,
                            onCheckedChange = { viewModel.setVibrationEnabled(it) },
                            enabled = settings.notificationsEnabled,
                        )
                    },
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Distancia de alerta", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${sliderPosition.roundToInt()} metros",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        onValueChangeFinished = { viewModel.setAlertRadius(sliderPosition.roundToInt()) },
                        valueRange = 50f..300f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        listOf(50, 100, 150, 200, 250, 300).forEach { mark ->
                            Text(
                                text = "$mark",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // ── Apariencia ────────────────────────────────────────────────
            item { CvpDivider() }
            item { SectionHeader("Apariencia") }

            item {
                SettingItem(
                    title = "Tema",
                    subtitle = settings.themeMode.displayName(),
                    onClick = { showThemeDialog = true },
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }

            item {
                SettingItem(
                    title = "Mostrar zonas en el mapa",
                    subtitle = "Visualizar las zonas de riesgo sobre el mapa",
                    trailing = {
                        Switch(
                            checked = settings.showZones,
                            onCheckedChange = { viewModel.setShowZones(it) },
                        )
                    },
                )
            }

            // ── Información ───────────────────────────────────────────────
            item { CvpDivider() }
            item { SectionHeader("Información") }

            item {
                SettingItem(
                    title = "Acerca de CVP",
                    subtitle = "Filosofía y fuentes de datos",
                    onClick = onNavigateToAbout,
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }

            item {
                SettingItem(
                    title = "Política de privacidad",
                    subtitle = "Qué datos usamos y cómo los protegemos",
                    onClick = onNavigateToPrivacy,
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }

            item {
                SettingItem(
                    title = "Versión",
                    trailing = {
                        Text(
                            text = BuildConfig.VERSION_NAME,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        }
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            current = settings.themeMode,
            onSelect = {
                viewModel.setThemeMode(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false },
        )
    }
}

@Composable
private fun ThemePickerDialog(
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tema") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RadioButton(
                            selected = mode == current,
                            onClick = { onSelect(mode) },
                        )
                        Text(
                            text = mode.displayName(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

private fun ThemeMode.displayName(): String = when (this) {
    ThemeMode.SYSTEM -> "Sistema"
    ThemeMode.LIGHT -> "Claro"
    ThemeMode.DARK -> "Oscuro"
}
