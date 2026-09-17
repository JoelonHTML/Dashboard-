package com.dailydashboard.core.ui.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.components.DashboardCard
import com.dailydashboard.core.designsystem.theme.DashboardTheme

/** Halfronde arc met icoon in het midden, zoals de "Audience satisfaction"-gauge. */
@Composable
fun GaugeCard(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    centerLabel: String = "${(progress * 100).toInt()}%",
) {
    val colors = DashboardTheme.colors
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "gaugeProgress",
    )

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Canvas(modifier = Modifier.size(160.dp, 90.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height * 2 - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    drawArc(
                        color = colors.surfaceBorder,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = colors.accent,
                        startAngle = 180f,
                        sweepAngle = 180f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (icon != null) {
                        Icon(imageVector = icon, contentDescription = null, tint = colors.accent)
                    }
                    Text(
                        text = centerLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                    )
                }
            }
        }
    }
}
