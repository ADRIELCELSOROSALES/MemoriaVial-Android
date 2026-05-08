package com.cvp.app.core.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP standard card. Use as a container for grouped content items when visual
 * separation from the background is needed. Prefer over a raw [androidx.compose.material3.Surface]
 * when the content represents a self-contained data block.
 */
@Composable
fun CvpCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(CvpTheme.spacing.md),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Preview(name = "Card — Light", showBackground = true)
@Preview(
    name = "Card — Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CvpCardPreview() {
    CvpTheme {
        CvpCard(modifier = Modifier.padding(CvpTheme.spacing.md)) {
            Text("Card title", style = MaterialTheme.typography.titleMedium)
            Text("Card body text", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
