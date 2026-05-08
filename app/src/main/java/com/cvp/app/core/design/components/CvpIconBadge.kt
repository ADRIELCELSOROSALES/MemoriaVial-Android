package com.cvp.app.core.design.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpSpacing
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP circular icon badge. A filled circle containing an icon.
 *
 * Primarily used as severity indicators on the map and in data cards.
 * [backgroundColor] should be one of [CvpTheme.colors.severityLow],
 * [CvpTheme.colors.severityMedium], or [CvpTheme.colors.severityHigh]
 * for consistent semantic meaning across the app.
 */
@Composable
fun CvpIconBadge(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    tint: Color = Color.White,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = tint,
        )
    }
}

@Preview(name = "IconBadge — Light", showBackground = true)
@Preview(name = "IconBadge — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CvpIconBadgePreview() {
    CvpTheme {
        Row(
            modifier = Modifier.padding(CvpSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(CvpSpacing.sm),
        ) {
            CvpIconBadge(
                icon = Icons.Filled.Place,
                backgroundColor = CvpTheme.colors.severityLow,
                contentDescription = "Baja",
            )
            CvpIconBadge(
                icon = Icons.Filled.Place,
                backgroundColor = CvpTheme.colors.severityMedium,
                contentDescription = "Media",
            )
            CvpIconBadge(
                icon = Icons.Filled.Place,
                backgroundColor = CvpTheme.colors.severityHigh,
                contentDescription = "Alta",
                size = 48.dp,
                iconSize = 24.dp,
            )
        }
    }
}
