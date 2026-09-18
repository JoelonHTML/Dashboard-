package com.dailydashboard.core.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.components.DashboardCard
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Vloeiende lijngrafiek voor een doorlopende meting (bv. CPU-load%). Elk punt heeft zijn eigen
 * [Animatable] — omdat het historie-venster elke sync met één punt opschuift, animeert elke
 * refresh alle punten soepel naar hun nieuwe waarde, wat als een continu "lopende" grafiek oogt.
 */
@Composable
fun ChartCard(
    title: String,
    values: List<Float>,
    modifier: Modifier = Modifier,
    unit: String = "",
) {
    val colors = DashboardTheme.colors
    val pointCount = values.size.coerceAtLeast(2)

    val animatables = remember(pointCount) {
        List(pointCount) { index -> Animatable(values.getOrElse(index) { 0f }) }
    }

    LaunchedEffect(values) {
        values.forEachIndexed { index, value ->
            if (index < animatables.size) {
                launch { animatables[index].animateTo(value, animationSpec = tween(durationMillis = 450)) }
            }
        }
    }

    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Text(
                    text = "${animatables.last().value.roundToInt()}$unit",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.accent,
                )
            }
            Spacer(modifier = Modifier.height(DashboardTokens.spacingGrid))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            ) {
                val stepX = size.width / (animatables.size - 1)
                fun yFor(value: Float) = size.height * (1f - (value / maxValue).coerceIn(0f, 1f))

                val linePath = Path().apply {
                    animatables.forEachIndexed { index, animatable ->
                        val x = index * stepX
                        val y = yFor(animatable.value)
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(colors.accent.copy(alpha = 0.35f), colors.accent.copy(alpha = 0f)),
                    ),
                )
                drawPath(
                    path = linePath,
                    color = colors.accent,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )

                val lastX = (animatables.size - 1) * stepX
                val lastY = yFor(animatables.last().value)
                drawCircle(color = colors.accent, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
            }
        }
    }
}
