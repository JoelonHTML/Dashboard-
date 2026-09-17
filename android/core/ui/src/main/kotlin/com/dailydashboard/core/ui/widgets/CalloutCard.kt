package com.dailydashboard.core.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens

/**
 * Solide accent-gekleurde kaart, zoals de groene "Webinars"-kaart —
 * voor de belangrijkste eye-catcher (volgende afspraak, top-herinnering).
 */
@Composable
fun CalloutCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = DashboardTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DashboardTokens.radiusCard),
        colors = CardDefaults.cardColors(containerColor = colors.accent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(DashboardTokens.spacingCard),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.background.copy(alpha = 0.75f),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = colors.background,
            )
        }
    }
}
