package com.cvp.app.core.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpSpacing
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP small chip. Use for tags, filter pills, or categorical labels.
 * Pass a non-null [onClick] to make it interactive (tappable filter).
 * Leave [onClick] null for display-only labels.
 */
@Composable
fun CvpChip(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable(role = Role.Button, onClick = onClick)
            } else Modifier
        ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = CvpSpacing.sm,
                vertical = CvpSpacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CvpSpacing.xs),
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Chip — Light", showBackground = true)
@Preview(
    name = "Chip — Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CvpChipPreview() {
    CvpTheme {
        Row(
            modifier = Modifier.padding(CvpSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(CvpSpacing.sm),
        ) {
            CvpChip(text = "Palermo")
            CvpChip(text = "2023–2024")
            CvpChip(text = "Intersección", onClick = {})
        }
    }
}
