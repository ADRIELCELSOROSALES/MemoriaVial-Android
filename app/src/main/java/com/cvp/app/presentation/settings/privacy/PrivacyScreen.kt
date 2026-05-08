package com.cvp.app.presentation.settings.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de privacidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            PrivacySection(
                title = "Qué datos usamos",
                body = "CVP accede a tu ubicación GPS únicamente mientras la app está en uso. " +
                    "Esta información se procesa localmente en tu dispositivo para calcular la " +
                    "proximidad a zonas de riesgo y no es enviada, almacenada ni compartida con " +
                    "ningún servidor externo. Al cerrar la app, no se retiene ningún dato de ubicación.",
            )

            HorizontalDivider()

            PrivacySection(
                title = "Permisos solicitados",
                body = "• Ubicación precisa (ACCESS_FINE_LOCATION): necesaria para calcular " +
                    "la distancia exacta a las zonas de riesgo registradas.\n\n" +
                    "• Ubicación aproximada (ACCESS_COARSE_LOCATION): utilizada como alternativa " +
                    "cuando el permiso preciso no está disponible.\n\n" +
                    "• Notificaciones (POST_NOTIFICATIONS): para enviar alertas cuando te aproximás " +
                    "a una zona de riesgo. Podés desactivarlas desde Configuración.",
            )

            HorizontalDivider()

            PrivacySection(
                title = "Datos de terceros",
                body = "CVP utiliza MapLibre (open-source) con tiles cartográficos de CARTO " +
                    "para renderizar el mapa. CARTO descarga los tiles de sus servidores. " +
                    "Tu ubicación no es compartida con CARTO; solo se transfieren las " +
                    "coordenadas de la vista del mapa para solicitar los tiles correspondientes. " +
                    "Consultá la política de privacidad de CARTO en carto.com/privacy.",
            )

            HorizontalDivider()

            PrivacySection(
                title = "Datos de siniestros viales",
                body = "Las zonas de riesgo son generadas a partir del dataset público del " +
                    "Observatorio de Movilidad y Seguridad Vial del Gobierno de la Ciudad " +
                    "de Buenos Aires. Estos datos no contienen información personal de ningún " +
                    "ciudadano y son accesibles libremente en data.buenosaires.gob.ar.",
            )

            HorizontalDivider()

            PrivacySection(
                title = "Tus derechos",
                body = "Podés revocar el permiso de ubicación en cualquier momento desde " +
                    "Ajustes → Aplicaciones → CVP → Permisos. Sin el permiso de ubicación, " +
                    "el mapa seguirá mostrando las zonas de riesgo pero no podrá centrar " +
                    "el mapa en tu posición ni enviar alertas de proximidad.\n\n" +
                    "Para revocar el permiso de notificaciones: " +
                    "Ajustes → Notificaciones → CVP → Desactivar.",
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
