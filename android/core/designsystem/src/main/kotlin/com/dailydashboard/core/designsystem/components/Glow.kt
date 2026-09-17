package com.dailydashboard.core.designsystem.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Zachte gloed onder accent-elementen (chart-bars, gauge-arc) — geen harde schaduw.
 * Tekent een radiale gradient iets groter dan de composable zelf, uitdovend naar transparant.
 */
fun Modifier.dashboardGlow(color: Color, radius: Dp, alpha: Float = 0.35f): Modifier = this.drawBehind {
    val glowRadiusPx = radius.toPx()
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = glowRadiusPx,
        ),
        radius = glowRadiusPx,
        center = Offset(size.width / 2f, size.height / 2f),
    )
}
