package com.cvp.app.core.design.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.cvpShimmer(shape: Shape = RoundedCornerShape(8.dp)): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    val baseColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val highlightColor = MaterialTheme.colorScheme.surfaceContainerHighest
    clip(shape).drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(translate - size.width, 0f),
                end = Offset(translate, 0f),
            ),
        )
    }
}

@Composable
fun CvpShimmer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(modifier = modifier.cvpShimmer(shape))
}
