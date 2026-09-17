package com.dailydashboard.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens

/**
 * Basis-kaart die alle vijf widget-types delen: surface-achtergrond, subtiele
 * rand en het vaste binnenpadding/radius uit het design system.
 */
@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = DashboardTheme.colors
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DashboardTokens.radiusCard),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(DashboardTokens.borderWidth, colors.surfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.padding(DashboardTokens.spacingCard)) {
            content()
        }
    }
}
