package com.dailydashboard.core.ui.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

/** Cijfers tellen op naar hun waarde in plaats van meteen te verschijnen (600ms). */
@Composable
fun AnimatedCounterText(
    targetValue: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    format: (Float) -> String = { "%.0f".format(it) },
) {
    val animated by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 600),
        label = "animatedCounter",
    )
    Text(text = format(animated), modifier = modifier, style = style, color = color)
}

/** Lichte pulse op live-updatende waardes, bv. een FS25-spelersaantal dat verandert. */
@Composable
fun rememberPulseScale(triggerKey: Any?): Float {
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 220),
        label = "pulseScale",
    )
    LaunchedEffect(triggerKey) {
        if (triggerKey != null) {
            scale = 1.08f
            delay(160)
            scale = 1f
        }
    }
    return animatedScale
}

fun Modifier.pulseOnChange(scale: Float): Modifier = this.scale(scale)
