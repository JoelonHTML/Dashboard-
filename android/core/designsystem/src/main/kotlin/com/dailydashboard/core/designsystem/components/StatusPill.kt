package com.dailydashboard.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens

/** Badge/pill zoals gebruikt in lijst-rijen (status) en KPI-kaarten (delta). */
@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
) {
    val colors = DashboardTheme.colors
    val tint = if (isPositive) colors.accent else colors.alert

    Text(
        text = text,
        modifier = modifier
            .background(tint.copy(alpha = 0.16f), RoundedCornerShape(DashboardTokens.radiusChip))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = tint,
        style = MaterialTheme.typography.labelSmall,
    )
}
