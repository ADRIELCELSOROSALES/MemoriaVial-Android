package com.cvp.app.core.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cvp.app.core.design.theme.CvpSpacing
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP stat item. Displays a prominent [value] with a supporting [label] below.
 *
 * Use in dashboards or summary cards where numeric data is the primary content.
 * The [value] renders in [MaterialTheme.typography.displaySmall] to ensure it
 * reads as the hero element even at small viewport sizes.
 */
@Composable
fun CvpStatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CvpSpacing.xxs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "StatItem — Light", showBackground = true)
@Preview(
    name = "StatItem — Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CvpStatItemPreview() {
    CvpTheme {
        Row(
            modifier = Modifier.padding(CvpSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(CvpSpacing.xl),
        ) {
            CvpStatItem(value = "342", label = "Siniestros")
            CvpStatItem(value = "18", label = "Zonas críticas")
            CvpStatItem(value = "4.2k", label = "Usuarios")
        }
    }
}
