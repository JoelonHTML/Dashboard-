package com.dailydashboard.app.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.designsystem.theme.DashboardTheme

/** Zichtbaar zodra er een update speelt — onzichtbaar bij Idle/Checking/UpToDate. */
@Composable
fun UpdateBanner(
    state: UpdateState,
    onDownload: (UpdateInfo) -> Unit,
    onInstall: (apkPath: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = DashboardTheme.colors

    when (state) {
        is UpdateState.Available -> Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Update ${state.info.versionLabel} beschikbaar",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { onDownload(state.info) }) { Text("Downloaden") }
            IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Negeren", tint = colors.textSecondary) }
        }

        is UpdateState.Downloading -> Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Update downloaden… ${(state.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.weight(1f))
        }

        is UpdateState.ReadyToInstall -> Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Update ${state.info.versionLabel} gedownload",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { onInstall(state.apkPath) }) { Text("Installeren") }
            IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Negeren", tint = colors.textSecondary) }
        }

        is UpdateState.Error -> Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Kon niet zoeken naar updates: ${state.message}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.alert,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Negeren", tint = colors.textSecondary) }
        }

        UpdateState.Idle, UpdateState.Checking, UpdateState.UpToDate -> Unit
    }
}
