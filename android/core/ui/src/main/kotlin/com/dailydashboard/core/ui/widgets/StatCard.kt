package com.dailydashboard.core.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.components.DashboardCard
import com.dailydashboard.core.designsystem.components.StatusPill
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.ui.animation.AnimatedCounterText
import com.dailydashboard.core.ui.animation.pulseOnChange
import com.dailydashboard.core.ui.animation.rememberPulseScale

/** Eén van de vier KPI-kaartjes: label, groot cijfer, optionele delta-pill. */
@Composable
fun StatCard(
    label: String,
    value: Float,
    modifier: Modifier = Modifier,
    unit: String = "",
    deltaText: String? = null,
    deltaIsPositive: Boolean = true,
    valueFormat: (Float) -> String = { "%.0f".format(it) },
) {
    val colors = DashboardTheme.colors
    val pulseScale = rememberPulseScale(triggerKey = value)

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedCounterText(
                    targetValue = value,
                    modifier = Modifier.pulseOnChange(pulseScale),
                    style = MaterialTheme.typography.displayLarge,
                    color = colors.textPrimary,
                    format = { "${valueFormat(it)}$unit" },
                )
            }
            if (deltaText != null) {
                StatusPill(text = deltaText, isPositive = deltaIsPositive)
            }
        }
    }
}
