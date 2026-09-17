package com.dailydashboard.core.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.components.DashboardCard
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens

/** Staafgrafiek zoals de "Audience"-grafiek: groene gradient met gloed aan de top. */
@Composable
fun ChartCard(
    title: String,
    values: List<Float>,
    modifier: Modifier = Modifier,
) {
    val colors = DashboardTheme.colors
    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(DashboardTokens.spacingGrid))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            ) {
                if (values.isEmpty()) return@Canvas

                val barSpacing = 8.dp.toPx()
                val barWidth = (size.width - barSpacing * (values.size - 1)) / values.size

                values.forEachIndexed { index, value ->
                    val barHeightFraction = (value / maxValue).coerceIn(0f, 1f)
                    val barHeight = size.height * barHeightFraction
                    val left = index * (barWidth + barSpacing)
                    val top = size.height - barHeight

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(colors.accent, colors.accent.copy(alpha = 0.35f)),
                            startY = top,
                            endY = size.height,
                        ),
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                    )

                    // Zachte gloed aan de top van elke bar.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.accent.copy(alpha = 0.4f), colors.accent.copy(alpha = 0f)),
                            center = Offset(left + barWidth / 2, top),
                            radius = barWidth,
                        ),
                        radius = barWidth,
                        center = Offset(left + barWidth / 2, top),
                    )
                }
            }
        }
    }
}
