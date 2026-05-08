package com.cvp.app.core.design.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP divider. A subtle 1 dp horizontal rule using [MaterialTheme.colorScheme.outlineVariant].
 * Prefer over [HorizontalDivider] directly to ensure consistent color usage across the app.
 */
@Composable
fun CvpDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@Preview(name = "Divider — Light", showBackground = true)
@Preview(
    name = "Divider — Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CvpDividerPreview() {
    CvpTheme {
        CvpDivider()
    }
}
