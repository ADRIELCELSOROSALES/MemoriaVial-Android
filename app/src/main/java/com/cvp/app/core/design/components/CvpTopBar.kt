package com.cvp.app.core.design.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cvp.app.core.design.theme.CvpTheme

/**
 * CVP top app bar with a semi-transparent surface background.
 *
 * The transparency ensures the map or content visible behind the bar doesn't
 * feel obscured. Call with [windowInsets] = [WindowInsets(0)] when used inside
 * a [androidx.compose.material3.Scaffold] that already handles insets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvpTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    val transparentSurface = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        modifier = modifier,
        navigationIcon = { navigationIcon?.invoke() },
        actions = actions,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = transparentSurface,
            scrolledContainerColor = transparentSurface,
        ),
    )
}

@Preview(name = "TopBar — Light", showBackground = true)
@Preview(
    name = "TopBar — Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CvpTopBarPreview() {
    CvpTheme {
        CvpTopBar(
            title = "Palermo",
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            },
        )
    }
}
