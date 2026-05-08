package com.cvp.app.core.design.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvp.app.core.design.theme.CvpSpacing
import com.cvp.app.core.design.theme.CvpTheme

/** Variant of [CvpButton]. */
enum class CvpButtonVariant { Primary, Secondary, Text }

/**
 * CVP standard button.
 *
 * - [CvpButtonVariant.Primary]: main action on a screen. Filled, high emphasis.
 * - [CvpButtonVariant.Secondary]: supporting action. Outlined, medium emphasis.
 * - [CvpButtonVariant.Text]: inline or low-emphasis action. No container.
 *
 * When [isLoading] is true the label and icon are replaced by a spinner and
 * the button becomes non-interactive.
 */
@Composable
fun CvpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CvpButtonVariant = CvpButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    isLoading: Boolean = false,
) {
    val shape = RoundedCornerShape(12.dp)
    val contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
    val isEnabled = enabled && !isLoading

    val innerContent: @Composable RowScope.() -> Unit = {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = when (variant) {
                    CvpButtonVariant.Primary -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                },
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(CvpSpacing.xs))
                }
                Text(text = text)
            }
        }
    }

    when (variant) {
        CvpButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = isEnabled,
            shape = shape,
            contentPadding = contentPadding,
            content = innerContent,
        )
        CvpButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = isEnabled,
            shape = shape,
            contentPadding = contentPadding,
            content = innerContent,
        )
        CvpButtonVariant.Text -> TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = isEnabled,
            shape = shape,
            contentPadding = contentPadding,
            content = innerContent,
        )
    }
}

@Preview(name = "Buttons — Light", showBackground = true)
@Preview(name = "Buttons — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CvpButtonPreview() {
    CvpTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(CvpTheme.spacing.md)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(CvpTheme.spacing.sm),
        ) {
            CvpButton(text = "Primary", onClick = {}, variant = CvpButtonVariant.Primary)
            CvpButton(text = "Secondary", onClick = {}, variant = CvpButtonVariant.Secondary)
            CvpButton(text = "Text", onClick = {}, variant = CvpButtonVariant.Text)
            CvpButton(text = "Loading", onClick = {}, isLoading = true)
            CvpButton(text = "Disabled", onClick = {}, enabled = false)
        }
    }
}
