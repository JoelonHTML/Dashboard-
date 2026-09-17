package com.dailydashboard.core.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.components.DashboardCard
import com.dailydashboard.core.designsystem.components.StatusPill
import com.dailydashboard.core.designsystem.theme.DashboardTheme

data class ListRowData(
    val marker: String,
    val primaryText: String,
    val secondaryText: String? = null,
    val statusText: String? = null,
    val statusPositive: Boolean = true,
)

/** Lijst zoals "Popular episodes": genummerd/geïconificeerd vakje links, status-pill rechts. */
@Composable
fun ListCard(
    title: String,
    items: List<ListRowData>,
    modifier: Modifier = Modifier,
    emptyText: String = "Niets te zien",
) {
    val colors = DashboardTheme.colors

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)

            if (items.isEmpty()) {
                Text(text = emptyText, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }

            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.accent.copy(alpha = 0.16f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = item.marker, style = MaterialTheme.typography.labelSmall, color = colors.accent)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.primaryText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                        )
                        if (item.secondaryText != null) {
                            Text(
                                text = item.secondaryText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary,
                            )
                        }
                    }

                    if (item.statusText != null) {
                        StatusPill(text = item.statusText, isPositive = item.statusPositive)
                    }
                }
            }
        }
    }
}
